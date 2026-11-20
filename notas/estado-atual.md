# Estado Atual do Projeto — SGHA

## O que já está pronto (Entregável 2 — Prazo 20/11)

- **Estrutura de pastas** organizada em `./notas` e `./raiz` (projeto Java Web dinâmico pronto para o Eclipse + Tomcat 10.1).
- **Camada Model (`br.ufrrj.si.web2.model`)**:
  - `Discente`, `Solicitacao`, `HistoricoStatus`, `MembroComissao`, `AtuacaoComissao`, `StatusSolicitacao`, `TipoHoras`.
  - Máquina de estados centralizada e validada em `StatusSolicitacao` e `Solicitacao`.
  - Vigência de mandatos implementada em `AtuacaoComissao.isVigenteHoje()`.
  - Trilha de auditoria (`HistoricoStatus`) gerada automaticamente em toda mudança de status.
  - Alerta automático de duplicidade cruzada entre comissões (`Solicitacao.temDocumentoSemelhante`).
- **Classe com `main` (`br.ufrrj.si.web2.util.TesteDominioSGHA`)**:
  - Simula criação de membros, vigência de mandatos (incluindo mandato expirado), ciclo completo da solicitação, bloqueio de restauração por membro comum, restauração pelo Coordenador, detecção de duplicidade e impressão da trilha de auditoria.
- **Persistência simulada em memória (`br.ufrrj.si.web2.repositorio.RepositorioMemoria`)**:
  - Pré-carregada com os 5 perfis de comissão/coordenação/secretaria + 1 conta com mandato expirado + discente Carlos Eduardo da Silva (`2023019823`) com solicitações em diferentes status e caso de duplicidade cruzada.
- **Controladores (`br.ufrrj.si.web2.controller`)**:
  - `AcessoDiscenteServlet`, `PainelDiscenteServlet`, `SolicitacaoServlet` (com `@MultipartConfig` salvando fora do container), `LoginComissaoServlet`, `PainelComissaoServlet`, `AvaliarSolicitacaoServlet`, `DownloadComprovanteServlet` e `LogoutServlet`.
- **Páginas JSP (`./raiz/src/main/webapp`)**:
  - `index.jsp`, `painel-discente.jsp`, `painel-comissao.jsp` usando JSTL/EL e Bootstrap 5, sem código Java (`<% %>`) nas páginas.
- **Sessão (`web.xml`)**:
  - Configurada para expirar em **2 minutos** de inatividade (`<session-timeout>2</session-timeout>`).

## O que fica para o Entregável 3 (Fora do escopo agora)

- Migração para projeto Gradle (`build.gradle`).
- Descomentar e ativar mapeamento JPA/Hibernate nas entidades e no `DAO.java`.
- Banco de dados relacional MySQL/MariaDB e `persistence.xml`.

