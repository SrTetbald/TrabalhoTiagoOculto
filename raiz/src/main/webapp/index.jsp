<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SGHA - Portal de Acesso</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body {
            background: linear-gradient(135deg, #f0f4f8 0%, #d9e2ec 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
        }
        .card-acesso {
            border: none;
            border-radius: 12px;
        }
    </style>
</head>
<body>

<div class="container py-5">
    <!-- Cabeçalho Institucional -->
    <div class="text-center mb-4">
        <h2 class="fw-bold text-dark">SGHA — Sistema de Gestão de Horas Acadêmicas</h2>
        <p class="text-muted mb-2">Curso de Sistemas de Informação (UFRRJ) • Registro de Horas Complementares, Extensão e Estágio</p>
        <span class="badge bg-secondary">Sessão com expiração automática após 2 minutos de inatividade</span>
    </div>

    <!-- Mensagens de retorno (logout, sessão expirada ou erro de autenticação) -->
    <div class="row justify-content-center mb-3">
        <div class="col-md-10">
            <c:if test="${param.msg == 'logout'}">
                <div class="alert alert-info alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle-fill me-2"></i>Sessão encerrada com segurança (Logout realizado).
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${param.erro == 'sessao'}">
                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    <i class="bi bi-clock-history me-2"></i>Sua sessão expirou por inatividade (limite de 2 minutos) ou você ainda não se identificou.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${not empty erroDiscente}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${erroDiscente}" />
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${not empty erroComissao}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-shield-exclamation me-2"></i><c:out value="${erroComissao}" />
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
        </div>
    </div>

    <div class="row justify-content-center g-4">
        <!-- CARTÃO 1: ACESSO DO DISCENTE -->
        <div class="col-md-5">
            <div class="card card-acesso shadow-sm h-100">
                <div class="card-header bg-primary text-white py-3 text-center rounded-top">
                    <h5 class="mb-0 fw-bold"><i class="bi bi-mortarboard-fill me-2"></i>Área do Discente</h5>
                </div>
                <div class="card-body p-4 d-flex flex-column justify-content-between">
                    <div>
                        <p class="small text-muted mb-4">
                            Informe seu nome completo e matrícula para consultar suas solicitações ou registrar novos comprovantes.
                            <em>(Primeiro acesso cria seu cadastro automaticamente).</em>
                        </p>

                        <form action="${pageContext.request.contextPath}/acessoDiscente" method="POST">
                            <div class="mb-3">
                                <label for="nomeCompleto" class="form-label fw-semibold">Nome Completo</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-person"></i></span>
                                    <input type="text" class="form-control" id="nomeCompleto" name="nomeCompleto"
                                           value="Carlos Eduardo da Silva"
                                           placeholder="Ex: Carlos Eduardo da Silva" required>
                                </div>
                            </div>

                            <div class="mb-4">
                                <label for="matricula" class="form-label fw-semibold">Número de Matrícula</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-card-text"></i></span>
                                    <input type="text" class="form-control" id="matricula" name="matricula"
                                           value="2023019823"
                                           placeholder="Ex: 2023019823" required>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary w-100 py-2 fw-bold">
                                Acessar Minhas Solicitações <i class="bi bi-arrow-right ms-1"></i>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- CARTÃO 2: ACESSO DA COMISSÃO / COORDENAÇÃO / SECRETARIA -->
        <div class="col-md-5">
            <div class="card card-acesso shadow-sm h-100">
                <div class="card-header bg-dark text-warning py-3 text-center rounded-top">
                    <h5 class="mb-0 fw-bold"><i class="bi bi-shield-lock-fill me-2"></i>Área Administrativa e Comissões</h5>
                </div>
                <div class="card-body p-4 d-flex flex-column justify-content-between">
                    <div>
                        <p class="small text-muted mb-4">
                            Acesso restrito aos membros das comissões avaliadoras (Extensão, Complementares, Estágio), Coordenação e Secretaria do Curso.
                        </p>

                        <form action="${pageContext.request.contextPath}/loginComissao" method="POST">
                            <div class="mb-3">
                                <label for="username" class="form-label fw-semibold">Usuário</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-person-badge"></i></span>
                                    <input type="text" class="form-control" id="username" name="username"
                                           placeholder="Ex: tfranca, cmendes, apatricia..." required>
                                </div>
                            </div>

                            <div class="mb-4">
                                <label for="senha" class="form-label fw-semibold">Senha</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-key"></i></span>
                                    <input type="password" class="form-control" id="senha" name="senha"
                                           placeholder="••••••••" required>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-dark text-warning w-100 py-2 fw-bold">
                                Entrar no Painel de Gestão <i class="bi bi-box-arrow-in-right ms-1"></i>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Resumo das contas pré-cadastradas em memória para facilitar o teste -->
    <div class="row justify-content-center mt-4">
        <div class="col-md-10">
            <div class="card border-0 shadow-sm bg-white">
                <div class="card-body py-3 px-4 small">
                    <h6 class="fw-bold mb-2"><i class="bi bi-info-circle-fill text-primary me-1"></i>Contas Pré-Cadastradas em Memória (Entregável 2)</h6>
                    <div class="row">
                        <div class="col-md-6">
                            <strong>Discente com dados de teste:</strong><br>
                            • Nome: <code>Carlos Eduardo da Silva</code> | Matrícula: <code>2023019823</code><br>
                            <em>(Qualquer outra matrícula informada cadastra um novo aluno automaticamente).</em>
                        </div>
                        <div class="col-md-6">
                            <strong>Perfis Administrativos e Comissões:</strong><br>
                            • Coordenador: <code>tfranca</code> / <code>admin123</code> | Secretário: <code>msecretaria</code> / <code>123456</code><br>
                            • Extensão: <code>cmendes</code> / <code>123456</code> | Complementares: <code>apatricia</code> / <code>123456</code> | Estágio: <code>rmarques</code> / <code>123456</code><br>
                            • Mandato Expirado (teste de bloqueio): <code>hsouza</code> / <code>123456</code>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

