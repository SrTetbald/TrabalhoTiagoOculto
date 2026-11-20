package br.ufrrj.si.web2.model;

//import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

//@Entity
//@Table(name = "historico_status")
public class HistoricoStatus implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(optional = false)
//    @JoinColumn(name = "solicitacao_id", nullable = false)
    private Solicitacao solicitacao;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 30)
    private StatusSolicitacao status;

//    @Column(columnDefinition = "TEXT")
    private String observacaoOuParecer;

    // Relacionamento opcional com o mandato/atuação da comissão (null se a ação foi do Discente)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "atuacao_comissao_id")
    private AtuacaoComissao atuacaoResponsavel;

//    @Column(nullable = false, length = 150)
    private String alteradoPor;

//    @Column(nullable = false)
    private LocalDateTime dataRegistro = LocalDateTime.now();

    public HistoricoStatus() {}

    // Construtor para ações realizadas pelo Discente (sem AtuacaoComissao)
    public HistoricoStatus(Solicitacao solicitacao, StatusSolicitacao status, String observacao, String nomeDiscente) {
        this.solicitacao = solicitacao;
        this.status = status;
        this.observacaoOuParecer = observacao;
        this.atuacaoResponsavel = null;
        this.alteradoPor = "Discente: " + nomeDiscente;
        this.dataRegistro = LocalDateTime.now();
    }

    // Construtor para ações realizadas por Membros da Comissão / Coordenação
    public HistoricoStatus(Solicitacao solicitacao, StatusSolicitacao status, String observacao, AtuacaoComissao atuacao) {
        this.solicitacao = solicitacao;
        this.status = status;
        this.observacaoOuParecer = observacao;
        this.atuacaoResponsavel = atuacao;
        this.alteradoPor = atuacao.getMembro().getNomeCompleto() + " (" + atuacao.getPerfil().name() + ")";
        this.dataRegistro = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Solicitacao getSolicitacao() { return solicitacao; }
    public void setSolicitacao(Solicitacao solicitacao) { this.solicitacao = solicitacao; }

    public StatusSolicitacao getStatus() { return status; }
    public void setStatus(StatusSolicitacao status) { this.status = status; }

    public String getObservacaoOuParecer() { return observacaoOuParecer; }
    public void setObservacaoOuParecer(String observacaoOuParecer) { this.observacaoOuParecer = observacaoOuParecer; }

    public AtuacaoComissao getAtuacaoResponsavel() { return atuacaoResponsavel; }
    public void setAtuacaoResponsavel(AtuacaoComissao atuacaoResponsavel) { this.atuacaoResponsavel = atuacaoResponsavel; }

    public String getAlteradoPor() { return alteradoPor; }
    public void setAlteradoPor(String alteradoPor) { this.alteradoPor = alteradoPor; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }
}
