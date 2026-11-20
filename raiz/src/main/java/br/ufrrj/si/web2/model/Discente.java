package br.ufrrj.si.web2.model;

//import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//@Entity
//@Table(name = "discentes")
public class Discente implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false)//, length = 120)
    private String nomeCompleto;

//    @Column(nullable = false)//, unique = true, length = 20)
    private String matricula;

//    @OneToMany(mappedBy = "discente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Solicitacao> solicitacoes = new ArrayList<>();

    // Soma as horas concedidas das solicitações já CONCLUIDAS por tipo
    public int calcularHorasPorTipo(TipoHoras tipo) {
        int soma = 0;
        for (Solicitacao s : solicitacoes) {
            if (s.getTipoHoras() == tipo && s.getStatus() == StatusSolicitacao.CONCLUIDO && s.getHorasConcedidas() != null) {
                soma += s.getHorasConcedidas();
            }
        }
        return soma;
    }

    public int getHorasExtensao() {
        return calcularHorasPorTipo(TipoHoras.EXTENSAO);
    }

    public int getHorasComplementar() {
        return calcularHorasPorTipo(TipoHoras.COMPLEMENTAR);
    }

    public int getHorasEstagio() {
        return calcularHorasPorTipo(TipoHoras.ESTAGIO);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public List<Solicitacao> getSolicitacoes() {
        return solicitacoes;
    }

    public void setSolicitacoes(List<Solicitacao> solicitacoes) {
        this.solicitacoes = solicitacoes;
    }
}
