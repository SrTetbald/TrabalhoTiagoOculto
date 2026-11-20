# Arquitetura do Sistema — SGHA (Entregável 2)

## Pacotes (`br.ufrrj.si.web2`)

- `br.ufrrj.si.web2.model`: classes POJO de domínio (`Discente`, `Solicitacao`, `HistoricoStatus`, `MembroComissao`, `AtuacaoComissao`) e enums (`StatusSolicitacao`, `TipoHoras`).
- `br.ufrrj.si.web2.repositorio`: `RepositorioMemoria` (singleton em memória que simula o banco de dados no Entregável 2).
- `br.ufrrj.si.web2.dao`: `DAO` e `DAOInterface` do exemplo do professor (mantidos comentados para ativar com JPA no Entregável 3).
- `br.ufrrj.si.web2.controller`: Servlets (`jakarta.servlet`, compatíveis com o Apache Tomcat 10.1).
- `br.ufrrj.si.web2.util`: `TesteDominioSGHA` (classe com método `main` que valida o modelo no console).

## Fluxo MVC (Quem chama quem)

1. **`index.jsp`** (Portal de Acesso):
   - Formulário do Discente $\rightarrow$ `POST /acessoDiscente` (`AcessoDiscenteServlet`) $\rightarrow$ busca ou cadastra no `RepositorioMemoria`, salva na sessão e redireciona para `GET /painelDiscente`.
   - Formulário da Comissão $\rightarrow$ `POST /loginComissao` (`LoginComissaoServlet`) $\rightarrow$ valida usuário, senha e `getAtuacaoVigente() != null`, salva na sessão e redireciona para `GET /painelComissao`.
2. **Módulo do Discente**:
   - `GET /painelDiscente` (`PainelDiscenteServlet`) $\rightarrow$ carrega dados do discente e faz `forward` para `painel-discente.jsp`.
   - `POST /solicitacao` (`SolicitacaoServlet`, `@MultipartConfig`) $\rightarrow$ cria/edita solicitação, salva arquivo de upload fora do Tomcat (`~/uploads/sgha` ou `/uploads/sgha`) e executa transições do discente (`enviar`, `cancelar`, `excluir`, `aceitar`, `recurso`, `recurso_colegiado`).
3. **Módulo da Comissão / Coordenação / Secretaria**:
   - `GET /painelComissao` (`PainelComissaoServlet`) $\rightarrow$ filtra solicitações visíveis ao perfil logado, busca duplicidades cruzadas (`buscarDuplicidadesCruzadas`) e faz `forward` para `painel-comissao.jsp`.
   - `POST /avaliar` (`AvaliarSolicitacaoServlet`) $\rightarrow$ emite parecer (`APROVAR`/`REJEITAR`), ajusta nome do documento (`AJUSTAR_NOME`) ou restaura solicitação cancelada (`RESTAURAR`, exclusivo do Coordenador).
4. **Arquivos e Sessão**:
   - `GET /downloadComprovante?id=X` (`DownloadComprovanteServlet`) $\rightarrow$ lê o arquivo do diretório externo e envia ao navegador.
   - `GET /logout` (`LogoutServlet`) $\rightarrow$ invalida a sessão (`session.invalidate()`) e volta para `index.jsp`.

## Decisões Técnicas Registradas

- **Tomcat 10.1 (`jakarta.servlet`)**: o ambiente de desenvolvimento do grupo usa o Apache Tomcat 10.1.62 no Eclipse, por isso todos os Servlets utilizam `jakarta.servlet.*` e os JARs do Jakarta JSTL 3.0 estão em `WEB-INF/lib`.
- **Upload fora do Tomcat**: tenta gravar em `/uploads/sgha`; se o usuário do sistema operacional não tiver permissão na raiz `/`, cria automaticamente `~/uploads/sgha` na pasta do usuário, mantendo os arquivos fora do container web.

