package br.ufrrj.si.web2.model;
//import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@Entity
//@Table(name = "solicitacoes")
public class Solicitacao implements Serializable {

	// Adicionado para garantir a compatibilidade na serialização (sessão / JPA)
    private static final long serialVersionUID = 1L;
    
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(optional = false, fetch = FetchType.EAGER)
//    @JoinColumn(name = "discente_id", nullable = false)
    private Discente discente;

//    @Column(nullable = false)//, length = 150)
    private String nomeDocumento;

    //@Column(length = 255)
    private String caminhoArquivoComprovante;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)//, length = 30)
    private TipoHoras tipoHoras;

//    @Column(nullable = false)
    private Integer horasSolicitadas;

    private Integer horasConcedidas;

    //@Column(columnDefinition = "TEXT")
    private String observacaoDiscente;

    //@Column(columnDefinition = "TEXT")
    private String observacaoParecerPublico; // Visível para o discente

    //@Column(columnDefinition = "TEXT")
    private String observacaoAdministrativa; // Visível APENAS para comissões/coordenação

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)//, length = 30)
    private StatusSolicitacao status = StatusSolicitacao.CRIADA;

//    @Enumerated(EnumType.STRING)
    //@Column(length = 30)
    private StatusSolicitacao statusAnteriorAoCancelamento;

//    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    private LocalDateTime dataUltimaAtualizacao = LocalDateTime.now();

//    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OrderBy("dataRegistro DESC")
    private List<HistoricoStatus> historicos = new ArrayList<>();

//    Chamado quando o DISCENTE altera o status (Ex: CRIADA -> ENVIADA, AVALIADA -> RECURSO)
    public void atualizarStatusPorDiscente(StatusSolicitacao novoStatus, String justificativa) {
        if (novoStatus == StatusSolicitacao.CANCELADA) {
            this.statusAnteriorAoCancelamento = this.status;
        }
        this.status = novoStatus;
        this.dataUltimaAtualizacao = LocalDateTime.now();

        HistoricoStatus h = new HistoricoStatus(this, novoStatus, justificativa, this.discente.getNomeCompleto());
        this.historicos.add(h);
    }

//    Chamado quando a COMISSÃO / COORDENADOR emite parecer ou restaura cancelamento
    public void atualizarStatusPorComissao(StatusSolicitacao novoStatus, String justificativa, AtuacaoComissao atuacao) {
        if (novoStatus == StatusSolicitacao.CANCELADA) {
            this.statusAnteriorAoCancelamento = this.status;
        }
        this.status = novoStatus;
        this.dataUltimaAtualizacao = LocalDateTime.now();

        HistoricoStatus h = new HistoricoStatus(this, novoStatus, justificativa, atuacao);
        this.historicos.add(h);
    }

    // Getters e Setters ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Discente getDiscente() { return discente; }
    public void setDiscente(Discente discente) { this.discente = discente; }
    public String getNomeDocumento() { return nomeDocumento; }
    public void setNomeDocumento(String nomeDocumento) { this.nomeDocumento = nomeDocumento; }
    public String getCaminhoArquivoComprovante() { return caminhoArquivoComprovante; }
    public void setCaminhoArquivoComprovante(String caminhoArquivoComprovante) { this.caminhoArquivoComprovante = caminhoArquivoComprovante; }
    public TipoHoras getTipoHoras() { return tipoHoras; }
    public void setTipoHoras(TipoHoras tipoHoras) { this.tipoHoras = tipoHoras; }
    public Integer getHorasSolicitadas() { return horasSolicitadas; }
    public void setHorasSolicitadas(Integer horasSolicitadas) { this.horasSolicitadas = horasSolicitadas; }
    public Integer getHorasConcedidas() { return horasConcedidas; }
    public void setHorasConcedidas(Integer horasConcedidas) { this.horasConcedidas = horasConcedidas; }
    public String getObservacaoDiscente() { return observacaoDiscente; }
    public void setObservacaoDiscente(String observacaoDiscente) { this.observacaoDiscente = observacaoDiscente; }
    public String getObservacaoParecerPublico() { return observacaoParecerPublico; }
    public void setObservacaoParecerPublico(String observacaoParecerPublico) { this.observacaoParecerPublico = observacaoParecerPublico; }
    public String getObservacaoAdministrativa() { return observacaoAdministrativa; }
    public void setObservacaoAdministrativa(String observacaoAdministrativa) { this.observacaoAdministrativa = observacaoAdministrativa; }
    public StatusSolicitacao getStatus() { return status; }
    public void setStatus(StatusSolicitacao status) { this.status = status; }
    public StatusSolicitacao getStatusAnteriorAoCancelamento() { return statusAnteriorAoCancelamento; }
    public void setStatusAnteriorAoCancelamento(StatusSolicitacao statusAnteriorAoCancelamento) { this.statusAnteriorAoCancelamento = statusAnteriorAoCancelamento; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }
    public List<HistoricoStatus> getHistoricos() { return historicos; }
    public void setHistoricos(List<HistoricoStatus> historicos) { this.historicos = historicos; }
    
}