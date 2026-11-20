package br.ufrrj.si.web2.model;

/**
 * Enum que representa todos os estados possíveis de uma solicitação no SGHA.
 * Centraliza a validação da máquina de estados para não espalhar ifs pelos Servlets.
 */
public enum StatusSolicitacao {
    CRIADA("Criada (Rascunho)"),
    ENVIADA("Enviada"),
    PARECER_PENDENTE("Parecer Pendente"),
    AVALIADA("Avaliada"),
    ACEITA("Aceita"),
    RECURSO("Em Recurso"),
    AVALIADA_APOS_RECURSO("Avaliada após Recurso"),
    RECURSO_COLEGIADO("Recurso ao Colegiado"),
    CANCELADA("Cancelada"),
    CONCLUIDO("Concluído");

    private final String rotulo;

    StatusSolicitacao(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    /**
     * Regra do enunciado: apenas solicitações no status CRIADA podem ser excluídas fisicamente.
     */
    public boolean permiteExclusao() {
        return this == CRIADA;
    }

    /**
     * Regra do enunciado: apenas solicitações no status CRIADA podem ter seus dados básicos editados pelo discente.
     */
    public boolean permiteEdicaoPeloDiscente() {
        return this == CRIADA;
    }

    /**
     * Verifica se a transição do status atual para o novoStatus é válida na máquina de estados.
     */
    public boolean podeTransicionarPara(StatusSolicitacao novoStatus) {
        if (novoStatus == null || this == novoStatus) {
            return false;
        }

        switch (this) {
            case CRIADA:
                // Discente pode enviar ou cancelar
                return novoStatus == ENVIADA
                        || novoStatus == PARECER_PENDENTE
                        || novoStatus == CANCELADA;

            case ENVIADA:
            case PARECER_PENDENTE:
                // Comissão avalia, ou discente cancela
                return novoStatus == PARECER_PENDENTE
                        || novoStatus == AVALIADA
                        || novoStatus == CANCELADA;

            case AVALIADA:
                // Discente aceita, abre recurso ou cancela
                return novoStatus == ACEITA
                        || novoStatus == CONCLUIDO
                        || novoStatus == RECURSO
                        || novoStatus == CANCELADA;

            case ACEITA:
                // Estado intermediário que avança automaticamente para CONCLUIDO
                return novoStatus == CONCLUIDO;

            case RECURSO:
                // Comissão reavalia, ou discente cancela
                return novoStatus == AVALIADA_APOS_RECURSO
                        || novoStatus == CANCELADA;

            case AVALIADA_APOS_RECURSO:
                // Discente aceita (vai p/ ACEITA -> CONCLUIDO), abre recurso colegiado ou cancela
                return novoStatus == ACEITA
                        || novoStatus == CONCLUIDO
                        || novoStatus == RECURSO_COLEGIADO
                        || novoStatus == CANCELADA;

            case RECURSO_COLEGIADO:
                // Colegiado/Coordenador emite decisão final e vai direto para CONCLUIDO (ou discente cancela antes)
                return novoStatus == CONCLUIDO
                        || novoStatus == CANCELADA;

            case CANCELADA:
                // Só pode sair de CANCELADA via restauração pelo Coordenador (validado no método específico)
                return true;

            case CONCLUIDO:
                // Estado final estático: só permite cancelamento administrativo
                return novoStatus == CANCELADA;

            default:
                return false;
        }
    }
}

