package br.ufrrj.si.web2.repositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import br.ufrrj.si.web2.model.AtuacaoComissao;
import br.ufrrj.si.web2.model.AtuacaoComissao.PerfilComissao;
import br.ufrrj.si.web2.model.Discente;
import br.ufrrj.si.web2.model.MembroComissao;
import br.ufrrj.si.web2.model.Solicitacao;
import br.ufrrj.si.web2.model.StatusSolicitacao;
import br.ufrrj.si.web2.model.TipoHoras;

/**
 * Simula a persistência de dados em memória para o Entregável 2.
 * Guarda os discentes, solicitações e membros das comissões em mapas na memória,
 * já inicializados com dados mockados para facilitar os testes no navegador.
 */
public class RepositorioMemoria {

    private static final RepositorioMemoria INSTANCIA = new RepositorioMemoria();

    private final Map<String, Discente> discentesPorMatricula = new LinkedHashMap<>();
    private final Map<Long, Solicitacao> solicitacoesPorId = new LinkedHashMap<>();
    private final Map<String, MembroComissao> membrosPorUsername = new LinkedHashMap<>();

    private final AtomicLong geradorIdDiscente = new AtomicLong(100);
    private final AtomicLong geradorIdSolicitacao = new AtomicLong(100);
    private final AtomicLong geradorIdMembro = new AtomicLong(1);
    private final AtomicLong geradorIdAtuacao = new AtomicLong(10);

    private RepositorioMemoria() {
        carregarDadosIniciais();
    }

    public static RepositorioMemoria getInstancia() {
        return INSTANCIA;
    }

    /**
     * Carrega os 5 perfis de comissão/gestão, 1 conta com mandato expirado
     * e o discente Carlos Eduardo da Silva com solicitações de exemplo (baseado nos protótipos do professor).
     */
    private void carregarDadosIniciais() {
        // 1. Membro da Comissão de Extensão (vigente)
        MembroComissao profCarlos = criarMembro("cmendes", "123456", "Prof. Carlos Mendes",
                PerfilComissao.COMISSAO_EXTENSAO, LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(9), true);

        // 2. Membro da Comissão de Atividades Complementares (vigente)
        MembroComissao profaAna = criarMembro("apatricia", "123456", "Profa. Ana Patrícia",
                PerfilComissao.COMISSAO_COMPLEMENTAR, LocalDate.now().minusMonths(4), LocalDate.now().plusMonths(8), true);

        // 3. Membro da Comissão de Estágio (vigente)
        criarMembro("rmarques", "123456", "Prof. Roberto Marques",
                PerfilComissao.COMISSAO_ESTAGIO, LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(10), true);

        // 4. Coordenador do Curso (vigente, mandato em aberto)
        criarMembro("tfranca", "admin123", "Prof. Tiago França",
                PerfilComissao.COORDENADOR, LocalDate.now().minusYears(1), null, true);

        // 5. Secretário do Curso (vigente - perfil somente consulta)
        criarMembro("msecretaria", "123456", "Marcos Vinícius (Secretaria)",
                PerfilComissao.SECRETARIO, LocalDate.now().minusYears(2), null, true);

        // 6. Membro com mandato EXPIRADO (para testar bloqueio de login por vigência)
        criarMembro("hsouza", "123456", "Profa. Helena Souza",
                PerfilComissao.COMISSAO_ESTAGIO, LocalDate.now().minusYears(2), LocalDate.now().minusDays(1), true);

        // Discente de exemplo igual ao protótipo do professor
        Discente carlos = buscarOuCadastrarDiscente("Carlos Eduardo da Silva", "2023019823");

        // Solicitação #88: Já concluída em Atividades Complementares (15h) - servirá para acionar o Alerta de Duplicidade Cruzada!
        Solicitacao sol88 = new Solicitacao();
        sol88.setId(88L);
        sol88.setDiscente(carlos);
        sol88.setTipoHoras(TipoHoras.COMPLEMENTAR);
        sol88.setNomeDocumento("Certificado Ação Social Lixo Zero.pdf");
        sol88.setCaminhoArquivoComprovante("mock_comprovante_88.pdf");
        sol88.setHorasSolicitadas(20);
        sol88.setObservacaoDiscente("Participação voluntária no mutirão Lixo Zero.");
        sol88.registrarCriacaoInicial(true);
        sol88.emitirParecerDaComissao(
                profaAna.getAtuacaoVigente(),
                15,
                "Aprovado 15h como atividade cultural/social.",
                "Certificado conferido com a lista de presença do evento.",
                null
        );
        sol88.atualizarStatusPorDiscente(StatusSolicitacao.ACEITA, "Discente aceitou as 15h concedidas.");
        carlos.getSolicitacoes().add(sol88);
        solicitacoesPorId.put(sol88.getId(), sol88);

        // Solicitação #99: Estado ENVIADA para Estágio
        Solicitacao sol99 = new Solicitacao();
        sol99.setId(99L);
        sol99.setDiscente(carlos);
        sol99.setTipoHoras(TipoHoras.ESTAGIO);
        sol99.setNomeDocumento("Relatório Parcial Estágio Empresa X.pdf");
        sol99.setCaminhoArquivoComprovante("mock_comprovante_99.pdf");
        sol99.setHorasSolicitadas(150);
        sol99.setObservacaoDiscente("Relatório referente ao primeiro semestre de estágio supervisionado.");
        sol99.registrarCriacaoInicial(true);
        carlos.getSolicitacoes().add(sol99);
        solicitacoesPorId.put(sol99.getId(), sol99);

        // Solicitação #101: Estado AVALIADA em Extensão (com o MESMO documento da #88 para demonstrar duplicidade cruzada)
        Solicitacao sol101 = new Solicitacao();
        sol101.setId(101L);
        sol101.setDiscente(carlos);
        sol101.setTipoHoras(TipoHoras.EXTENSAO);
        sol101.setNomeDocumento("Certificado Ação Social Lixo Zero.pdf");
        sol101.setCaminhoArquivoComprovante("mock_comprovante_101.pdf");
        sol101.setHorasSolicitadas(30);
        sol101.setObservacaoDiscente("Estou enviando o certificado da ação comunitária para solicitação de horas de extensão.");
        sol101.registrarCriacaoInicial(true);
        sol101.emitirParecerDaComissao(
                profCarlos.getAtuacaoVigente(),
                20,
                "Deferido parcialmente. Máximo de 20h para oficinas curtas.",
                "Atenção: verificar se o discente já usou este certificado em Atividades Complementares (#88).",
                null
        );
        carlos.getSolicitacoes().add(sol101);
        solicitacoesPorId.put(sol101.getId(), sol101);

        // Solicitação #104: Estado CRIADA (rascunho que permite Editar, Enviar, Cancelar ou Excluir)
        Solicitacao sol104 = new Solicitacao();
        sol104.setId(104L);
        sol104.setDiscente(carlos);
        sol104.setTipoHoras(TipoHoras.COMPLEMENTAR);
        sol104.setNomeDocumento("Certificado Curso Python 20h.pdf");
        sol104.setCaminhoArquivoComprovante("mock_comprovante_104.pdf");
        sol104.setHorasSolicitadas(20);
        sol104.setObservacaoDiscente("Curso concluído na plataforma institucional.");
        sol104.registrarCriacaoInicial(false);
        carlos.getSolicitacoes().add(sol104);
        solicitacoesPorId.put(sol104.getId(), sol104);

        geradorIdSolicitacao.set(105L);
    }

    private MembroComissao criarMembro(String username, String senha, String nomeCompleto,
                                       PerfilComissao perfil, LocalDate inicio, LocalDate validade, boolean ativo) {
        MembroComissao membro = new MembroComissao();
        membro.setId(geradorIdMembro.getAndIncrement());
        membro.setUsername(username);
        membro.setSenha(senha);
        membro.setNomeCompleto(nomeCompleto);

        AtuacaoComissao atuacao = new AtuacaoComissao();
        atuacao.setId(geradorIdAtuacao.getAndIncrement());
        atuacao.setMembro(membro);
        atuacao.setPerfil(perfil);
        atuacao.setDataInicio(inicio);
        atuacao.setDataValidade(validade);
        atuacao.setAtivo(ativo);

        membro.getAtuacoes().add(atuacao);
        membrosPorUsername.put(username.toLowerCase(), membro);
        return membro;
    }

    /**
     * Regra do Discente: Se a matrícula não existir, cadastra automaticamente.
     * Se já existir, retorna o discente existente (sem alterar o cadastro).
     */
    public synchronized Discente buscarOuCadastrarDiscente(String nomeCompleto, String matricula) {
        String matLimpa = matricula != null ? matricula.trim() : "";
        Discente existente = discentesPorMatricula.get(matLimpa);
        if (existente != null) {
            return existente;
        }
        Discente novo = new Discente(geradorIdDiscente.getAndIncrement(), nomeCompleto.trim(), matLimpa);
        discentesPorMatricula.put(matLimpa, novo);
        return novo;
    }

    public synchronized Discente buscarDiscentePorMatricula(String matricula) {
        if (matricula == null) {
            return null;
        }
        return discentesPorMatricula.get(matricula.trim());
    }

    public synchronized MembroComissao buscarMembroPorUsername(String username) {
        if (username == null) {
            return null;
        }
        return membrosPorUsername.get(username.trim().toLowerCase());
    }

    public synchronized Solicitacao salvarNovaSolicitacao(Solicitacao solicitacao, boolean enviarParaComissao) {
        if (solicitacao.getId() == null) {
            solicitacao.setId(geradorIdSolicitacao.getAndIncrement());
        }
        solicitacao.registrarCriacaoInicial(enviarParaComissao);
        solicitacoesPorId.put(solicitacao.getId(), solicitacao);
        if (solicitacao.getDiscente() != null && !solicitacao.getDiscente().getSolicitacoes().contains(solicitacao)) {
            solicitacao.getDiscente().getSolicitacoes().add(solicitacao);
        }
        return solicitacao;
    }

    public synchronized Solicitacao buscarSolicitacaoPorId(Long id) {
        return solicitacoesPorId.get(id);
    }

    /**
     * Regra: Apenas solicitações no status CRIADA podem ser excluídas fisicamente.
     */
    public synchronized void excluirSolicitacao(Long idSolicitacao, Discente discenteLogado) {
        Solicitacao sol = solicitacoesPorId.get(idSolicitacao);
        if (sol == null) {
            throw new IllegalArgumentException("Solicitação não encontrada.");
        }
        if (discenteLogado == null || !sol.getDiscente().getMatricula().equals(discenteLogado.getMatricula())) {
            throw new IllegalStateException("Você só pode excluir suas próprias solicitações.");
        }
        if (!sol.getStatus().permiteExclusao()) {
            throw new IllegalStateException("Operação bloqueada: apenas solicitações no status CRIADA podem ser excluídas.");
        }
        solicitacoesPorId.remove(idSolicitacao);
        sol.getDiscente().getSolicitacoes().remove(sol);
    }

    /**
     * Filtra as solicitações que um perfil de comissão/coordenação/secretaria pode ver.
     * Regras:
     * - CRIADA: visível apenas pelo próprio discente (nunca aparece para as comissões).
     * - CANCELADA: não é exibida nas filas das comissões; apenas o Coordenador (e Secretário em consulta) visualiza para restauração.
     * - Comissão específica só vê solicitações do seu TipoHoras.
     */
    public synchronized List<Solicitacao> listarSolicitacoesVisiveisParaComissao(
            AtuacaoComissao atuacao, String termoBuscaDiscente, String filtroTipoHoras) {

        List<Solicitacao> resultado = new ArrayList<>();
        if (atuacao == null) {
            return resultado;
        }

        String termo = (termoBuscaDiscente != null) ? termoBuscaDiscente.trim().toLowerCase() : "";
        String tipoFiltro = (filtroTipoHoras != null) ? filtroTipoHoras.trim() : "TODAS";

        for (Solicitacao s : solicitacoesPorId.values()) {
            // Regra: CRIADA é visível apenas pelo próprio discente
            if (s.getStatus() == StatusSolicitacao.CRIADA) {
                continue;
            }
            // Regra: CANCELADA não aparece nas filas da comissão (apenas Coordenador pode restaurar)
            if (s.getStatus() == StatusSolicitacao.CANCELADA && !atuacao.isCoordenador()) {
                continue;
            }
            // Regra: Cada comissão só vê solicitações direcionadas para ela
            if (!atuacao.podeVisualizarTipo(s.getTipoHoras())) {
                continue;
            }
            // Filtro opcional de TipoHoras na tela (para Coordenador e Secretário)
            if (!tipoFiltro.isEmpty() && !"TODAS".equalsIgnoreCase(tipoFiltro)) {
                if (!s.getTipoHoras().name().equalsIgnoreCase(tipoFiltro)) {
                    continue;
                }
            }
            // Filtro de pesquisa por nome do discente ou matrícula
            if (!termo.isEmpty()) {
                String nomeAluno = s.getDiscente().getNomeCompleto().toLowerCase();
                String matAluno = s.getDiscente().getMatricula().toLowerCase();
                String docNome = s.getNomeDocumento().toLowerCase();
                if (!nomeAluno.contains(termo) && !matAluno.contains(termo) && !docNome.contains(termo)) {
                    continue;
                }
            }
            resultado.add(s);
        }
        return resultado;
    }

    /**
     * Busca outras solicitações do mesmo discente (em qualquer comissão) que tenham documento semelhante.
     */
    public synchronized List<Solicitacao> buscarDuplicidadesCruzadas(Solicitacao solicitacaoAlvo) {
        List<Solicitacao> duplicadas = new ArrayList<>();
        if (solicitacaoAlvo == null || solicitacaoAlvo.getDiscente() == null) {
            return duplicadas;
        }

        for (Solicitacao outra : solicitacaoAlvo.getDiscente().getSolicitacoes()) {
            if (outra.getStatus() != StatusSolicitacao.CRIADA
                    && outra.getStatus() != StatusSolicitacao.CANCELADA
                    && solicitacaoAlvo.temDocumentoSemelhante(outra)) {
                duplicadas.add(outra);
            }
        }
        return duplicadas;
    }
}

