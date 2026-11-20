# Roteiro de Apresentação — Entregável 2 (SGHA)

Roteiro direto cobrindo exatamente os 3 itens pedidos no Entregável 2:

## 1. Camada Model e classe com `main`
- Mostrar o pacote `br.ufrrj.si.web2.model` (`Discente`, `Solicitacao`, `HistoricoStatus`, `MembroComissao`, `AtuacaoComissao`, `StatusSolicitacao`, `TipoHoras`).
- Rodar `TesteDominioSGHA.java` (**Run As -> Java Application**) para mostrar no console a criação e relação entre os objetos, vigência de mandatos e trilha de auditoria (`HistoricoStatus`).

## 2. Controladores (Servlets) e Estrutura em Memória
- Mostrar `BancoMemoria.java` (listas `ArrayList` em memória com dados mockados básicos).
- Mostrar os 4 Servlets em `br.ufrrj.si.web2.controller`:
  - `AcessoDiscenteServlet` (`/acessoDiscente`): instancia/busca `Discente` na memória e abre `discente.jsp`.
  - `SolicitacaoServlet` (`/solicitacao`): anotado com `@MultipartConfig`, salva o arquivo fora do container (`/uploads/sgha` ou `~/uploads/sgha`), instancia `Solicitacao` gravando `caminhoArquivoComprovante` e adiciona na memória.
  - `LoginComissaoServlet` (`/loginComissao`): busca `MembroComissao`, valida `getAtuacaoVigente()` e abre `comissao.jsp`.
  - `AvaliarServlet` (`/avaliar`): atualiza a `Solicitacao` com o parecer da comissão.

## 3. Protótipos / Páginas Iniciais (JSP)
- Rodar no Tomcat (`http://localhost:8080/SGHA/`):
  - Em `index.jsp`, entrar como discente -> vai para `discente.jsp`.
  - Em `discente.jsp`, preencher o formulário de Nova Solicitação anexando um arquivo -> mostrar que a nova linha aparece na tabela com o caminho externo salvo em `caminhoArquivoComprovante`.
  - Voltar para `index.jsp`, entrar na comissão (ex.: `tfranca` / `admin123` ou `cmendes` / `123456`) -> vai para `comissao.jsp` e permite registrar um parecer na solicitação.
