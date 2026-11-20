<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>SGHA - Painel do Discente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-primary mb-4">
    <div class="container">
        <span class="navbar-brand">SGHA - Painel do Discente</span>
        <span class="navbar-text text-white">
            Discente: <strong>${discenteLogado.nomeCompleto}</strong> (Matrícula: ${discenteLogado.matricula})
            | <a href="index.jsp" class="text-white">Voltar</a>
        </span>
    </div>
</nav>

<div class="container">

    <c:if test="${not empty msg}">
        <div class="alert alert-success">${msg}</div>
    </c:if>

    <!-- Formulário Principal de Nova Solicitação com Upload (@MultipartConfig) -->
    <div class="card mb-4">
        <div class="card-header">Registrar Nova Solicitação</div>
        <div class="card-body">
            <form action="solicitacao" method="POST" enctype="multipart/form-data">
                <div class="mb-2">
                    <label class="form-label">Comissão / Tipo de Carga Horária:</label>
                    <select name="tipoHoras" class="form-select" required>
                        <option value="EXTENSAO">Comissão de Ações Extensionistas</option>
                        <option value="COMPLEMENTAR">Comissão de Atividades Complementares</option>
                        <option value="ESTAGIO">Comissão de Estágio</option>
                    </select>
                </div>

                <div class="mb-2">
                    <label class="form-label">Nome/Título do Documento Comprovante:</label>
                    <input type="text" name="nomeDocumento" class="form-control" required>
                </div>

                <div class="mb-2">
                    <label class="form-label">Arquivo Comprobatório (PDF, PNG ou JPG):</label>
                    <input type="file" name="arquivoComprovante" class="form-control" accept=".pdf,.png,.jpg,.jpeg" required>
                </div>

                <div class="mb-2">
                    <label class="form-label">Horas Pretendidas:</label>
                    <input type="number" name="horasSolicitadas" class="form-control" min="1" required>
                </div>

                <div class="mb-3">
                    <label class="form-label">Observações Adicionais do Discente:</label>
                    <textarea name="observacaoDiscente" class="form-control" rows="2"></textarea>
                </div>

                <button type="submit" name="acao" value="salvar_rascunho" class="btn btn-secondary">Salvar (Criada)</button>
                <button type="submit" name="acao" value="enviar_comissao" class="btn btn-primary">Salvar e Enviar</button>
            </form>
        </div>
    </div>

    <!-- Tabela de Solicitações do Aluno -->
    <h5>Minhas Solicitações</h5>
    <table class="table table-bordered bg-white">
        <thead class="table-dark">
            <tr>
                <th>#ID</th>
                <th>Nome do Documento</th>
                <th>Caminho do Arquivo Salvo</th>
                <th>Comissão Destino</th>
                <th>Horas (Sol/Conc)</th>
                <th>Status</th>
                <th>Parecer Público</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="s" items="${discenteLogado.solicitacoes}">
                <tr>
                    <td>#${s.id}</td>
                    <td>${s.nomeDocumento}</td>
                    <td><small>${s.caminhoArquivoComprovante}</small></td>
                    <td>${s.tipoHoras}</td>
                    <td>${s.horasSolicitadas}h / ${s.horasConcedidas != null ? s.horasConcedidas : '--'}</td>
                    <td>${s.status}</td>
                    <td>${not empty s.observacaoParecerPublico ? s.observacaoParecerPublico : '-'}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

</body>
</html>
