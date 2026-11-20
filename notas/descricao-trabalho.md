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

