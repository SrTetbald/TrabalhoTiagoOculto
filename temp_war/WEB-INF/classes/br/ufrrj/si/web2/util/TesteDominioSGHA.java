package br.ufrrj.si.web2.util;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//import br.ufrrj.si.web2.model.AtuacaoComissao;
//import br.ufrrj.si.web2.model.Discente;
//import br.ufrrj.si.web2.model.HistoricoStatus;
//import br.ufrrj.si.web2.model.MembroComissao;
//import br.ufrrj.si.web2.model.Solicitacao;
//import br.ufrrj.si.web2.model.StatusSolicitacao;
//import br.ufrrj.si.web2.model.TipoHoras;
import br.ufrrj.si.web2.model.*;
import br.ufrrj.si.web2.model.AtuacaoComissao.PerfilComissao;

public class TesteDominioSGHA {

    public static void main(String[] args) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("==========================================================");
        System.out.println("   INICIANDO TESTE DE DOMÍNIO E MÁQUINA DE ESTADOS (SGHA) ");
        System.out.println("==========================================================\n");

        // 1. CRIAÇÃO DOS MEMBROS DA COMISSÃO E SEUS MANDATOS (VIGÊNCIAS)
        MembroComissao profCarlos = new MembroComissao();
        profCarlos.setId(1L);
        profCarlos.setNomeCompleto("Prof. Carlos Mendes");
        profCarlos.setUsername("cmendes");
        profCarlos.setSenha("123456");

        // Mandato vigente na Comissão de Extensão
        AtuacaoComissao atuacaoExtensao = new AtuacaoComissao();
        atuacaoExtensao.setId(10L);
        atuacaoExtensao.setMembro(profCarlos);
        atuacaoExtensao.setPerfil(PerfilComissao.COMISSAO_EXTENSAO);
        atuacaoExtensao.setDataInicio(LocalDate.now().minusMonths(2));
        atuacaoExtensao.setDataValidade(LocalDate.now().plusMonths(10));
        atuacaoExtensao.setAtivo(true);
        profCarlos.getAtuacoes().add(atuacaoExtensao);

        // Coordenador do Curso
        MembroComissao profTiago = new MembroComissao();
        profTiago.setId(2L);
        profTiago.setNomeCompleto("Prof. Tiago França");
        profTiago.setUsername("tfranca");
        profTiago.setSenha("admin123");

        AtuacaoComissao atuacaoCoordenador = new AtuacaoComissao();
        atuacaoCoordenador.setId(11L);
        atuacaoCoordenador.setMembro(profTiago);
        atuacaoCoordenador.setPerfil(PerfilComissao.COORDENADOR);
        atuacaoCoordenador.setDataInicio(LocalDate.now().minusYears(1));
        atuacaoCoordenador.setDataValidade(null); // Mandato sem data final definida
        atuacaoCoordenador.setAtivo(true);
        profTiago.getAtuacoes().add(atuacaoCoordenador);

        // Membro com mandato EXPIRADO (para testar bloqueio de vigência)
        MembroComissao profAntigo = new MembroComissao();
        profAntigo.setId(3L);
        profAntigo.setNomeCompleto("Profa. Helena Souza");
        profAntigo.setUsername("hsouza");
        profAntigo.setSenha("123456");

        AtuacaoComissao atuacaoExpirada = new AtuacaoComissao();
        atuacaoExpirada.setId(12L);
        atuacaoExpirada.setMembro(profAntigo);
        atuacaoExpirada.setPerfil(PerfilComissao.COMISSAO_ESTAGIO);
        atuacaoExpirada.setDataInicio(LocalDate.now().minusYears(2));
        atuacaoExpirada.setDataValidade(LocalDate.now().minusDays(1)); // Venceu ontem
        atuacaoExpirada.setAtivo(true);
        profAntigo.getAtuacoes().add(atuacaoExpirada);

        // Validação de acesso por vigência
        System.out.println("--- 1. Verificação de Vigência de Mandatos ---");
        System.out.println("Prof. Carlos está vigente? " + (profCarlos.getAtuacaoVigente() != null ? "SIM (" + profCarlos.getAtuacaoVigente().getPerfil() + ")" : "NÃO"));
        System.out.println("Prof. Tiago está vigente?  " + (profTiago.getAtuacaoVigente() != null ? "SIM (" + profTiago.getAtuacaoVigente().getPerfil() + ")" : "NÃO"));
        System.out.println("Profa. Helena está vigente? " + (profAntigo.getAtuacaoVigente() != null ? "SIM" : "NÃO (Acesso bloqueado!)"));
        System.out.println();

        // 2. CRIAÇÃO DO DISCENTE E DE UMA NOVA SOLICITAÇÃO
        Discente aluno = new Discente();
        aluno.setId(100L);
        aluno.setNomeCompleto("Lucas Oliveira Santos");
        aluno.setMatricula("2024005412");

        Solicitacao sol = new Solicitacao();
        sol.setId(501L);
        sol.setDiscente(aluno);
        sol.setTipoHoras(TipoHoras.EXTENSAO);
        sol.setNomeDocumento("certificado_feira_tecnologia_2026.pdf");
        sol.setCaminhoArquivoComprovante("/uploads/sgha/2024005412_feira.pdf");
        sol.setHorasSolicitadas(40);
        sol.setObservacaoDiscente("Atuei como monitor na organização da Feira de Tecnologia da UFRRJ.");
        
        // Registro inicial no histórico (Status inicial: CRIADA)
        sol.atualizarStatusPorDiscente(StatusSolicitacao.CRIADA, "Rascunho criado pelo discente.");
        aluno.getSolicitacoes().add(sol);

        // 3. DISCENTE ENVIA A SOLICITAÇÃO PARA A COMISSÃO
        sol.atualizarStatusPorDiscente(StatusSolicitacao.ENVIADA, "Solicitação encaminhada para a Comissão de Extensão.");

        // 4. COMISSÃO ANALISA, AJUSTA O NOME DO DOCUMENTO E EMITE PARECER PARCIAL
        AtuacaoComissao avaliadorExtensao = profCarlos.getAtuacaoVigente();
        if (avaliadorExtensao != null) {
            // Comissão corrige o nome genérico enviado pelo aluno
            sol.setNomeDocumento("Certificado Monitoria Feira de Tecnologia UFRRJ 2026.pdf");
            sol.setHorasConcedidas(25);
            sol.setObservacaoParecerPublico("Deferido parcialmente (25h). O certificado comprova apenas os 3 dias do evento.");
            sol.setObservacaoAdministrativa("Verificado no sistema: não há duplicidade em Atividades Complementares.");
            
            sol.atualizarStatusPorComissao(
                StatusSolicitacao.AVALIADA,
                "Parecer inicial emitido: 25h concedidas.",
                avaliadorExtensao
            );
        }

        // 5. DISCENTE NÃO CONCORDA E ABRE RECURSO
        sol.atualizarStatusPorDiscente(
            StatusSolicitacao.RECURSO,
            "Anexei declaração complementar da coordenação do evento comprovando reuniões preparatórias."
        );

        // 6. SIMULAÇÃO DE CANCELAMENTO ACIDENTAL E RESTAURAÇÃO PELO COORDENADOR
        sol.atualizarStatusPorDiscente(StatusSolicitacao.CANCELADA, "Discente clicou em cancelar por engano.");
        System.out.println("--- 2. Teste de Cancelamento e Restauração ---");
        System.out.println("Status após cancelamento: " + sol.getStatus());
        System.out.println("Status guardado para restauração: " + sol.getStatusAnteriorAoCancelamento());

        // Apenas o Coordenador restaura
        AtuacaoComissao coord = profTiago.getAtuacaoVigente();
        if (coord != null && coord.getPerfil() == PerfilComissao.COORDENADOR) {
            StatusSolicitacao statusRestaurado = sol.getStatusAnteriorAoCancelamento();
            sol.atualizarStatusPorComissao(
                statusRestaurado,
                "Solicitação restaurada a pedido do discente.",
                coord
            );
            System.out.println("Status após restauração pelo Coordenador: " + sol.getStatus() + "\n");
        }

        // 7. REAVALIAÇÃO APÓS RECURSO E CONCLUSÃO DO PROCESSO
        sol.setHorasConcedidas(35);
        sol.setObservacaoParecerPublico("Recurso deferido parcialmente. Ajustado para 35h mediante declaração complementar.");
        sol.atualizarStatusPorComissao(
            StatusSolicitacao.AVALIADA_APOS_RECURSO,
            "Reavaliação concluída com ajuste para 35h.",
            avaliadorExtensao
        );

        // Discente aceita o novo parecer -> Avança para ACEITA e CONCLUIDO
        sol.atualizarStatusPorDiscente(StatusSolicitacao.ACEITA, "Discente aceitou o parecer do recurso.");
        sol.atualizarStatusPorDiscente(StatusSolicitacao.CONCLUIDO, "Processo finalizado e horas computadas no histórico.");

        // 8. IMPRESSÃO DO RELATÓRIO FINAL E TRILHA DE AUDITORIA
        System.out.println("--- 3. Resumo Final da Solicitação #" + sol.getId() + " ---");
        System.out.println("Discente:             " + sol.getDiscente().getNomeCompleto() + " (" + sol.getDiscente().getMatricula() + ")");
        System.out.println("Documento (Ajustado): " + sol.getNomeDocumento());
        System.out.println("Comissão Destino:     " + sol.getTipoHoras().getDescricao());
        System.out.println("Horas Solicitadas:    " + sol.getHorasSolicitadas() + "h | Concedidas: " + sol.getHorasConcedidas() + "h");
        System.out.println("Status Final:         " + sol.getStatus());
        System.out.println("Parecer Público:      " + sol.getObservacaoParecerPublico());
        System.out.println("Obs. Administrativa:  " + sol.getObservacaoAdministrativa());

        System.out.println("\n--- 4. Trilha de Auditoria (HistoricoStatus) ---");
        // Como @OrderBy("dataRegistro DESC") vale no JPA, em memória imprimimos na ordem de inserção
        for (HistoricoStatus h : sol.getHistoricos()) {
            System.out.printf("[%s] %-22s | Por: %-42s | Obs: %s%n",
                    h.getDataRegistro().format(fmt),
                    h.getStatus(),
                    h.getAlteradoPor(),
                    h.getObservacaoOuParecer());
        }
    }
}