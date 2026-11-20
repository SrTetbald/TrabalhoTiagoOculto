package br.ufrrj.si.web2.model;

//import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@Entity
//@Table(name = "membros_comissao")
public class MembroComissao implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false, unique = true, length = 50)
    private String username;

//    @Column(nullable = false)
    private String senha;

//    @Column(nullable = false, length = 120)
    private String nomeCompleto;

//    @Column(nullable = false)
    private LocalDateTime dataCriacaoCadastro = LocalDateTime.now();

//    @OneToMany(mappedBy = "membro", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AtuacaoComissao> atuacoes = new ArrayList<>();

    public MembroComissao() {}

    // Método auxiliar de negócio: retorna a atuação vigente hoje (ou null se afastado/inativo)
    public AtuacaoComissao getAtuacaoVigente() {
        return atuacoes.stream()
                .filter(AtuacaoComissao::isVigenteHoje)
                .findFirst()
                .orElse(null);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public LocalDateTime getDataCriacaoCadastro() { return dataCriacaoCadastro; }
    public void setDataCriacaoCadastro(LocalDateTime dataCriacaoCadastro) { this.dataCriacaoCadastro = dataCriacaoCadastro; }
    public List<AtuacaoComissao> getAtuacoes() { return atuacoes; }
    public void setAtuacoes(List<AtuacaoComissao> atuacoes) { this.atuacoes = atuacoes; }
}