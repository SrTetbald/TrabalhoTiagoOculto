# Estado Atual do Projeto — SGHA

## Entregável 2 (Concluído)

1. **Camada Model + `main`**:
   - Entidades e enums em `br.ufrrj.si.web2.model` com vigência de mandatos, trilha de auditoria (`HistoricoStatus`) e validação de status.
   - Classe `TesteDominioSGHA` em `br.ufrrj.si.web2.util` com método `main` simulando a criação e relação entre objetos.
2. **Controladores + Memória**:
   - `BancoMemoria` (`br.ufrrj.si.web2.dao`) com listas em memória e dados mockados básicos.
   - 4 Servlets (`AcessoDiscenteServlet`, `SolicitacaoServlet`, `LoginComissaoServlet`, `AvaliarServlet`) acessando o modelo.
3. **Páginas iniciais (JSP) + Upload fora do container**:
   - `index.jsp`, `discente.jsp` e `comissao.jsp` acessando os 4 Servlets.
   - `SolicitacaoServlet` com `@MultipartConfig` salvando os arquivos em `/uploads/sgha` (ou `~/uploads/sgha`) e guardando o caminho em `caminhoArquivoComprovante`.
