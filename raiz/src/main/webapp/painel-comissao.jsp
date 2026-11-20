<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>SGHA - Painel de Avaliação da Comissão</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold text-warning" href="${pageContext.request.contextPath}/painelComissao">SGHA | Módulo de Gestão</a>
        <div class="d-flex align-items-center gap-3">
            <span class="navbar-text text-white">
                <i class="bi bi-shield-lock-fill text-warning"></i>
                Perfil: <strong><c:out value="${atuacaoVigente.perfil.descricao}" /> / <c:out value="${membroLogado.nomeCompleto}" /></strong>
                <small class="text-secondary ms-1">(Vigência: ${atuacaoVigente.resumoVigencia})</small>
            </span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-warning">
                <i class="bi bi-box-arrow-right"></i> Sair
            </a>
        </div>
    </div>
</nav>

<div class="container my-4">
    <!-- Mensagens de retorno -->
    <c:if test="${not empty mensagemSucesso}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i><c:out value="${mensagemSucesso}" />
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty mensagemErro}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${mensagemErro}" />
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${atuacaoVigente.secretario}">
        <div class="alert alert-info py-2">
            <i class="bi bi-eye-fill me-2"></i><strong>Modo Consulta (Secretaria do Curso):</strong>
            você pode pesquisar qualquer discente e visualizar todas as solicitações, comprovantes e pareceres, sem permissão para emitir decisões.
        </div>
    </c:if>

    <!-- Filtros de Pesquisa para a Comissão / Secretário / Coordenador -->
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/painelComissao" method="GET" class="row g-3 align-items-center">
                <div class="col-md-5">
                    <input type="text" name="busca" class="form-control"
                           value="<c:out value='${buscaAtual}'/>"
                           placeholder="Buscar por nome do discente, matrícula ou documento...">
                </div>
                <div class="col-md-4">
                    <select name="filtroComissao" class="form-select" ${not atuacaoVigente.coordenador and not atuacaoVigente.secretario ? 'disabled' : ''}>
                        <option value="TODAS" ${filtroComissaoAtual == 'TODAS' ? 'selected' : ''}>Todas as Comissões Permitidas</option>
                        <option value="EXTENSAO" ${filtroComissaoAtual == 'EXTENSAO' ? 'selected' : ''}>Extensão</option>
                        <option value="COMPLEMENTAR" ${filtroComissaoAtual == 'COMPLEMENTAR' ? 'selected' : ''}>Atividades Complementares</option>
                        <option value="ESTAGIO" ${filtroComissaoAtual == 'ESTAGIO' ? 'selected' : ''}>Estágio</option>
                    </select>
                </div>
                <div class="col-md-3 d-flex gap-2">
                    <button type="submit" class="btn btn-dark w-100"><i class="bi bi-search"></i> Pesquisar</button>
                    <a href="${pageContext.request.contextPath}/painelComissao" class="btn btn-outline-secondary">Limpar</a>
                </div>
            </form>
        </div>
    </div>

    <!-- Fila de Solicitações Visíveis para o Perfil -->
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
            <h6 class="mb-0 fw-bold"><i class="bi bi-list-check me-2"></i>Fila de Solicitações Visíveis (${solicitacoesVisiveis.size()})</h6>
            <small class="text-warning">Clique em "Selecionar" para avaliar ou consultar detalhes</small>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>#ID</th>
                            <th>Discente (Matrícula)</th>
                            <th>Documento</th>
                            <th>Comissão</th>
                            <th>Horas (Sol/Conc)</th>
                            <th>Status</th>
                            <th>Ação</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty solicitacoesVisiveis}">
                            <tr>
                                <td colspan="7" class="text-center py-3 text-muted">
                                    Nenhuma solicitação encontrada para os filtros e permissões do seu perfil.
                                </td>
                            </tr>
                        </c:if>
                        <c:forEach var="item" items="${solicitacoesVisiveis}">
                            <tr class="${not empty solicitacaoSelecionada and solicitacaoSelecionada.id == item.id ? 'table-warning' : ''}">
                                <td class="fw-bold">#${item.id}</td>
                                <td>
                                    <c:out value="${item.discente.nomeCompleto}" />
                                    <small class="text-muted">(${item.discente.matricula})</small>
                                </td>
                                <td><c:out value="${item.nomeDocumento}" /></td>
                                <td><span class="badge bg-secondary">${item.tipoHoras.nomeCurto}</span></td>
                                <td>
                                    ${item.horasSolicitadas}h /
                                    <c:choose>
                                        <c:when test="${item.horasConcedidas != null}">${item.horasConcedidas}h</c:when>
                                        <c:otherwise>--</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span class="badge bg-dark">${item.status}</span></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/painelComissao?id=${item.id}&busca=${buscaAtual}&filtroComissao=${filtroComissaoAtual}"
                                       class="btn btn-sm ${not empty solicitacaoSelecionada and solicitacaoSelecionada.id == item.id ? 'btn-dark text-warning' : 'btn-outline-dark'}">
                                        <i class="bi bi-folder2-open"></i> Selecionar
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Avaliação de Solicitação Selecionada -->
    <c:if test="${not empty solicitacaoSelecionada}">
        <div class="card border-0 shadow-sm border-start border-4 border-warning mb-4">
            <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
                <div>
                    <h5 class="fw-bold mb-0">
                        Avaliação da Solicitação #${solicitacaoSelecionada.id} —
                        <span class="text-primary"><c:out value="${solicitacaoSelecionada.discente.nomeCompleto}" /></span>
                        <small class="text-muted">(Matrícula: ${solicitacaoSelecionada.discente.matricula} | Total já concluído: ${solicitacaoSelecionada.discente.totalHorasConcluidas}h)</small>
                    </h5>
                </div>
                <span class="badge bg-warning text-dark fs-6">Status: ${solicitacaoSelecionada.status}</span>
            </div>
            <div class="card-body">

                <!-- ALERTA DE DUPLICIDADE AUTOMÁTICA CRUZADA -->
                <c:if test="${not empty duplicidadesCruzadas}">
                    <c:forEach var="dup" items="${duplicidadesCruzadas}">
                        <div class="alert alert-danger d-flex align-items-center" role="alert">
                            <i class="bi bi-exclamation-triangle-fill fs-3 me-3"></i>
                            <div>
                                <strong>ALERTA DE DUPLICIDADE DE DOCUMENTO IDENTIFICADA!</strong><br>
                                O discente <strong><c:out value="${solicitacaoSelecionada.discente.nomeCompleto}" /></strong>
                                já submeteu outra solicitação com documento semelhante
                                ("<em><c:out value="${dup.nomeDocumento}" /></em>") para a
                                <strong><c:out value="${dup.tipoHoras.descricao}" /></strong>
                                (Solicitação #${dup.id} - Status: ${dup.status}
                                <c:if test="${dup.horasConcedidas != null}"> - Concedidas: ${dup.horasConcedidas}h</c:if>).
                                <button class="btn btn-sm btn-outline-danger ms-2" type="button"
                                        data-bs-toggle="collapse" data-bs-target="#detalhesDuplicidade${dup.id}">
                                    Ver detalhes e parecer anterior
                                </button>
                            </div>
                        </div>

                        <!-- Painel Colapsável de Detalhes da Duplicidade -->
                        <div class="collapse mb-3" id="detalhesDuplicidade${dup.id}">
                            <div class="card card-body bg-light text-dark">
                                <h6><strong>Histórico Cruzado: Solicitação #${dup.id} (${dup.tipoHoras.nomeCurto})</strong></h6>
                                <p class="mb-1">
                                    <strong>Parecer Anterior:</strong>
                                    "<c:out value="${not empty dup.observacaoParecerPublico ? dup.observacaoParecerPublico : 'Sem parecer registrado ainda.'}" />"
                                </p>
                                <c:if test="${not empty dup.observacaoAdministrativa}">
                                    <p class="mb-1 text-danger">
                                        <strong>Obs. Administrativa Anterior:</strong>
                                        "<c:out value="${dup.observacaoAdministrativa}" />"
                                    </p>
                                </c:if>
                                <p class="mb-0">
                                    <strong>Atenção:</strong> Avalie se a concessão de novas horas por esta comissão não resultará em contabilização dupla superior ao tempo real desempenhado na ação.
                                </p>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>

                <form action="${pageContext.request.contextPath}/avaliar" method="POST">
                    <input type="hidden" name="idSolicitacao" value="${solicitacaoSelecionada.id}">

                    <div class="row align-items-end">
                        <!-- Ajuste do Nome do Documento -->
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Nome / Título do Documento (Ajustável pela Comissão)</label>
                            <div class="input-group">
                                <input type="text" class="form-control" name="nomeDocumento"
                                       value="<c:out value='${solicitacaoSelecionada.nomeDocumento}'/>"
                                       ${atuacaoVigente.secretario ? 'readonly' : ''}>
                                <c:if test="${not atuacaoVigente.secretario}">
                                    <button class="btn btn-outline-secondary" type="submit" name="decisao" value="AJUSTAR_NOME"
                                            formnovalidate title="Salvar alteração no nome">
                                        <i class="bi bi-check"></i> Ajustar
                                    </button>
                                </c:if>
                            </div>
                        </div>

                        <!-- BOTÃO PARA BAIXAR/VISUALIZAR O COMPROVANTE ANEXADO -->
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-bold d-block">Comprovante Anexado</label>
                            <a href="${pageContext.request.contextPath}/downloadComprovante?id=${solicitacaoSelecionada.id}"
                               target="_blank" class="btn btn-outline-primary w-100">
                                <i class="bi bi-file-earmark-pdf-fill"></i> Abrir Arquivo
                            </a>
                        </div>

                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-bold">
                                Horas Concedidas <small class="text-muted">(Sol: ${solicitacaoSelecionada.horasSolicitadas}h)</small>
                            </label>
                            <input type="number" class="form-control" name="horasConcedidas" min="0"
                                   value="${solicitacaoSelecionada.horasConcedidas != null ? solicitacaoSelecionada.horasConcedidas : solicitacaoSelecionada.horasSolicitadas}"
                                   ${atuacaoVigente.secretario ? 'readonly' : ''}>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold text-muted">Observação do Discente</label>
                        <p class="p-2 bg-light rounded border mb-0">
                            "<c:out value="${not empty solicitacaoSelecionada.observacaoDiscente ? solicitacaoSelecionada.observacaoDiscente : 'Nenhuma observação informada pelo discente.'}" />"
                        </p>
                    </div>

                    <!-- Campo de Observação do Parecer (Público) -->
                    <div class="mb-3">
                        <label class="form-label fw-bold text-primary">Parecer da Comissão (Visível para o Discente)</label>
                        <textarea class="form-control" name="observacaoParecerPublico" rows="3" required
                                  ${atuacaoVigente.secretario ? 'readonly' : ''}
                                  placeholder="Descreva o parecer fundamentado para o discente..."><c:out value="${solicitacaoSelecionada.observacaoParecerPublico}" /></textarea>
                    </div>

                    <!-- Campo de Observação Administrativa (Privada) -->
                    <div class="mb-3">
                        <label class="form-label fw-bold text-danger">
                            Observação Administrativa (Privada - Visível apenas para Comissões, Coordenação e Secretaria)
                        </label>
                        <textarea class="form-control" name="observacaoAdministrativa" rows="2"
                                  ${atuacaoVigente.secretario ? 'readonly' : ''}
                                  placeholder="Ex: Discente tentou usar o mesmo certificado em outra comissão. Foi abatida a diferença de horas."><c:out value="${solicitacaoSelecionada.observacaoAdministrativa}" /></textarea>
                    </div>

                    <!-- Ações do Coordenador / Comissão -->
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <!-- Prerrogativa exclusiva do Coordenador: Restaurar solicitação CANCELADA -->
                            <c:if test="${atuacaoVigente.coordenador and solicitacaoSelecionada.status == 'CANCELADA'}">
                                <button type="submit" name="decisao" value="RESTAURAR" formnovalidate
                                        class="btn btn-warning fw-bold me-2">
                                    <i class="bi bi-arrow-counterclockwise"></i>
                                    Restaurar Solicitação Cancelada (Voltar para ${solicitacaoSelecionada.statusAnteriorAoCancelamento})
                                </button>
                            </c:if>
                            <!-- Cancelamento Administrativo pelo Coordenador em solicitação CONCLUIDO -->
                            <c:if test="${atuacaoVigente.coordenador and solicitacaoSelecionada.status == 'CONCLUIDO'}">
                                <button type="submit" name="decisao" value="CANCELAR_ADMIN" formnovalidate
                                        class="btn btn-outline-danger me-2">
                                    <i class="bi bi-slash-circle"></i> Cancelamento Administrativo (Coordenador)
                                </button>
                            </c:if>
                        </div>

                        <div>
                            <c:choose>
                                <c:when test="${podeAvaliarSelecionada}">
                                    <button type="submit" name="decisao" value="REJEITAR" class="btn btn-outline-danger me-2">
                                        Indeferir / Rejeitar (0h)
                                    </button>
                                    <button type="submit" name="decisao" value="APROVAR" class="btn btn-success">
                                        <i class="bi bi-check2-circle"></i> Emitir Parecer
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted small">
                                        <i class="bi bi-lock-fill"></i>
                                        <c:choose>
                                            <c:when test="${atuacaoVigente.secretario}">
                                                Perfil Secretário possui acesso apenas para leitura.
                                            </c:when>
                                            <c:otherwise>
                                                Esta solicitação está no status <strong>${solicitacaoSelecionada.status}</strong> e não aguarda parecer do seu perfil no momento.
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </form>

                <!-- Trilha de Auditoria Completa (HistoricoStatus) -->
                <hr class="my-4">
                <h6 class="fw-bold mb-2"><i class="bi bi-clock-history me-1"></i>Trilha de Auditoria (Histórico de Status)</h6>
                <div class="table-responsive">
                    <table class="table table-sm table-bordered mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Data/Hora</th>
                                <th>Status</th>
                                <th>Responsável (Perfil no momento)</th>
                                <th>Observação / Justificativa</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="h" items="${solicitacaoSelecionada.historicos}">
                                <tr>
                                    <td>${h.dataRegistroFormatada}</td>
                                    <td><span class="badge bg-secondary">${h.status}</span></td>
                                    <td><c:out value="${h.alteradoPor}" /></td>
                                    <td><c:out value="${h.observacaoOuParecer}" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

            </div>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
