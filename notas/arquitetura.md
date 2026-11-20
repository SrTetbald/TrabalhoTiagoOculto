# Arquitetura do Sistema — SGHA (Entregável 2)

## Pacotes (`br.ufrrj.si.web2`)

- `br.ufrrj.si.web2.model`: `Discente`, `Solicitacao`, `HistoricoStatus`, `MembroComissao`, `AtuacaoComissao`, `StatusSolicitacao`, `TipoHoras`.
- `br.ufrrj.si.web2.dao`: `DAO` e `DAOInterface` (do exemplo do professor, comentados para o Entregável 3) + `BancoMemoria` (listas estáticas `ArrayList` simulando a memória no Entregável 2).
- `br.ufrrj.si.web2.util`: `TesteDominioSGHA` (classe com método `main` simulando a criação e relação entre objetos).
- `br.ufrrj.si.web2.controller`: apenas os 4 Servlets dos formulários principais:
  - `AcessoDiscenteServlet` (`/acessoDiscente`)
  - `SolicitacaoServlet` (`/solicitacao` com `@MultipartConfig`)
  - `LoginComissaoServlet` (`/loginComissao`)
  - `AvaliarServlet` (`/avaliar`)

## Páginas JSP (`src/main/webapp`)

- `index.jsp`: portal inicial de acesso (Discente e Comissão).
- `discente.jsp`: formulário de nova solicitação com upload e listagem em memória.
- `comissao.jsp`: listagem em memória e formulário principal de parecer da comissão.
