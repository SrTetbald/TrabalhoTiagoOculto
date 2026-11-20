package br.ufrrj.si.web2.model;

//import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.ufrrj.si.web2.model.AtuacaoComissao.PerfilComissao;

//@Entity
//@Table(name = "solicitacoes")
public class Solicitacao implements Serializable {

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

    // Chamado quando o DISCENTE altera o status (Ex: CRIADA -> ENVIADA, AVALIADA -> RECURSO)
    public void atualizarStatusPorDiscente(StatusSolicitacao novoStatus, String justificativa) {
        // Quando acaba de criar a solicitação, registra o primeiro histórico como CRIADA
        if (this.status == StatusSolicitacao.CRIADA && novoStatus == StatusSolicitacao.CRIADA && this.historicos.isEmpty()) {
            HistoricoStatus h = new HistoricoStatus(this, novoStatus, justificativa, this.discente.getNomeCompleto());
            this.historicos.add(h);
            return;
        }

        if (this.status == StatusSolicitacao.CANCELADA || this.status == StatusSolicitacao.CONCLUIDO) {
            throw new IllegalStateException("O discente não pode alterar solicitação com status " + this.status);
        }

        if (!this.status.podeMudarPara(novoStatus)) {
            throw new IllegalStateException("Mudança de status não permitida: de " + this.status + " para " + novoStatus);
        }

        if (novoStatus == StatusSolicitacao.CANCELADA) {
            this.statusAnteriorAoCancelamento = this.status;
        }
        this.status = novoStatus;
        this.dataUltimaAtualizacao = LocalDateTime.now();

        HistoricoStatus h = new HistoricoStatus(this, novoStatus, justificativa, this.discente.getNomeCompleto());
        this.historicos.add(h);

        // Regra: quando o discente aceita o parecer (ACEITA), o sistema altera para CONCLUIDO
        if (novoStatus == StatusSolicitacao.ACEITA) {
            this.status = StatusSolicitacao.CONCLUIDO;
            HistoricoStatus hFim = new HistoricoStatus(this, StatusSolicitacao.CONCLUIDO,
                    "Processo finalizado após aceite do discente.", this.discente.getNomeCompleto());
            this.historicos.add(hFim);
        }
    }

    // Chamado quando a COMISSÃO / COORDENADOR emite parecer ou restaura cancelamento
    public void atualizarStatusPorComissao(StatusSolicitacao novoStatus, String justificativa, AtuacaoComissao atuacao) {
        if (atuacao == null || !atuacao.isVigenteHoje()) {
            throw new IllegalStateException("Mandato da comissão não está vigente.");
        }
        if (atuacao.getPerfil() == PerfilComissao.SECRETARIO) {
            throw new IllegalStateException("Secretário só tem acesso de consulta.");
        }

        // Regra: apenas o Coordenador pode restaurar solicitação CANCELADA
        if (this.status == StatusSolicitacao.CANCELADA) {
            if (atuacao.getPerfil() != PerfilComissao.COORDENADOR) {
                throw new IllegalStateException("Apenas o Coordenador pode restaurar uma solicitação cancelada.");
            }
        } else {
            if (!atuacao.podeVerTipoHoras(this.tipoHoras)) {
                throw new IllegalStateException("Esta comissão não avalia solicitações de " + this.tipoHoras);
            }
            if (!this.status.podeMudarPara(novoStatus)) {
                throw new IllegalStateException("Mudança de status não permitida: de " + this.status + " para " + novoStatus);
            }
        }

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
