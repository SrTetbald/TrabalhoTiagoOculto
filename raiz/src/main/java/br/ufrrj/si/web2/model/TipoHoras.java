package br.ufrrj.si.web2.model;

/**
 * Categorias de carga horária e comissão destino.
 */
public enum TipoHoras {
    EXTENSAO("Comissão de Ações Extensionistas", "Extensão", 60),
    COMPLEMENTAR("Comissão de Atividades Complementares", "Complementares", 120),
    ESTAGIO("Comissão de Estágio", "Estágio", 300);

    private final String descricao;
    private final String nomeCurto;
    private final int metaHorasReferencia;

    TipoHoras(String descricao, String nomeCurto, int metaHorasReferencia) {
        this.descricao = descricao;
        this.nomeCurto = nomeCurto;
        this.metaHorasReferencia = metaHorasReferencia;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getNomeCurto() {
        return nomeCurto;
    }

    public int getMetaHorasReferencia() {
        return metaHorasReferencia;
    }
}

