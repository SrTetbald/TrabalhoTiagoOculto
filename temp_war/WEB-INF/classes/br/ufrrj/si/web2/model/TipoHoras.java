package br.ufrrj.si.web2.model;

public enum TipoHoras {
    EXTENSAO("Comissão de Ações Extensionistas"),
    COMPLEMENTAR("Comissão de Atividades Complementares"),
    ESTAGIO("Comissão de Estágio");

    private final String descricao;

    TipoHoras(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}