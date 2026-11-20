package br.ufrrj.si.web2.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.ufrrj.si.web2.model.AtuacaoComissao;
import br.ufrrj.si.web2.model.AtuacaoComissao.PerfilComissao;
import br.ufrrj.si.web2.model.Discente;
import br.ufrrj.si.web2.model.MembroComissao;
import br.ufrrj.si.web2.model.Solicitacao;
import br.ufrrj.si.web2.model.StatusSolicitacao;
import br.ufrrj.si.web2.model.TipoHoras;

// Classe simples que guarda os dados em listas estáticas na memória para o Entregável 2
public class BancoMemoria {

    public static List<Discente> discentes = new ArrayList<>();
    public static List<Solicitacao> solicitacoes = new ArrayList<>();
    public static List<MembroComissao> membros = new ArrayList<>();
    public static long proximoIdSolicitacao = 105;
    public static long proximoIdDiscente = 2;

    static {
        // 1. Membro Comissão de Extensão
        MembroComissao m1 = new MembroComissao();
        m1.setId(1L);
        m1.setUsername("cmendes");
        m1.setSenha("123456");
        m1.setNomeCompleto("Prof. Carlos Mendes");
        AtuacaoComissao a1 = new AtuacaoComissao();
        a1.setId(1L);
        a1.setMembro(m1);
        a1.setPerfil(PerfilComissao.COMISSAO_EXTENSAO);
        a1.setDataInicio(LocalDate.now().minusMonths(2));
        a1.setDataValidade(LocalDate.now().plusMonths(10));
        a1.setAtivo(true);
        m1.getAtuacoes().add(a1);
        membros.add(m1);

        // 2. Membro Comissão de Atividades Complementares
        MembroComissao m2 = new MembroComissao();
        m2.setId(2L);
        m2.setUsername("apatricia");
        m2.setSenha("123456");
        m2.setNomeCompleto("Profa. Ana Patrícia");
        AtuacaoComissao a2 = new AtuacaoComissao();
        a2.setId(2L);
        a2.setMembro(m2);
        a2.setPerfil(PerfilComissao.COMISSAO_COMPLEMENTAR);
        a2.setDataInicio(LocalDate.now().minusMonths(2));
        a2.setDataValidade(LocalDate.now().plusMonths(10));
        a2.setAtivo(true);
        m2.getAtuacoes().add(a2);
        membros.add(m2);

        // 3. Membro Comissão de Estágio
        MembroComissao m3 = new MembroComissao();
        m3.setId(3L);
        m3.setUsername("rmarques");
        m3.setSenha("123456");
        m3.setNomeCompleto("Prof. Roberto Marques");
        AtuacaoComissao a3 = new AtuacaoComissao();
        a3.setId(3L);
        a3.setMembro(m3);
        a3.setPerfil(PerfilComissao.COMISSAO_ESTAGIO);
        a3.setDataInicio(LocalDate.now().minusMonths(2));
        a3.setDataValidade(LocalDate.now().plusMonths(10));
        a3.setAtivo(true);
        m3.getAtuacoes().add(a3);
        membros.add(m3);

        // 4. Coordenador
        MembroComissao m4 = new MembroComissao();
        m4.setId(4L);
        m4.setUsername("tfranca");
        m4.setSenha("admin123");
        m4.setNomeCompleto("Prof. Tiago França");
        AtuacaoComissao a4 = new AtuacaoComissao();
        a4.setId(4L);
        a4.setMembro(m4);
        a4.setPerfil(PerfilComissao.COORDENADOR);
        a4.setDataInicio(LocalDate.now().minusYears(1));
        a4.setDataValidade(null);
        a4.setAtivo(true);
        m4.getAtuacoes().add(a4);
        membros.add(m4);

        // 5. Secretário
        MembroComissao m5 = new MembroComissao();
        m5.setId(5L);
        m5.setUsername("msecretaria");
        m5.setSenha("123456");
        m5.setNomeCompleto("Marcos Vinícius");
        AtuacaoComissao a5 = new AtuacaoComissao();
        a5.setId(5L);
        a5.setMembro(m5);
        a5.setPerfil(PerfilComissao.SECRETARIO);
        a5.setDataInicio(LocalDate.now().minusYears(1));
        a5.setDataValidade(null);
        a5.setAtivo(true);
        m5.getAtuacoes().add(a5);
        membros.add(m5);

        // 6. Ex-membro com mandato vencido (para testar bloqueio de login)
        MembroComissao m6 = new MembroComissao();
        m6.setId(6L);
        m6.setUsername("hsouza");
        m6.setSenha("123456");
        m6.setNomeCompleto("Profa. Helena Souza");
        AtuacaoComissao a6 = new AtuacaoComissao();
        a6.setId(6L);
        a6.setMembro(m6);
        a6.setPerfil(PerfilComissao.COMISSAO_ESTAGIO);
        a6.setDataInicio(LocalDate.now().minusYears(2));
        a6.setDataValidade(LocalDate.now().minusDays(1));
        a6.setAtivo(true);
        m6.getAtuacoes().add(a6);
        membros.add(m6);

        // Discente de exemplo do protótipo do professor
        Discente aluno = new Discente();
        aluno.setId(1L);
        aluno.setNomeCompleto("Carlos Eduardo da Silva");
        aluno.setMatricula("2023019823");
        discentes.add(aluno);

        // Solicitação #88 (já concluída em Complementares - usada para mostrar alerta de duplicidade com a #101)
        Solicitacao s88 = new Solicitacao();
        s88.setId(88L);
        s88.setDiscente(aluno);
        s88.setTipoHoras(TipoHoras.COMPLEMENTAR);
        s88.setNomeDocumento("Certificado Ação Social Lixo Zero.pdf");
        s88.setCaminhoArquivoComprovante("/uploads/sgha/lixo_zero_88.pdf");
        s88.setHorasSolicitadas(20);
        s88.setHorasConcedidas(15);
        s88.setObservacaoDiscente("Participação na ação social.");
        s88.setObservacaoParecerPublico("Aprovado 15h como atividade cultural/social.");
        s88.atualizarStatusPorDiscente(StatusSolicitacao.CRIADA, "Criada pelo discente");
        s88.atualizarStatusPorDiscente(StatusSolicitacao.ENVIADA, "Enviada para comissão");
        s88.atualizarStatusPorComissao(StatusSolicitacao.AVALIADA, "Aprovado 15h", a2);
        s88.atualizarStatusPorDiscente(StatusSolicitacao.ACEITA, "Aceito pelo discente");
        aluno.getSolicitacoes().add(s88);
        solicitacoes.add(s88);

        // Solicitação #99 (ENVIADA para Estágio)
        Solicitacao s99 = new Solicitacao();
        s99.setId(99L);
        s99.setDiscente(aluno);
        s99.setTipoHoras(TipoHoras.ESTAGIO);
        s99.setNomeDocumento("Relatório Parcial Estágio Empresa X.pdf");
        s99.setCaminhoArquivoComprovante("/uploads/sgha/relatorio_99.pdf");
        s99.setHorasSolicitadas(150);
        s99.setObservacaoDiscente("Relatório do estágio.");
        s99.atualizarStatusPorDiscente(StatusSolicitacao.CRIADA, "Criada pelo discente");
        s99.atualizarStatusPorDiscente(StatusSolicitacao.ENVIADA, "Enviada para comissão");
        aluno.getSolicitacoes().add(s99);
        solicitacoes.add(s99);

        // Solicitação #101 (AVALIADA em Extensão - mesmo documento da #88 para demonstrar duplicidade)
        Solicitacao s101 = new Solicitacao();
        s101.setId(101L);
        s101.setDiscente(aluno);
        s101.setTipoHoras(TipoHoras.EXTENSAO);
        s101.setNomeDocumento("Certificado Ação Social Lixo Zero.pdf");
        s101.setCaminhoArquivoComprovante("/uploads/sgha/lixo_zero_101.pdf");
        s101.setHorasSolicitadas(30);
        s101.setHorasConcedidas(20);
        s101.setObservacaoDiscente("Estou enviando o certificado da ação comunitária para solicitação de horas de extensão.");
        s101.setObservacaoParecerPublico("Deferido parcialmente. Máximo de 20h para oficinas curtas.");
        s101.setObservacaoAdministrativa("Verificar duplicidade com a solicitação #88 de Complementares.");
        s101.atualizarStatusPorDiscente(StatusSolicitacao.CRIADA, "Criada pelo discente");
        s101.atualizarStatusPorDiscente(StatusSolicitacao.ENVIADA, "Enviada para comissão");
        s101.atualizarStatusPorComissao(StatusSolicitacao.AVALIADA, "Deferido 20h", a1);
        aluno.getSolicitacoes().add(s101);
        solicitacoes.add(s101);

        // Solicitação #104 (CRIADA - rascunho)
        Solicitacao s104 = new Solicitacao();
        s104.setId(104L);
        s104.setDiscente(aluno);
        s104.setTipoHoras(TipoHoras.COMPLEMENTAR);
        s104.setNomeDocumento("Certificado Curso Python 20h.pdf");
        s104.setCaminhoArquivoComprovante("/uploads/sgha/python_104.pdf");
        s104.setHorasSolicitadas(20);
        s104.setObservacaoDiscente("Curso de Python.");
        s104.atualizarStatusPorDiscente(StatusSolicitacao.CRIADA, "Rascunho criado pelo discente");
        aluno.getSolicitacoes().add(s104);
        solicitacoes.add(s104);
    }

    public static Discente buscarOuCriarDiscente(String nome, String matricula) {
        for (Discente d : discentes) {
            if (d.getMatricula().equals(matricula.trim())) {
                return d;
            }
        }
        Discente novo = new Discente();
        novo.setId(proximoIdDiscente++);
        novo.setNomeCompleto(nome.trim());
        novo.setMatricula(matricula.trim());
        discentes.add(novo);
        return novo;
    }

    public static MembroComissao buscarMembro(String username) {
        for (MembroComissao m : membros) {
            if (m.getUsername().equalsIgnoreCase(username.trim())) {
                return m;
            }
        }
        return null;
    }

    public static Solicitacao buscarSolicitacaoPorId(Long id) {
        for (Solicitacao s : solicitacoes) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    // Procura outra solicitação do mesmo aluno com nome de documento igual ou parecido (em qualquer comissão)
    public static List<Solicitacao> buscarDuplicadas(Solicitacao atual) {
        List<Solicitacao> lista = new ArrayList<>();
        if (atual == null || atual.getNomeDocumento() == null) {
            return lista;
        }
        String nomeAtual = atual.getNomeDocumento().trim().toLowerCase();

        for (Solicitacao outra : atual.getDiscente().getSolicitacoes()) {
            if (!outra.getId().equals(atual.getId()) && outra.getStatus() != StatusSolicitacao.CRIADA) {
                String nomeOutra = outra.getNomeDocumento().trim().toLowerCase();
                if (nomeAtual.equals(nomeOutra) || nomeAtual.contains(nomeOutra) || nomeOutra.contains(nomeAtual)) {
                    lista.add(outra);
                }
            }
        }
        return lista;
    }
}

