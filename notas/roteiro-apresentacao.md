# Roteiro de Apresentação — Entregável 2 (SGHA)

Este documento serve como guia para apresentar ao professor o que foi desenvolvido no **Entregável 2** (Modelagem, Controladores em Memória, Upload fora do Container e JSPs iniciais), seguindo os três tópicos pedidos no enunciado.

---

## Parte 1 — Camada Model e Validação via `main`

**O que mostrar no Eclipse:** pacote `br.ufrrj.si.web2.model` e classe `br.ufrrj.si.web2.util.TesteDominioSGHA`.

1. **Adaptação do modelo de referência**:
   - Mantivemos a estrutura base sugerida pelo professor (`Discente`, `Solicitacao`, `HistoricoStatus`, `MembroComissao`, `AtuacaoComissao`, `StatusSolicitacao`, `TipoHoras`), com as anotações JPA comentadas para serem ativadas apenas no Entregável 3.
2. **O que evoluímos no modelo**:
   - **Máquina de estados validada e centralizada**: no modelo original era possível setar qualquer status livremente. Centralizamos as regras de transição em `StatusSolicitacao.podeTransicionarPara()` e nos métodos de negócio de `Solicitacao`:
     - Exclusão física só é permitida em `CRIADA` (`permiteExclusao()`).
     - Quando o discente aceita o parecer (`ACEITA`), o modelo registra o aceite no histórico e já avança automaticamente para `CONCLUIDO`.
     - Quando há `RECURSO_COLEGIADO`, após o parecer do Coordenador o status também avança automaticamente para `CONCLUIDO`.
     - Quando uma solicitação é `CANCELADA`, ela guarda o `statusAnteriorAoCancelamento` e **apenas o perfil `COORDENADOR`** consegue restaurá-la.
   - **Vigência de mandatos**: `MembroComissao` possui uma lista de `AtuacaoComissao`. O método `isVigenteHoje()` verifica se o mandato está ativo, já iniciou e não expirou (`dataValidade`).
   - **Trilha de auditoria (`HistoricoStatus`)**: toda mudança de status cria automaticamente um `HistoricoStatus` registrando data/hora, novo status, justificativa e quem executou (o nome do discente ou o nome do membro + perfil vigente no momento do parecer).
   - **Detecção de duplicidade cruzada**: o método `Solicitacao.temDocumentoSemelhante()` normaliza o título do documento e identifica se o mesmo discente já submeteu documento igual ou parecido em qualquer comissão.

**Demonstração prática (1 minuto):**
- Clicar com o botão direito em `TesteDominioSGHA.java` $\rightarrow$ **Run As $\rightarrow$ Java Application**.
- Mostrar no Console:
  1. Verificação de vigência (Prof. Carlos e Prof. Tiago vigentes; Profa. Helena com mandato vencido ontem bloqueada).
  2. Ciclo da solicitação (`CRIADA` $\rightarrow$ `ENVIADA` $\rightarrow$ `AVALIADA` $\rightarrow$ `RECURSO`).
  3. Cancelamento acidental, tentativa de restauração por membro comum bloqueada por exceção (`IllegalStateException`) e restauração realizada pelo Coordenador.
  4. Alerta de duplicidade cruzada detectado entre Extensão e Complementares.
  5. Trilha de auditoria completa impressa no final.

---

## Parte 2 — Controladores (Servlets), Persistência em Memória e Upload (`@MultipartConfig`)

**O que mostrar no Eclipse:** pacotes `br.ufrrj.si.web2.repositorio`, `br.ufrrj.si.web2.controller` e `WEB-INF/web.xml`.

1. **Persistência simulada em memória (`RepositorioMemoria.java`)**:
   - Como o banco de dados (JPA/Hibernate + MySQL + Gradle) é escopo do Entregável 3, criamos a classe `RepositorioMemoria` (Singleton) que mantém os objetos em mapas na memória (`discentesPorMatricula`, `solicitacoesPorId`, `membrosPorUsername`).
   - Já deixamos dados mockados carregados (os 5 perfis da comissão, 1 conta com mandato expirado e 1 discente com solicitações em diferentes status) para facilitar a navegação.
2. **Controladores (`controller`) respeitando o MVC**:
   - As JSPs não possuem código Java (`<% %>`) nem regra de negócio: usam apenas JSTL/EL e enviam requisições para os Servlets.
   - `AcessoDiscenteServlet` (`/acessoDiscente`): identifica o discente por nome + matrícula; se a matrícula não existir, cadastra automaticamente; se já existir, recupera as solicitações anteriores.
   - `LoginComissaoServlet` (`/loginComissao`): autentica usuário/senha e verifica `membro.getAtuacaoVigente() != null` (bloqueando ex-membros com mandato expirado).
   - `SolicitacaoServlet` (`/solicitacao`): anotado com `@MultipartConfig` (limite de 5 MB por arquivo, 10 MB por requisição). Salva o arquivo físico em um diretório **fora do Tomcat** (`~/uploads/sgha` ou `/uploads/sgha`) com nome único (`matricula_timestamp_nomeArquivo`) e grava apenas o caminho em `caminhoArquivoComprovante`.
   - `AvaliarSolicitacaoServlet` (`/avaliar`): processa o parecer da comissão/coordenador, ajuste do nome do documento e restauração de cancelamento.
   - `DownloadComprovanteServlet` (`/downloadComprovante`): lê o arquivo da pasta externa e exibe no navegador.
   - `LogoutServlet` (`/logout`) e `web.xml`: encerramento explícito da sessão e `<session-timeout>2</session-timeout>` (expiração automática após 2 minutos de inatividade).

---

## Parte 3 — Demonstração das Páginas (JSP) no Navegador

**Como iniciar:** clicar com botão direito no projeto **SGHA** $\rightarrow$ **Run As $\rightarrow$ Run on Server** (`http://localhost:8080/SGHA/`).

### Roteiro de cliques no navegador:

1. **Portal de Acesso (`index.jsp`)**:
   - Mostrar o acesso dividido entre Área do Discente e Área Administrativa/Comissões.
2. **Fluxo do Discente (`painel-discente.jsp`)**:
   - Entrar com `Carlos Eduardo da Silva` / matrícula `2023019823` (ou digitar uma matrícula nova para mostrar o cadastro automático no primeiro acesso).
   - Mostrar os cartões de horas consolidadas no topo e a tabela de solicitações:
     - Na solicitação `#104` (`CRIADA`): mostrar que é a única que exibe os botões **Editar** e **Excluir**, além de **Enviar** e **Cancelar**.
     - Clicar em **Nova Solicitação**, preencher os dados, anexar um PDF/imagem real e clicar em **Salvar e Enviar**.
     - Mostrar na mensagem verde o caminho externo onde o arquivo foi salvo (`.../uploads/sgha/...`) e clicar em **Ver comprovante** para abrir o arquivo.
     - Clicar no botão de relógio (**Histórico**) de qualquer solicitação para mostrar a trilha de auditoria e ressaltar que o discente vê o **Parecer Público**, mas **não vê** a Observação Administrativa.
   - Clicar em **Sair (Logout)**.
3. **Fluxo da Comissão e Perfis (`painel-comissao.jsp`)**:
   - **Teste de mandato expirado**: tentar logar com `hsouza` / `123456` e mostrar a mensagem de bloqueio por mandato expirado.
   - **Teste de Comissão Específica**: logar com `cmendes` / `123456` (Comissão de Extensão). Mostrar que ele só vê solicitações de Extensão (como a `#101`) e que o sistema exibe automaticamente a caixa vermelha de **Alerta de Duplicidade de Documento Identificada** (avisando que o mesmo aluno já submeteu o certificado *"Ação Social Lixo Zero"* na Comissão de Atividades Complementares, solicitação `#88`, com botão para ver o parecer anterior).
   - **Teste de Secretário (somente consulta)**: sair e logar com `msecretaria` / `123456`. Mostrar que ele consegue pesquisar e ver todas as solicitações e observações administrativas, mas todos os campos estão bloqueados para edição.
   - **Teste de Coordenador**: sair e logar com `tfranca` / `admin123`. Mostrar que ele visualiza todas as comissões, pode emitir parecer em qualquer solicitação e possui o botão exclusivo **Restaurar Solicitação Cancelada** quando uma solicitação está no status `CANCELADA`.

