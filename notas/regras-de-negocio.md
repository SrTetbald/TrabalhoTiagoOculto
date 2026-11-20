# Regras de Negócio e Onde Foram Implementadas

1. **Cadastro automático do discente no primeiro acesso**:
   - `BancoMemoria.buscarOuCriarDiscente()` percorre a lista `discentes`; se achar a matrícula retorna o aluno, se não achar cria um novo e adiciona na lista.
2. **Sessão de 2 minutos e Logout**:
   - `<session-timeout>2</session-timeout>` no `web.xml` e `LogoutServlet` (`/logout`).
3. **Máquina de estados centralizada**:
   - `StatusSolicitacao.podeMudarPara()` valida quais transições são permitidas.
   - `Solicitacao.atualizarStatusPorDiscente()` e `Solicitacao.atualizarStatusPorComissao()` validam antes de trocar o status e sempre adicionam um `HistoricoStatus`.
   - Quando o discente aceita (`ACEITA`), `atualizarStatusPorDiscente()` já avança automaticamente para `CONCLUIDO`.
   - Quando é `RECURSO_COLEGIADO`, o `AvaliarServlet` define o próximo status como `CONCLUIDO`.
4. **Exclusão só em `CRIADA`**:
   - Validado no `SolicitacaoServlet` (`if (s.getStatus() != StatusSolicitacao.CRIADA)` lança erro) e na `discente.jsp` o botão Excluir só aparece quando `s.status == 'CRIADA'`.
5. **Restauração de `CANCELADA` só pelo Coordenador**:
   - Validado em `Solicitacao.atualizarStatusPorComissao()` (`if (this.status == StatusSolicitacao.CANCELADA)` exige `PerfilComissao.COORDENADOR`).
6. **Vigência de mandato e 5 perfis**:
   - `AtuacaoComissao.isVigenteHoje()` e `MembroComissao.getAtuacaoVigente()`.
   - `LoginComissaoServlet` bloqueia o login se `getAtuacaoVigente()` for `null`.
7. **Alerta de duplicidade cruzada**:
   - `BancoMemoria.buscarDuplicadas()` compara o nome do documento com as outras solicitações do mesmo aluno em qualquer comissão e exibe o alerta em `comissao.jsp`.
8. **Parecer Público vs. Observação Administrativa**:
   - `discente.jsp` só mostra `observacaoParecerPublico`; `observacaoAdministrativa` só aparece em `comissao.jsp`.
