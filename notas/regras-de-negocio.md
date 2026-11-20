# Regras de Negócio e Onde Foram Implementadas

Guia rápido para explicar o código na arguição oral:

1. **Cadastro simplificado do discente no primeiro acesso (sem alteração/exclusão de conta)**:
   - Implementado em `RepositorioMemoria.buscarOuCadastrarDiscente()` e chamado por `AcessoDiscenteServlet`. Se a matrícula já existe, apenas retorna o `Discente` com suas solicitações; se não existe, instancia e guarda no mapa.
2. **Logout e sessão de 2 minutos de inatividade**:
   - Timeout configurado em `src/main/webapp/WEB-INF/web.xml` (`<session-timeout>2</session-timeout>`).
   - Logout implementado em `LogoutServlet` (`/logout`) chamando `sessao.invalidate()`.
3. **Máquina de estados centralizada**:
   - As transições permitidas estão em `StatusSolicitacao.podeTransicionarPara()`.
   - A execução das transições e criação do histórico ficam em `Solicitacao.atualizarStatusPorDiscente()`, `Solicitacao.atualizarStatusPorComissao()` e `Solicitacao.emitirParecerDaComissao()`.
4. **Exclusão física permitida apenas em `CRIADA`**:
   - Validada em `StatusSolicitacao.permiteExclusao()` (`this == CRIADA`) e em `RepositorioMemoria.excluirSolicitacao()`.
5. **Aceite do discente (`ACEITA -> CONCLUIDO`) e decisão do Colegiado (`RECURSO_COLEGIADO -> CONCLUIDO`)**:
   - Em `Solicitacao.atualizarStatusPorDiscente()`: quando o discente aceita (`StatusSolicitacao.ACEITA`), o método registra `ACEITA` no histórico e avança imediatamente o status para `CONCLUIDO`.
   - Em `Solicitacao.emitirParecerDaComissao()`: quando a solicitação está em `RECURSO_COLEGIADO`, o parecer final do Coordenador avança o status diretamente para `CONCLUIDO`.
6. **Cancelamento e restauração exclusiva pelo Coordenador**:
   - Ao cancelar, `Solicitacao` guarda o estado em `statusAnteriorAoCancelamento`.
   - Solicitações `CANCELADA` não aparecem nas filas das comissões (`RepositorioMemoria.listarSolicitacoesVisiveisParaComissao`), sendo exibidas apenas para o `COORDENADOR`.
   - Em `Solicitacao.atualizarStatusPorComissao()`, sair de `CANCELADA` exige `atuacao.isCoordenador()`.
7. **Vigência de mandato e 5 perfis (RBAC)**:
   - `AtuacaoComissao.isVigenteHoje()` checa `ativo`, `dataInicio` e `dataValidade`.
   - `LoginComissaoServlet` bloqueia o login se `membro.getAtuacaoVigente()` retornar `null` (ex.: conta `hsouza`).
   - `AtuacaoComissao.podeVisualizarTipo()` restringe cada comissão ao seu `TipoHoras`, enquanto `COORDENADOR` e `SECRETARIO` visualizam todas.
   - `AtuacaoComissao.podeAvaliar()` bloqueia qualquer decisão do `SECRETARIO`.
8. **Alerta automático de duplicidade cruzada**:
   - `Solicitacao.temDocumentoSemelhante()` normaliza o nome do documento e compara com outras solicitações do mesmo aluno em qualquer comissão.
   - `RepositorioMemoria.buscarDuplicidadesCruzadas()` é chamado no `PainelComissaoServlet` e exibido em destaque vermelho no topo de `painel-comissao.jsp`.
9. **Observação Pública vs. Observação Administrativa (Privada)**:
   - `painel-discente.jsp` exibe apenas `sol.observacaoParecerPublico`. O atributo `observacaoAdministrativa` só existe em `painel-comissao.jsp`.

