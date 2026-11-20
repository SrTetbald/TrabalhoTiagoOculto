<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>SGHA - Painel de Avaliação da Comissão</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark mb-4">
    <div class="container">
        <span class="navbar-brand">SGHA - Módulo de Gestão</span>
        <span class="navbar-text text-white">
            Perfil: <strong>${atuacaoVigente.perfil} / ${membroLogado.nomeCompleto}</strong>
            | <a href="index.jsp" class="text-warning">Voltar</a>
        </span>
    </div>
</nav>

<div class="container">

    <c:if test="${not empty msg}">
        <div class="alert alert-success">${msg}</div>
    </c:if>
    <c:if test="${not empty erro}">
        <div class="alert alert-danger">${erro}</div>
    </c:if>

    <!-- Lista simples de solicitações em memória -->
    <h5>Solicitações Cadastradas</h5>
    <table class="table table-bordered bg-white mb-4">
        <thead class="table-secondary">
            <tr>
                <th>#ID</th>
                <th>Discente</th>
                <th>Documento</th>
                <th>Comissão</th>
                <th>Status</th>
                <th>Selecionar</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="item" items="${listaSolicitacoes}">
                <tr>
                    <td>#${item.id}</td>
                    <td>${item.discente.nomeCompleto} (${item.discente.matricula})</td>
                    <td>${item.nomeDocumento}</td>
                    <td>${item.tipoHoras}</td>
                    <td>${item.status}</td>
                    <td><a href="loginComissao?id=${item.id}" class="btn btn-sm btn-primary">Abrir</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <!-- Formulário Principal de Avaliação -->
    <c:if test="${not empty solicitacaoSelecionada}">
        <div class="card mb-4">
            <div class="card-header">
                Avaliação da Solicitação #${solicitacaoSelecionada.id} (Status: ${solicitacaoSelecionada.status})
            </div>
            <div class="card-body">
                <form action="avaliar" method="POST">
                    <input type="hidden" name="idSolicitacao" value="${solicitacaoSelecionada.id}">

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Nome / Título do Documento:</label>
                            <input type="text" class="form-control" name="nomeDocumento" value="${solicitacaoSelecionada.nomeDocumento}">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Caminho do Comprovante:</label>
                            <input type="text" class="form-control" value="${solicitacaoSelecionada.caminhoArquivoComprovante}" readonly>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Horas Concedidas:</label>
                            <input type="number" class="form-control" name="horasConcedidas"
                                   value="${solicitacaoSelecionada.horasConcedidas != null ? solicitacaoSelecionada.horasConcedidas : solicitacaoSelecionada.horasSolicitadas}">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Observação do Discente:</label>
                        <p class="p-2 bg-light border">${solicitacaoSelecionada.observacaoDiscente}</p>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Parecer da Comissão (Visível para o Discente):</label>
                        <textarea class="form-control" name="observacaoParecerPublico" rows="2" required>${solicitacaoSelecionada.observacaoParecerPublico}</textarea>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Observação Administrativa (Privada):</label>
                        <textarea class="form-control" name="observacaoAdministrativa" rows="2">${solicitacaoSelecionada.observacaoAdministrativa}</textarea>
                    </div>

                    <button type="submit" name="decisao" value="REJEITAR" class="btn btn-danger">Indeferir / Rejeitar</button>
                    <button type="submit" name="decisao" value="APROVAR" class="btn btn-success">Emitir Parecer Final</button>
                </form>
            </div>
        </div>
    </c:if>

</div>

</body>
</html>
