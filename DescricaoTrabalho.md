# Universidade Federal Rural do Rio de Janeiro

**Instituto de Ciências Exatas**  
**Departamento de Computação**

**Tiago Cruz de França**  
Trabalho da Disciplina de Web 2 do Curso de Bacharelado em Sistemas de Informação  
Segundo período de 2026

# Sistema de Gestão de Horas Acadêmicas (SGHA)

## 1. Contexto e Objetivo

O **SGHA** é uma aplicação Java Web (MVC) projetada para gerenciar a submissão, verificação de duplicidades, avaliação por comissões específicas, fluxo de recursos e homologação de comprovantes de carga horária para discentes de Sistemas de Informação em três categorias: **Horas Complementares, Ações Extensionistas e Horas de Estágio**.

## 2. Módulos e Regras de Negócio

### 2.1 Módulo do Discente

- **Identificação / Cadastro Simplificado:** O discente acessa o sistema informando seu nome completo e matrícula. Se a matrícula não existir no banco de dados, o usuário é cadastrado automaticamente; caso já exista, suas solicitações anteriores são recuperadas e listadas.
- **Envio de Solicitação:**
  - Informa o Título/Nome do Documento e anexa o comprovante (upload/metadados).
  - Seleciona a Comissão Destino (Extensão, Atividades Complementares ou Estágio).
  - Informa a quantidade de horas pretendidas.
  - Dispõe de um Campo de Observação do Discente para informações adicionais.
- **Máquina de Estados e Operações Permitidas no Dashboard:**
  - **CRIADA:** Visível apenas pelo próprio discente. Operações disponíveis: Alterar, Cancelar, Enviar ou Excluir (único status em que a exclusão física/remoção é permitida).
  - **ENVIADA / PARECER PENDENTE:** A solicitação foi encaminhada para a comissão. A partir de ENVIADA, a solicitação não pode mais ser excluída, apenas cancelada.
  - **AVALIADA:** A comissão emitiu o parecer. O discente pode selecionar a operação Aceitar (avança para ACEITA) ou abrir um Recurso (avança para RECURSO).
  - **ACEITA:** O discente concordou com o parecer. O sistema altera o status para CONCLUÍDO.
  - **RECURSO:** Solicitação devolvida para a comissão reavaliar.
  - **AVALIADA APÓS RECURSO:** Comissão emitiu parecer do recurso. O discente pode Aceitar (vai para CONCLUÍDO) ou acionar o Recurso Colegiado (vai para RECURSO COLEGIADO).
  - **RECURSO COLEGIADO:** Enviado para decisão final do Colegiado/Coordenador. Após a emissão do parecer do Colegiado, o status avança automaticamente para CONCLUÍDO.
  - **CANCELADA:** Solicitação inativada. Não é exibida nas filas da comissão. Apenas o Coordenador pode restaurar uma solicitação cancelada para o seu status anterior.
  - **CONCLUÍDO:** Estado final estático. Nenhuma alteração ou exclusão é permitida (somente cancelamento administrativo ou leitura).
- **Consolidação Total de Carga Horária:** A busca por duplicidade e o cálculo acumulado tratam todos os documentos do aluno, independentemente da comissão que avaliou, impedindo que uma mesma ação física receba contabilização dupla acima das horas efetivamente desempenhadas.

Sobre o cadastro do discente, para simplificar o sistema, basta cadastrar o discente no seu primeiro acesso, quando ele fornecer seu nome e matrícula pela primeira vez. Cada cadastro depende da checagem da existência de um cadastro prévio do discente. Uma vez cadastrado, não será possível alterar ou excluir a conta.

Por questões de segurança mínima, permita que o discente encerre a sessão, fazendo o logout da sua conta. Isso representa apenas que, se outra pessoa usar o mesmo computador, não encontrará os dados dele acessíveis. Além disso, configure a sessão para ter um tempo de inatividade bem curto: **2 minutos**. Após esses dois minutos, qualquer sessão ativa deve ser encerrada.

### 2.2 Módulo da Comissão e Autenticação (RBAC)

Exige autenticação por login/senha pré-cadastrados. Ou seja, também não faremos o CRUD de cadastro do usuário do perfil “comissão”. As contas serão pré-cadastradas. Existirá o login para essas contas pré-cadastradas. Existem 5 perfis funcionais:

1. **Comissão de Extensão:** Visualiza e analisa apenas solicitações direcionadas para Extensão.
2. **Comissão de Atividades Complementares:** Visualiza e analisa apenas solicitações direcionadas para Complementares.
3. **Comissão de Estágio:** Visualiza e analisa apenas solicitações direcionadas para Estágio.
4. **Coordenador do Curso:** Pode avaliar e emitir parecer para qualquer solicitação (o sistema registra explicitamente no histórico que o parecer foi emitido pelo Coordenador). Possui a prerrogativa única de restaurar solicitações canceladas.
5. **Secretário do Curso:** Acesso de consulta global. Pode pesquisar discentes por nome/matrícula e visualizar todas as solicitações e pareceres, sem poder de decisão/parecer.

### 2.3 Recursos de Análise da Comissão

- **Ajuste de Nome do Documento:** A comissão pode corrigir o campo "Nome do Documento" cadastrado pelo discente caso haja divergência com o comprovante enviado.
- **Alerta Automático de Duplicidade Cruzada:** O sistema notifica a comissão caso exista outro registro ou solicitação (mesmo em outras comissões) para documento semelhante ou mesma ação do discente, exibindo o histórico e os pareceres anteriores para evitar contagem dupla.
- **Campos Duplos de Observação do Parecer:**
  - **Observação do Parecer (Pública):** Visível para o discente e para as comissões.
  - **Observação Administrativa (Privada):** Visível apenas para o Coordenador, Secretário e membros das Comissões.

Para que seja possível fazer login, a infraestrutura precisa ter uma classe para esse tipo de usuário. Isso deve ser guardado no banco de dados. O ideal, é fazer tudo com um canal seguro (https), guardando o hash da senha no banco. Você precisará guardar usuário e senha com diferentes perfis no banco, mas não precisará fazer a parte de segurança como requisito para o trabalho. Ou seja, você pode deixar a senha plena no banco de dados, já que não faremos a estrutura de recuperação de conta, troca de senha, etc. O cadastro desses usuários e seus perfis pode ser feito direto no banco. Para isso, você pode criar um script com o SQL necessário ou pode usar uma ferramenta como o PhpMyAdmin.php que é bem comum para banco de dados MySQL ou MariaDB.

Existem algumas observações práticas para comissão, coordenação e até secretário. É muito comum a ocorrência de mudanças, principalmente com a comissão e a coordenação. Os professores da comissão podem mudar, assim como o coordenador. Isso é menos comum para a secretaria, mas pode acontecer. Além disso, um docente que está na comissão, pode sair, virar coordenador, voltar para comissão depois de um tempo fora dela, etc. Considere essa dinâmica, mesmo para a secretaria, mesmo ela sendo menos frequente para esse perfil. É importante que os pareceres tenham a indicação de quem deu o parecer, qual era sua função no momento. Além disso, apenas contas ativas devem poder fazer login. Isso quer dizer que ex-coordenadores, ex-membros da comissão ou ex-secretários não podem mais fazer login no sistema, ou seja, suas contas passam a ficar inativas. Um usuário uma vez cadastrado não precisa ser novamente cadastrado caso volte a exercer uma função no sistema. Mas sua atuação tem um período de validade. Essa regra deve ser observada no sistema.

## 3. Arquitetura Técnica e Requisitos de Software

- **Padrão de Arquitetura:** MVC com Java Servlets (Controller), JSPs + JSTL (View) e JPA/Hibernate + PostgreSQL/MySQL (Model). Minha sugestão é usar o MySQL (MariaDB) com o PHPMyAdmin para gerenciar o banco.
- **Gerenciamento de Build:** Gradle (`build.gradle`) com empacotamento WAR.
- **Container Web Standalone:** Deploy funcional fora da IDE (Apache Tomcat Standalone local).

**Observação:** Reforço que o fato de não ter cadastro ou operações de gestão de contas dos perfis de usuário não significa que o objeto não exista no modelo. Para exemplificar (e servir de suporte, caso o grupo deseje), estou disponibilizando um exemplo de modelo. Ele precisa ser adaptado, especializado pelo grupo, se necessário. Também coloquei um exemplo simples com as principais páginas, a fim de exemplificar como eu pensaria no sistema.

Por fim, este documento tenta apresentar todas as informações necessárias para realização do projeto. Contudo, se necessário, qualquer informação que se faça necessária será determinada pelo professor e deve ser seguida pelo grupo.

## 4. Processo de Avaliação

É de responsabilidade do aluno ler e compreender o processo de avaliação. O professor se coloca à disposição para esclarecimentos se o aluno achar necessário. Mas qualquer esclarecimento deve ser realizado antes do final da avaliação. Parte do que é apresentado aqui foi acordado anteriormente em sala. O que será apresentado leva em consideração o que foi conversado e detalha como será a avaliação.

A avaliação do projeto será contínua e composta por três etapas complementares: o acompanhamento do desenvolvimento por meio de entregas parciais, a entrega técnica do produto final em funcionamento e a apresentação/arguição oral da equipe.

A Tabela 1 descreve a formação das notas e os pesos. A nota final é composta pela nota do trabalho (comum para todos os membros do grupo) multiplicada pela nota da apresentação que varia de 0 a 1. Dessa forma, mesmo que a nota do trabalho seja 10, um membro do grupo que tiver um avaliação ruim na apresentação/arguição pode ficar com nota individual baixa. A nota do trabalho será determinada com relação a adequação e entrega no prazo dos entregáveis parciais (descritos abaixo).

**Tabela 1: formação da nota do trabalho e nota final do aluno com peso da apresentação**

| Etapa de Avaliação | Descrição Resumida | Peso |
|---|---|---:|
| I. Entregas Parciais (Checkpoints) | Evolução incremental do projeto, modelagem e código-fonte no repositório. | 50% |
| II. Produto Técnico Final | Aplicação completa, aderência às regras de negócio, arquitetura e deploy standalone. | 50% |
| III. Apresentação e Arguição Final | Demonstração ao vivo do sistema rodando e domínio técnico individual dos integrantes. | [0..1] |

### 4.1 Entregas Parciais

O objetivo é garantir que o grupo evolua de forma constante, evitando acúmulo de tarefas na última semana e permitindo correções antecipadas.

- **Entregável 1:** definição dos membros dos grupos (13-11).
- **Entregável 2:** modelagem, e configuração inicial de um projeto java web (20-11).
  - Classes da camada “model” (Entidades/classes, Enums de status/perfis, vigência de mandatos e trilha de auditoria) com classe Java com método `main` simulando a criação e relação entre objetos para validar o modelo.
  - Criação dos controladores, acessando o modelo, usando estrutura de memória para simular persistência de dados básicos (possibilidade de uso de dados mockados, sem obrigatoriedade). A visualização deve ser capaz de acessar os controladores que, por sua vez, devem ser capazes de instanciar as classes do modelo.
  - Protótipos/páginas iniciais (JSP/HTML) do portal de acesso e formulários principais. Configurações básicas de upload com configuração de documentos sendo salvos em pastas (diretórios) fora do ambiente do container web. O Quadro 1 exemplifica um servlet fictício mapeado com `@MultipartConfig` e alguns parâmetros que delimitam o tamanho do arquivo enviado. No corpo do servlet, um exemplo do que pode ser feito para lidar com o arquivo.

**Quadro 1. Exemplo de anotação para lidar com upload de arquivo.**

```java
@WebServlet("/solicitacao")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 5,       // 5 MB
    maxRequestSize = 1024 * 1024 * 10    // 10 MB
)
public class SolicitacaoServlet extends HttpServlet {
    // Dentro do doPost:
    // Part filePart = request.getPart("arquivoComprovante");
    // Salva o arquivo em uma pasta local no servidor (ex:
    // /uploads/sgha/)
    // e grava apenas o caminho/nome único gerado no atributo
    // caminhoArquivoComprovante da entidade Solicitacao.
}
```

- **Entregável 3:** fluxo principal e persistência.
  - Mudança do projeto para seguir uma estrutura configurada via Gradle (`build.gradle` com dependências mapeadas). Deve-se usar auxílio de IA (Gemini, ChatGPT ou Cloud) para montar essa estrutura. Será um novo projeto (não mais um projeto java web dinâmico), pois o gradle lidará com as dependências de persistência (drive jdbc, libs hibernate, dependências web jakarta) automaticamente, facilitando a gestão de todas as dependências do projeto. Os artefatos criados no projeto java-web do entregável anterior devem ser mapeados nesse novo projeto.
  - Mapeamento JPA do POJO.
  - Integração do DAO ao projeto, com implementação das classes especializadas para o projeto.
  - Configuração adequada do projeto e do ORM para geração do esquema no banco de dados relacional. Para isso, lembre-se de ter seu MySQL rodando e o banco criado (sem esqueça de tabelas).
  - Implementação funcional dos Servlets de identificação do discente, autenticação da comissão (com checagem de vigência) e submissão de comprovantes (`@MultipartConfig`).
  - Operações básicas de listagem e transições iniciais de status persistidas via ORM.

### 4.2 Produto Técnico Final

Avaliação da arquitetura implementada e do empacotamento final da aplicação. Deve estar funcionando o ciclo de vida das solicitações (**CRIADA, ENVIADA, AVALIADA, RECURSO, AVALIADA APÓS RECURSO, RECURSO COLEGIADO, CANCELADA e CONCLUÍDO**), respeitando bloqueios de exclusão e permissões de restauração exclusivas do Coordenador. Detecção automática e alerta de duplicidade de documentos entre diferentes comissões, exibindo pareceres anteriores. Controle de acesso dos 5 perfis (Comissões específicas, Coordenador e Secretário) e à visibilidade distinta entre Parecer Público e Observação Administrativa. Possibilidade de edição do nome do documento pela comissão e persistência íntegra da trilha de auditoria (`HistoricoStatus`).

Observe a corretude do uso do padrão MVC. Mantenha o código limpo e use nomes sugestivos. Não temos nenhum objetivo com ofuscação de código nessa etapa, então, esse argumento não será aceito para fins de justificativa.

Também gere o WAR para colocar em produção standalone com Gradle. Use seu container por fora do Eclipse (ambiente de desenvolvimento). O objetivo é que você cuide das dependências com essa mudança de onde o projeto está rodando. Mas como estamos usando o Gradle para gerir todas as etapas, isso será facilitado no projeto.

### 4.3 Apresentação

O objetivo dessa etapa é avaliar o domínio individual de cada membro do grupo. O processo de desenvolvimento, o uso ou não de IA, repositórios de código, etc. é de responsabilidade do grupo. Mas cada aluno deve entender a parte de Java Web (POJO, Servlets e JSPs) e a integração do projeto OO com banco de dados relacional usando ORM.
