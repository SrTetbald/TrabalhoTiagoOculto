package br.ufrrj.si.web2.model;

//import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o aluno (discente) de Sistemas de Informação.
 * Regra: cadastrado automaticamente no primeiro acesso (nome + matrícula).
 * Uma vez cadastrado, não pode ser alterado nem excluído.
 */
//@Entity
//@Table(name = "discentes")
public class Discente implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false, length = 120)
    private String nomeCompleto;

//    @Column(nullable = false, unique = true, length = 20)
    private String matricula;

//    @OneToMany(mappedBy = "discente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Solicitacao> solicitacoes = new ArrayList<>();

    public Discente() {
    }

    public Discente(Long id, String nomeCompleto, String matricula) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.matricula = matricula;
    }

    /**
     * Soma as horas concedidas nas solicitações CONCLUIDAS de uma categoria específica.
     */
    public int calcularHorasConcluidasPorTipo(TipoHoras tipo) {
        int total = 0;
        for (Solicitacao s : solicitacoes) {
            if (s.getTipoHoras() == tipo
                    && s.getStatus() == StatusSolicitacao.CONCLUIDO
                    && s.getHorasConcedidas() != null) {
                total += s.getHorasConcedidas();
            }
        }
        return total;
    }

    /**
     * Soma todas as horas concluídas do discente somando todas as comissões.
     */
    public int calcularTotalHorasConcluidas() {
        int total = 0;
        for (Solicitacao s : solicitacoes) {
            if (s.getStatus() == StatusSolicitacao.CONCLUIDO && s.getHorasConcedidas() != null) {
                total += s.getHorasConcedidas();
            }
        }
        return total;
    }

    // Getters específicos para facilitar acesso via Expression Language (EL) no JSP
    public int getHorasConcluidasExtensao() {
        return calcularHorasConcluidasPorTipo(TipoHoras.EXTENSAO);
    }

    public int getHorasConcluidasComplementar() {
        return calcularHorasConcluidasPorTipo(TipoHoras.COMPLEMENTAR);
    }

    public int getHorasConcluidasEstagio() {
        return calcularHorasConcluidasPorTipo(TipoHoras.ESTAGIO);
    }

    public int getTotalHorasConcluidas() {
        return calcularTotalHorasConcluidas();
    }

    // Getters e Setters padrão
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
