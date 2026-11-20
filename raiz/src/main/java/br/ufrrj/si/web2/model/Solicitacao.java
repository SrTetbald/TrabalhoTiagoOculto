package br.ufrrj.si.web2.model;

//import javax.persistence.*;
import java.io.Serializable;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma solicitação de horas acadêmicas submetida por um discente.
 * Contém a máquina de estados validada e o registro automático de trilha de auditoria (HistoricoStatus).
 */
//@Entity
//@Table(name = "solicitacoes")
public class Solicitacao implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(optional = false, fetch = FetchType.EAGER)
//    @JoinColumn(name = "discente_id", nullable = false)
    private Discente discente;

//    @Column(nullable = false, length = 150)
    private String nomeDocumento;

//    @Column(length = 255)
    private String caminhoArquivoComprovante;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 30)
    private TipoHoras tipoHoras;

//    @Column(nullable = false)
    private Integer horasSolicitadas;

    private Integer horasConcedidas;

//    @Column(columnDefinition = "TEXT")
    private String observacaoDiscente;

//    @Column(columnDefinition = "TEXT")
    private String observacaoParecerPublico; // Visível para o discente e comissões

//    @Column(columnDefinition = "TEXT")
    private String observacaoAdministrativa; // Visível APENAS para comissões, coordenação e secretaria

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 30)
    private StatusSolicitacao status = StatusSolicitacao.CRIADA;

//    @Enumerated(EnumType.STRING)
//    @Column(length = 30)
    private StatusSolicitacao statusAnteriorAoCancelamento;

//    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    private LocalDateTime dataUltimaAtualizacao = LocalDateTime.now();

//    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OrderBy("dataRegistro DESC")
    private List<HistoricoStatus> historicos = new ArrayList<>();

    public Solicitacao() {
    }

    /**
     * Registra o histórico inicial quando a solicitação é criada pelo discente.
     */
    public void registrarCriacaoInicial(boolean enviarDireto) {
        this.status = StatusSolicitacao.CRIADA;
        this.dataCriacao = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
        String nomeAutor = (this.discente != null) ? this.discente.getNomeCompleto() : "Discente";
        this.historicos.add(new HistoricoStatus(this, StatusSolicitacao.CRIADA, "Solicitação registrada pelo discente.", nomeAutor));

        if (enviarDireto) {
            atualizarStatusPorDiscente(StatusSolicitacao.ENVIADA, "Solicitação enviada para análise da " + this.tipoHoras.getDescricao() + ".");
        }
    }

    /**
     * Chamado quando o DISCENTE altera o status (Ex: CRIADA -> ENVIADA, AVALIADA -> RECURSO, ACEITA -> CONCLUIDO).
     * Valida a máquina de estados antes de aplicar a transição.
     */
    public void atualizarStatusPorDiscente(StatusSolicitacao novoStatus, String justificativa) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("O novo status não pode ser nulo.");
        }

        // Quando a solicitação acaba de ser instanciada (CRIADA sem histórico ainda), permite registrar o estado inicial
        if (this.status == StatusSolicitacao.CRIADA && novoStatus == StatusSolicitacao.CRIADA && this.historicos.isEmpty()) {
            String nomeAutor = (this.discente != null) ? this.discente.getNomeCompleto() : "Discente";
            this.historicos.add(new HistoricoStatus(this, StatusSolicitacao.CRIADA, justificativa, nomeAutor));
            return;
        }

        // Bloqueia se o discente tentar restaurar uma solicitação cancelada
        if (this.status == StatusSolicitacao.CANCELADA) {
            throw new IllegalStateException("Solicitação cancelada só pode ser restaurada pelo Coordenador do Curso.");
        }

        // Bloqueia se o discente tentar cancelar uma solicitação já concluída (somente cancelamento administrativo)
        if (this.status == StatusSolicitacao.CONCLUIDO) {
            throw new IllegalStateException("Solicitação concluída não pode mais ser alterada pelo discente.");
        }

        if (!this.status.podeTransicionarPara(novoStatus)) {
            throw new IllegalStateException("Transição inválida pelo discente: de " + this.status + " para " + novoStatus + ".");
        }

        if (novoStatus == StatusSolicitacao.CANCELADA) {
            this.statusAnteriorAoCancelamento = this.status;
        }

        this.status = novoStatus;
        this.dataUltimaAtualizacao = LocalDateTime.now();

        String nomeAutor = (this.discente != null) ? this.discente.getNomeCompleto() : "Discente";
        HistoricoStatus h = new HistoricoStatus(this, novoStatus, justificativa, nomeAutor);
        this.historicos.add(h);

        // Regra do enunciado: "ACEITA: O discente concordou com o parecer. O sistema altera o status para CONCLUÍDO."
        if (novoStatus == StatusSolicitacao.ACEITA) {
            this.status = StatusSolicitacao.CONCLUIDO;
            this.dataUltimaAtualizacao = LocalDateTime.now();
            this.historicos.add(new HistoricoStatus(
                    this,
                    StatusSolicitacao.CONCLUIDO,
                    "Status avançado automaticamente para CONCLUÍDO após aceite do discente.",
                    nomeAutor
            ));
        }
    }

    /**
     * Chamado quando a COMISSÃO ou COORDENADOR emite parecer, reavalia recurso ou restaura cancelamento.
     */
    public void atualizarStatusPorComissao(StatusSolicitacao novoStatus, String justificativa, AtuacaoComissao atuacao) {
        if (atuacao == null || !atuacao.isVigenteHoje()) {
            throw new IllegalStateException("Apenas membros com mandato ativo e dentro da validade podem alterar solicitações.");
        }
        if (atuacao.isSecretario()) {
            throw new IllegalStateException("O perfil de Secretário possui acesso exclusivo de consulta e não pode emitir parecer.");
        }

        // Regra: Restauração de solicitação cancelada é prerrogativa exclusiva do Coordenador
        if (this.status == StatusSolicitacao.CANCELADA && novoStatus != StatusSolicitacao.CANCELADA) {
            if (!atuacao.isCoordenador()) {
                throw new IllegalStateException("Apenas o Coordenador do Curso pode restaurar uma solicitação cancelada.");
            }
            if (this.statusAnteriorAoCancelamento != null && novoStatus != this.statusAnteriorAoCancelamento) {
                throw new IllegalStateException("A restauração deve retornar a solicitação ao seu status anterior (" + this.statusAnteriorAoCancelamento + ").");
            }
        } else {
            // Verifica se a comissão tem permissão para o tipo de horas desta solicitação
            if (!atuacao.podeVisualizarTipo(this.tipoHoras)) {
                throw new IllegalStateException("Esta comissão não possui permissão para avaliar solicitações de " + this.tipoHoras.getDescricao() + ".");
            }
            // Recurso Colegiado só pode ser decidido pelo Coordenador/Colegiado
            if (this.status == StatusSolicitacao.RECURSO_COLEGIADO && !atuacao.isCoordenador()) {
                throw new IllegalStateException("Recurso Colegiado só pode ser decidido pelo Coordenador/Colegiado.");
            }
            if (!this.status.podeTransicionarPara(novoStatus)) {
                throw new IllegalStateException("Transição inválida pela comissão: de " + this.status + " para " + novoStatus + ".");
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

    /**
     * Registra o parecer da comissão/coordenador, avançando o status para o estado correspondente:
     * - ENVIADA / PARECER_PENDENTE -> AVALIADA
     * - RECURSO -> AVALIADA_APOS_RECURSO
     * - RECURSO_COLEGIADO -> CONCLUIDO (decisão final do Colegiado/Coordenador avança automaticamente para CONCLUIDO)
     */
    public void emitirParecerDaComissao(AtuacaoComissao atuacao, Integer horasConcedidas,
                                        String parecerPublico, String obsAdministrativa, String novoNomeDocumento) {
        if (!atuacao.podeAvaliar(this)) {
            throw new IllegalStateException("O perfil atual não pode emitir parecer para esta solicitação no status " + this.status + ".");
        }

        if (novoNomeDocumento != null && !novoNomeDocumento.trim().isEmpty()) {
            this.nomeDocumento = novoNomeDocumento.trim();
        }
        this.horasConcedidas = (horasConcedidas != null && horasConcedidas >= 0) ? horasConcedidas : 0;
        this.observacaoParecerPublico = parecerPublico;
        if (obsAdministrativa != null && !obsAdministrativa.trim().isEmpty()) {
            this.observacaoAdministrativa = obsAdministrativa.trim();
        }

        StatusSolicitacao proximoStatus;
        if (this.status == StatusSolicitacao.ENVIADA || this.status == StatusSolicitacao.PARECER_PENDENTE) {
            proximoStatus = StatusSolicitacao.AVALIADA;
        } else if (this.status == StatusSolicitacao.RECURSO) {
            proximoStatus = StatusSolicitacao.AVALIADA_APOS_RECURSO;
        } else if (this.status == StatusSolicitacao.RECURSO_COLEGIADO) {
            proximoStatus = StatusSolicitacao.CONCLUIDO;
        } else {
            throw new IllegalStateException("Não é possível emitir parecer para solicitação no status " + this.status + ".");
        }

        String resumoHistorico = "Parecer emitido (" + this.horasConcedidas + "h concedidas): " + parecerPublico;
        atualizarStatusPorComissao(proximoStatus, resumoHistorico, atuacao);
    }

    /**
     * Verifica se esta solicitação tem documento semelhante a outra solicitação do mesmo discente
     * (mesmo que tenha sido enviada para outra comissão), para acionar o Alerta de Duplicidade Cruzada.
     */
    public boolean temDocumentoSemelhante(Solicitacao outra) {
        if (outra == null || this.id != null && this.id.equals(outra.getId())) {
            return false;
        }
        if (this.discente == null || outra.getDiscente() == null) {
            return false;
        }
        if (!this.discente.getMatricula().equalsIgnoreCase(outra.getDiscente().getMatricula())) {
            return false;
        }

        String nomeA = normalizarTextoDocumento(this.nomeDocumento);
        String nomeB = normalizarTextoDocumento(outra.getNomeDocumento());

        if (nomeA.isEmpty() || nomeB.isEmpty()) {
            return false;
        }
        if (nomeA.equals(nomeB) || nomeA.contains(nomeB) || nomeB.contains(nomeA)) {
            return true;
        }

        // Compara palavras significativas (com mais de 3 letras) para pegar títulos parecidos
        String[] palavrasA = nomeA.split("\\s+");
        int coincidencias = 0;
        for (String p : palavrasA) {
            if (p.length() > 3 && nomeB.contains(p)) {
                coincidencias++;
            }
        }
        return coincidencias >= 2;
    }

    private static String normalizarTextoDocumento(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return semAcento.toLowerCase()
                .replaceAll("\\.(pdf|png|jpg|jpeg)$", "")
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\b(certificado|declaracao|comprovante|relatorio|curso|acao|de|da|do|em|no|na)\\b", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public String getDataCriacaoFormatada() {
        return dataCriacao != null ? dataCriacao.format(FORMATADOR_DATA) : "";
    }

    public String getDataUltimaAtualizacaoFormatada() {
        return dataUltimaAtualizacao != null ? dataUltimaAtualizacao.format(FORMATADOR_DATA) : "";
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Discente getDiscente() {
        return discente;
    }

    public void setDiscente(Discente discente) {
        this.discente = discente;
    }

    public String getNomeDocumento() {
        return nomeDocumento;
    }

    public void setNomeDocumento(String nomeDocumento) {
        this.nomeDocumento = nomeDocumento;
    }

    public String getCaminhoArquivoComprovante() {
        return caminhoArquivoComprovante;
    }

    public void setCaminhoArquivoComprovante(String caminhoArquivoComprovante) {
        this.caminhoArquivoComprovante = caminhoArquivoComprovante;
    }

    public TipoHoras getTipoHoras() {
        return tipoHoras;
    }

    public void setTipoHoras(TipoHoras tipoHoras) {
        this.tipoHoras = tipoHoras;
    }

    public Integer getHorasSolicitadas() {
        return horasSolicitadas;
    }

    public void setHorasSolicitadas(Integer horasSolicitadas) {
        this.horasSolicitadas = horasSolicitadas;
    }

    public Integer getHorasConcedidas() {
        return horasConcedidas;
    }

    public void setHorasConcedidas(Integer horasConcedidas) {
        this.horasConcedidas = horasConcedidas;
    }

    public String getObservacaoDiscente() {
        return observacaoDiscente;
    }

    public void setObservacaoDiscente(String observacaoDiscente) {
        this.observacaoDiscente = observacaoDiscente;
    }

    public String getObservacaoParecerPublico() {
        return observacaoParecerPublico;
    }

    public void setObservacaoParecerPublico(String observacaoParecerPublico) {
        this.observacaoParecerPublico = observacaoParecerPublico;
    }

    public String getObservacaoAdministrativa() {
        return observacaoAdministrativa;
    }

    public void setObservacaoAdministrativa(String observacaoAdministrativa) {
        this.observacaoAdministrativa = observacaoAdministrativa;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public StatusSolicitacao getStatusAnteriorAoCancelamento() {
        return statusAnteriorAoCancelamento;
    }

    public void setStatusAnteriorAoCancelamento(StatusSolicitacao statusAnteriorAoCancelamento) {
        this.statusAnteriorAoCancelamento = statusAnteriorAoCancelamento;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public List<HistoricoStatus> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<HistoricoStatus> historicos) {
        this.historicos = historicos;
    }
}

