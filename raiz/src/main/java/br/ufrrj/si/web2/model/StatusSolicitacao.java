package br.ufrrj.si.web2.model;

public enum StatusSolicitacao {
    CRIADA,
    ENVIADA,
    PARECER_PENDENTE,
    AVALIADA,
    ACEITA,
    RECURSO,
    AVALIADA_APOS_RECURSO,
    RECURSO_COLEGIADO,
    CANCELADA,
    CONCLUIDO;

    // Centraliza a regra de quais mudanças de status são permitidas
    public boolean podeMudarPara(StatusSolicitacao novoStatus) {
        if (novoStatus == null || this == novoStatus) {
            return false;
        }

        if (this == CRIADA) {
            return novoStatus == ENVIADA || novoStatus == CANCELADA;
        }
        if (this == ENVIADA || this == PARECER_PENDENTE) {
            return novoStatus == AVALIADA || novoStatus == CANCELADA;
        }
        if (this == AVALIADA) {
            return novoStatus == ACEITA || novoStatus == CONCLUIDO || novoStatus == RECURSO || novoStatus == CANCELADA;
        }
        if (this == ACEITA) {
            return novoStatus == CONCLUIDO;
        }
        if (this == RECURSO) {
            return novoStatus == AVALIADA_APOS_RECURSO || novoStatus == CANCELADA;
        }
        if (this == AVALIADA_APOS_RECURSO) {
            return novoStatus == ACEITA || novoStatus == CONCLUIDO || novoStatus == RECURSO_COLEGIADO || novoStatus == CANCELADA;
        }
        if (this == RECURSO_COLEGIADO) {
            return novoStatus == CONCLUIDO || novoStatus == CANCELADA;
        }
        if (this == CANCELADA) {
            // Quando está cancelada, só o Coordenador restaura (validado na classe Solicitacao)
            return true;
        }
        if (this == CONCLUIDO) {
            // Estado final: só permite cancelamento administrativo
            return novoStatus == CANCELADA;
        }

        return false;
    }
}
