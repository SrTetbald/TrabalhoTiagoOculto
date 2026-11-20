<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>SGHA - Portal de Acesso</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-4">
    <div class="text-center mb-4">
        <h3>SGHA - Sistema de Gestão de Horas Acadêmicas</h3>
        <p>Curso de Sistemas de Informação - UFRRJ</p>
    </div>

    <c:if test="${not empty erroLogin}">
        <div class="alert alert-danger">${erroLogin}</div>
    </c:if>

    <div class="row">
        <!-- ACESSO DO DISCENTE -->
        <div class="col-md-6 mb-3">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    Área do Discente
                </div>
                <div class="card-body">
                    <p>Informe seu nome completo e matrícula (no primeiro acesso o cadastro é criado automaticamente).</p>
                    <form action="acessoDiscente" method="POST">
                        <div class="mb-3">
                            <label class="form-label">Nome Completo:</label>
                            <input type="text" class="form-control" name="nomeCompleto" value="Carlos Eduardo da Silva" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Matrícula:</label>
                            <input type="text" class="form-control" name="matricula" value="2023019823" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Acessar</button>
                    </form>
                </div>
            </div>
        </div>

        <!-- ACESSO DA COMISSÃO / COORDENAÇÃO / SECRETARIA -->
        <div class="col-md-6 mb-3">
            <div class="card">
                <div class="card-header bg-dark text-white">
                    Área da Comissão / Coordenação / Secretaria
                </div>
                <div class="card-body">
                    <p>Acesso restrito para membros com mandato ativo.</p>
                    <form action="loginComissao" method="POST">
                        <div class="mb-3">
                            <label class="form-label">Usuário:</label>
                            <input type="text" class="form-control" name="username" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Senha:</label>
                            <input type="password" class="form-control" name="senha" required>
                        </div>
                        <button type="submit" class="btn btn-dark">Entrar</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <div class="card mt-3">
        <div class="card-body">
            <strong>Contas pré-cadastradas para teste (senha 123456, coordenador admin123):</strong><br>
            Extensão: <code>cmendes</code> | Complementares: <code>apatricia</code> | Estágio: <code>rmarques</code> |
            Coordenador: <code>tfranca</code> (senha: <code>admin123</code>) | Secretário: <code>msecretaria</code> |
            Mandato Vencido: <code>hsouza</code>
        </div>
    </div>
</div>

</body>
</html>
