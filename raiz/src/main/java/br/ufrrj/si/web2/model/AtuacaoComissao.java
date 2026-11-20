package br.ufrrj.si.web2.model;

//import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa um mandato/atuação de um membro em um dos 5 perfis do sistema.
 * Permite que um professor saia da comissão, vire coordenador ou volte depois,
 * preservando o histórico de qual função exercia em cada parecer.
 */
//@Entity
//@Table(name = "atuacoes_comissao")
public class AtuacaoComissao implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(optional = false)
//    @JoinColumn(name = "membro_id", nullable = false)
    private MembroComissao membro;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 30)
    private PerfilComissao perfil;

//    @Column(nullable = false)
    private LocalDate dataInicio;

    // Se null, significa mandato em aberto (indeterminado até encerramento)
    private LocalDate dataValidade;

    // Permite revogação manual imediata antes da dataValidade, se necessário
//    @Column(nullable = false)
    private boolean ativo = true;

    public enum PerfilComissao {
        COMISSAO_EXTENSAO("Comissão de Extensão", TipoHoras.EXTENSAO),
        COMISSAO_COMPLEMENTAR("Comissão de Atividades Complementares", TipoHoras.COMPLEMENTAR),
        COMISSAO_ESTAGIO("Comissão de Estágio", TipoHoras.ESTAGIO),
        COORDENADOR("Coordenador do Curso", null),
        SECRETARIO("Secretário do Curso", null);

        private final String descricao;
        private final TipoHoras tipoHorasAssociado;

        PerfilComissao(String descricao, TipoHoras tipoHorasAssociado) {
            this.descricao = descricao;
            this.tipoHorasAssociado = tipoHorasAssociado;
        }

        public String getDescricao() {
            return descricao;
        }

        public TipoHoras getTipoHorasAssociado() {
            return tipoHorasAssociado;
        }
    }

    public AtuacaoComissao() {
    }

    /**
     * Verifica se o mandato está ativo e dentro do período de vigência na data de hoje.
     */
    public boolean isVigenteHoje() {
        if (!this.ativo || this.dataInicio == null) {
            return false;
        }
        LocalDate hoje = LocalDate.now();
        boolean iniciou = !hoje.isBefore(this.dataInicio);
        boolean naoExpirou = (this.dataValidade == null) || !hoje.isAfter(this.dataValidade);
        return iniciou && naoExpirou;
    }

    public boolean isCoordenador() {
        return this.perfil == PerfilComissao.COORDENADOR;
    }

    public boolean isSecretario() {
        return this.perfil == PerfilComissao.SECRETARIO;
    }

    /**
     * Verifica se este perfil pode visualizar solicitações de determinado tipo de horas.
     * Comissões específicas só veem o próprio tipo; Coordenador e Secretário veem todas.
     */
    public boolean podeVisualizarTipo(TipoHoras tipo) {
        if (isCoordenador() || isSecretario()) {
            return true;
        }
        return this.perfil != null && this.perfil.getTipoHorasAssociado() == tipo;
    }

    /**
     * Verifica se este perfil pode emitir parecer em uma solicitação.
     * - Secretário nunca emite parecer (apenas consulta).
     * - Coordenador pode avaliar qualquer solicitação pendente (inclusive RECURSO_COLEGIADO).
     * - Comissão específica só avalia solicitações do seu tipo em ENVIADA, PARECER_PENDENTE ou RECURSO.
     */
    public boolean podeAvaliar(Solicitacao solicitacao) {
        if (solicitacao == null || !isVigenteHoje() || isSecretario()) {
            return false;
        }

        StatusSolicitacao st = solicitacao.getStatus();
        boolean statusAguardandoComissao = (st == StatusSolicitacao.ENVIADA
                || st == StatusSolicitacao.PARECER_PENDENTE
                || st == StatusSolicitacao.RECURSO);

        if (isCoordenador()) {
            return statusAguardandoComissao || st == StatusSolicitacao.RECURSO_COLEGIADO;
        }

        return podeVisualizarTipo(solicitacao.getTipoHoras()) && statusAguardandoComissao;
    }

    public String getResumoVigencia() {
        String inicio = (dataInicio != null) ? dataInicio.format(FORMATADOR_DATA) : "--";
        String fim = (dataValidade != null) ? dataValidade.format(FORMATADOR_DATA) : "Indeterminada";
        return inicio + " até " + fim;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MembroComissao getMembro() {
        return membro;
    }

    public void setMembro(MembroComissao membro) {
        this.membro = membro;
    }

    public PerfilComissao getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilComissao perfil) {
        this.perfil = perfil;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}

