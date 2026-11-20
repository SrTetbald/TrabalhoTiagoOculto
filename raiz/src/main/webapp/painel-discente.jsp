<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>SGHA - Painel do Discente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/painelDiscente">SGHA | Sistema de Horas</a>
        <div class="d-flex align-items-center gap-3">
            <span class="navbar-text text-white">
                <i class="bi bi-person-circle"></i> Discente: <strong><c:out value="${discente.nomeCompleto}" /></strong>
                (Matrícula: <c:out value="${discente.matricula}" />)
            </span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-light">
                <i class="bi bi-box-arrow-right"></i> Sair
            </a>
        </div>
    </div>
</nav>

<div class="container my-4">
    <!-- Feedback de operações -->
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

    <!-- Consolidação de Carga Horária Global -->
    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card border-0 shadow-sm text-center bg-white">
                <div class="card-body">
                    <h6 class="text-muted">Ações Extensionistas (Concluídas)</h6>
                    <h3 class="text-primary fw-bold">${discente.horasConcluidasExtensao} / 60h</h3>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border-0 shadow-sm text-center bg-white">
                <div class="card-body">
                    <h6 class="text-muted">Atividades Complementares (Concluídas)</h6>
                    <h3 class="text-success fw-bold">${discente.horasConcluidasComplementar} / 120h</h3>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border-0 shadow-sm text-center bg-white">
                <div class="card-body">
                    <h6 class="text-muted">Horas de Estágio (Concluídas)</h6>
                    <h3 class="text-warning fw-bold">${discente.horasConcluidasEstagio} / 300h</h3>
                </div>
            </div>
        </div>
    </div>

    <!-- Cabeçalho e Botão de Nova Solicitação -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="fw-bold mb-0">Minhas Solicitações</h4>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalNovaSolicitacao">
            <i class="bi bi-plus-lg"></i> Nova Solicitação
        </button>
    </div>

    <!-- Tabela de Solicitações do Aluno -->
    <div class="card border-0 shadow-sm">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>#ID</th>
                            <th>Nome do Documento</th>
                            <th>Comissão Destino</th>
                            <th>Horas (Sol/Conc)</th>
                            <th>Status</th>
                            <th>Parecer Público</th>
                            <th>Operações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty solicitacoes}">
                            <tr>
                                <td colspan="7" class="text-center py-4 text-muted">
                                    Nenhuma solicitação registrada ainda. Clique em <strong>Nova Solicitação</strong> para enviar seu primeiro comprovante.
                                </td>
                            </tr>
                        </c:if>

                        <c:forEach var="sol" items="${solicitacoes}">
                            <tr>
                                <td class="fw-bold">#${sol.id}</td>
                                <td>
                                    <div><c:out value="${sol.nomeDocumento}" /></div>
                                    <a href="${pageContext.request.contextPath}/downloadComprovante?id=${sol.id}" target="_blank" class="small text-decoration-none">
                                        <i class="bi bi-paperclip"></i> Ver comprovante
                                    </a>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${sol.tipoHoras == 'EXTENSAO'}">
                                            <span class="badge bg-primary">Extensão</span>
                                        </c:when>
                                        <c:when test="${sol.tipoHoras == 'COMPLEMENTAR'}">
                                            <span class="badge bg-secondary">Complementares</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning text-dark">Estágio</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    ${sol.horasSolicitadas}h /
                                    <c:choose>
                                        <c:when test="${sol.horasConcedidas != null}">${sol.horasConcedidas}h</c:when>
                                        <c:otherwise>--</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${sol.status == 'CRIADA'}">
                                            <span class="badge bg-info text-dark">CRIADA</span>
                                        </c:when>
                                        <c:when test="${sol.status == 'ENVIADA' || sol.status == 'PARECER_PENDENTE'}">
                                            <span class="badge bg-secondary">${sol.status}</span>
                                        </c:when>
                                        <c:when test="${sol.status == 'AVALIADA' || sol.status == 'AVALIADA_APOS_RECURSO'}">
                                            <span class="badge bg-warning text-dark">${sol.status}</span>
                                        </c:when>
                                        <c:when test="${sol.status == 'RECURSO' || sol.status == 'RECURSO_COLEGIADO'}">
                                            <span class="badge bg-danger">${sol.status}</span>
                                        </c:when>
                                        <c:when test="${sol.status == 'CONCLUIDO'}">
                                            <span class="badge bg-success">CONCLUÍDO</span>
                                        </c:when>
                                        <c:when test="${sol.status == 'CANCELADA'}">
                                            <span class="badge bg-dark">CANCELADA</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${sol.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <!-- Regra: O discente vê apenas o Parecer Público; NUNCA vê a observação administrativa -->
                                    <c:choose>
                                        <c:when test="${not empty sol.observacaoParecerPublico}">
                                            <c:out value="${sol.observacaoParecerPublico}" />
                                        </c:when>
                                        <c:when test="${sol.status == 'CRIADA'}">
                                            <em class="text-muted">Ainda não enviada</em>
                                        </c:when>
                                        <c:otherwise>
                                            <em class="text-muted">Aguardando análise</em>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="d-flex flex-wrap gap-1">
                                        <!-- CRIADA: Enviar, Editar, Cancelar, Excluir (único status que permite exclusão) -->
                                        <c:if test="${sol.status == 'CRIADA'}">
                                            <form action="${pageContext.request.contextPath}/solicitacao" method="POST" class="d-inline">
                                                <input type="hidden" name="idSolicitacao" value="${sol.id}">
                                                <input type="hidden" name="acao" value="enviar">
                                                <button type="submit" class="btn btn-sm btn-outline-success" title="Enviar para a Comissão">
                                                    <i class="bi bi-send"></i> Enviar
                                                </button>
                                            </form>
                                            <button type="button" class="btn btn-sm btn-outline-primary"
                                                    data-bs-toggle="modal" data-bs-target="#modalEditar${sol.id}" title="Alterar">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <form action="${pageContext.request.contextPath}/solicitacao" method="POST" class="d-inline">
                                                <input type="hidden" name="idSolicitacao" value="${sol.id}">
                                                <input type="hidden" name="acao" value="cancelar">
                                                <button type="submit" class="btn btn-sm btn-outline-warning" title="Cancelar">
                                                    <i class="bi bi-slash-circle"></i>
                                                </button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/solicitacao" method="POST" class="d-inline"
                                                  onsubmit="return confirm('Deseja realmente excluir este rascunho (#${sol.id})?');">
                                                <input type="hidden" name="idSolicitacao" value="${sol.id}">
                                                <input type="hidden" name="acao" value="excluir">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Excluir (Somente CRIADA)">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </form>
                                        </c:if>

                                        <!-- ENVIADA / PARECER_PENDENTE / RECURSO / RECURSO_COLEGIADO: apenas Cancelar -->
                                        <c:if test="${sol.status == 'ENVIADA' || sol.status == 'PARECER_PENDENTE' || sol.status == 'RECURSO' || sol.status == 'RECURSO_COLEGIADO'}">
                                            <form action="${pageContext.request.contextPath}/solicitacao" method="POST" class="d-inline">
                                                <input type="hidden" name="idSolicitacao" value="${sol.id}">
                                                <input type="hidden" name="acao" value="cancelar">
                                                <button type="submit" class="btn btn-sm btn-outline-warning" title="Cancelar">
                                                    <i class="bi bi-slash-circle"></i> Cancelar
                                                </button>
                                            </form>
                                        </c:if>

                                        <!-- AVALIADA: Aceitar (-> ACEITA -> CONCLUIDO) ou Abrir Recurso (-> RECURSO) ou Cancelar -->
                                        <c:if test="${sol.status == 'AVALIADA'}">
                                            <form action="${pageContext.request.contextPath}/solicitacao" method="POST" class="d-inline">
                                                <input type="hidden" name="idSolicitacao" value="${sol.id}">
                                                <input type="hidden" name="acao" value="aceitar">
                                                <button type="submit" class="btn btn-sm btn-success" title="Aceitar Parecer">
                                                    <i class="bi bi-check-circle"></i> Aceitar
                                                </button>
                                            </form>
                                            <button type="button" class="btn btn-sm btn-outline-danger"
                                                    data-bs-toggle="modal" data-bs-target="#modalRecurso${sol.id}">
                                                <i class="bi bi-arrow-counterclockwise"></i> Recurso
                                            </button>
                                            <form action="${pageContext.request.contextPath}/solicitacao" method="POST" class="d-inline">
                                                <input type="hidden" name="idSolicitacao" value="${sol.id}">
                                                <input type="hidden" name="acao" value="cancelar">
                                                <button type="submit" class="btn btn-sm btn-outline-warning" title="Cancelar">
                                                    <i class="bi bi-slash-circle"></i>
                                                </button>
                                            </form>
                                        </c:if>

                                        <!-- AVALIADA_APOS_RECURSO: Aceitar (-> CONCLUIDO) ou Recurso Colegiado (-> RECURSO_COLEGIADO) -->
                                        <c:if test="${sol.status == 'AVALIADA_APOS_RECURSO'}">
                                            <form action="${pageContext.request.contextPath}/solicitacao" method="POST" class="d-inline">
                                                <input type="hidden" name="idSolicitacao" value="${sol.id}">
                                                <input type="hidden" name="acao" value="aceitar">
                                                <button type="submit" class="btn btn-sm btn-success" title="Aceitar Parecer do Recurso">
                                                    <i class="bi bi-check-circle"></i> Aceitar
                                                </button>
                                            </form>
                                            <button type="button" class="btn btn-sm btn-danger"
                                                    data-bs-toggle="modal" data-bs-target="#modalColegiado${sol.id}">
                                                <i class="bi bi-bank"></i> Recurso Colegiado
                                            </button>
                                        </c:if>

                                        <!-- Botão para ver Histórico / Trilha de Auditoria -->
                                        <button type="button" class="btn btn-sm btn-outline-secondary"
                                                data-bs-toggle="modal" data-bs-target="#modalHistorico${sol.id}" title="Ver Histórico de Status">
                                            <i class="bi bi-clock-history"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Modais individuais por solicitação (Editar CRIADA, Abrir Recurso, Recurso Colegiado, Histórico) -->
<c:forEach var="sol" items="${solicitacoes}">
    <!-- Modal Editar Solicitação CRIADA -->
    <c:if test="${sol.status == 'CRIADA'}">
        <div class="modal fade" id="modalEditar${sol.id}" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="${pageContext.request.contextPath}/solicitacao" method="POST" enctype="multipart/form-data">
                        <input type="hidden" name="idSolicitacao" value="${sol.id}">
                        <div class="modal-header bg-primary text-white">
                            <h5 class="modal-title">Alterar Solicitação #${sol.id}</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Comissão / Tipo de Carga Horária</label>
                                <select name="tipoHoras" class="form-select" required>
                                    <option value="EXTENSAO" ${sol.tipoHoras == 'EXTENSAO' ? 'selected' : ''}>Comissão de Ações Extensionistas</option>
                                    <option value="COMPLEMENTAR" ${sol.tipoHoras == 'COMPLEMENTAR' ? 'selected' : ''}>Comissão de Atividades Complementares</option>
                                    <option value="ESTAGIO" ${sol.tipoHoras == 'ESTAGIO' ? 'selected' : ''}>Comissão de Estágio</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Nome/Título do Documento Comprovante</label>
                                <input type="text" name="nomeDocumento" class="form-control" value="<c:out value='${sol.nomeDocumento}'/>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Substituir Arquivo Comprobatório (Opcional)</label>
                                <input class="form-control" type="file" name="arquivoComprovante" accept=".pdf,.png,.jpg,.jpeg">
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Horas Pretendidas</label>
                                <input type="number" name="horasSolicitadas" class="form-control" value="${sol.horasSolicitadas}" required min="1">
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Observações do Discente</label>
                                <textarea name="observacaoDiscente" class="form-control" rows="3"><c:out value="${sol.observacaoDiscente}"/></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" name="acao" value="salvar_rascunho" class="btn btn-secondary">Salvar Alterações (CRIADA)</button>
                            <button type="submit" name="acao" value="enviar_comissao" class="btn btn-primary">Salvar e Enviar</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Modal Abrir Recurso (status AVALIADA -> RECURSO) -->
    <c:if test="${sol.status == 'AVALIADA'}">
        <div class="modal fade" id="modalRecurso${sol.id}" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="${pageContext.request.contextPath}/solicitacao" method="POST">
                        <input type="hidden" name="idSolicitacao" value="${sol.id}">
                        <input type="hidden" name="acao" value="recurso">
                        <div class="modal-header bg-danger text-white">
                            <h5 class="modal-title">Solicitar Recurso — Solicitação #${sol.id}</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <p class="small text-muted">Informe a justificativa do recurso para reavaliação pela comissão:</p>
                            <textarea name="justificativa" class="form-control" rows="3" required
                                      placeholder="Explique por que solicita revisão das horas concedidas..."></textarea>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                            <button type="submit" class="btn btn-danger">Enviar Recurso</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Modal Abrir Recurso ao Colegiado (status AVALIADA_APOS_RECURSO -> RECURSO_COLEGIADO) -->
    <c:if test="${sol.status == 'AVALIADA_APOS_RECURSO'}">
        <div class="modal fade" id="modalColegiado${sol.id}" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="${pageContext.request.contextPath}/solicitacao" method="POST">
                        <input type="hidden" name="idSolicitacao" value="${sol.id}">
                        <input type="hidden" name="acao" value="recurso_colegiado">
                        <div class="modal-header bg-dark text-warning">
                            <h5 class="modal-title">Acionar Recurso ao Colegiado — Solicitação #${sol.id}</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <p class="small text-muted">
                                O Recurso Colegiado será encaminhado para decisão final do Coordenador/Colegiado.
                                Após o parecer final, a solicitação avançará automaticamente para <strong>CONCLUÍDO</strong>.
                            </p>
                            <textarea name="justificativa" class="form-control" rows="3" required
                                      placeholder="Fundamente seu recurso ao Colegiado do Curso..."></textarea>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                            <button type="submit" class="btn btn-dark text-warning">Enviar ao Colegiado</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Modal Histórico de Status (Trilha de Auditoria) -->
    <div class="modal fade" id="modalHistorico${sol.id}" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header bg-secondary text-white">
                    <h5 class="modal-title"><i class="bi bi-clock-history me-2"></i>Histórico da Solicitação #${sol.id}</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p class="mb-2"><strong>Documento:</strong> <c:out value="${sol.nomeDocumento}" /></p>
                    <c:if test="${not empty sol.observacaoDiscente}">
                        <p class="mb-3"><strong>Observação do Discente:</strong> <c:out value="${sol.observacaoDiscente}" /></p>
                    </c:if>
                    <table class="table table-sm table-bordered">
                        <thead class="table-light">
                            <tr>
                                <th>Data/Hora</th>
                                <th>Status</th>
                                <th>Responsável</th>
                                <th>Observação / Parecer</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="hist" items="${sol.historicos}">
                                <tr>
                                    <td>${hist.dataRegistroFormatada}</td>
                                    <td><span class="badge bg-secondary">${hist.status}</span></td>
                                    <td><c:out value="${hist.alteradoPor}" /></td>
                                    <td><c:out value="${hist.observacaoOuParecer}" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</c:forEach>

<!-- Modal Cadastrar Nova Solicitação (COM UPLOAD DE COMPROVANTE @MultipartConfig) -->
<div class="modal fade" id="modalNovaSolicitacao" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/solicitacao" method="POST" enctype="multipart/form-data">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title">Registrar Nova Solicitação</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Comissão / Tipo de Carga Horária</label>
                        <select name="tipoHoras" class="form-select" required>
                            <option value="EXTENSAO">Comissão de Ações Extensionistas</option>
                            <option value="COMPLEMENTAR">Comissão de Atividades Complementares</option>
                            <option value="ESTAGIO">Comissão de Estágio</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Nome/Título do Documento Comprovante</label>
                        <input type="text" name="nomeDocumento" class="form-control"
                               placeholder="Ex: Certificado de Apresentação no Evento X" required>
                    </div>

                    <!-- CAMPO DE UPLOAD DO DOCUMENTO -->
                    <div class="mb-3">
                        <label for="arquivoComprovante" class="form-label fw-semibold">
                            Arquivo Comprobatório (PDF, PNG ou JPG)
                        </label>
                        <input class="form-control" type="file" id="arquivoComprovante"
                               name="arquivoComprovante" accept=".pdf,.png,.jpg,.jpeg" required>
                        <div class="form-text">Salvo fora do diretório do container web. Tamanho máximo: 5 MB.</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Horas Pretendidas</label>
                        <input type="number" name="horasSolicitadas" class="form-control" required min="1">
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Observações Adicionais do Discente</label>
                        <textarea name="observacaoDiscente" class="form-control" rows="3"
                                  placeholder="Insira detalhes complementares caso necessário..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" name="acao" value="salvar_rascunho" class="btn btn-secondary">Salvar (Criada)</button>
                    <button type="submit" name="acao" value="enviar_comissao" class="btn btn-primary">Salvar e Enviar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

