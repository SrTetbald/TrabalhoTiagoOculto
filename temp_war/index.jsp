<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
            transition: transform 0.2s;
        }
        .card-acesso:hover {
            transform: translateY(-3px);
        }
    </style>
</head>
<body>

<div class="container py-5">
    <!-- Cabeçalho Institucional -->
    <div class="text-center mb-5">
        <h2 class="fw-bold text-dark">SGHA — Sistema de Gestão de Horas Acadêmicas</h2>
        <p class="text-muted">Curso de Sistemas de Informação • Registro de Horas Complementares, Extensão e Estágio</p>
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

                        <!-- Action aponta para o Servlet de identificação do discente -->
                        <form action="acessoDiscente" method="POST">
                            <div class="mb-3">
                                <label for="nomeCompleto" class="form-label fw-semibold">Nome Completo</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-person"></i></span>
                                    <input type="text" class="form-control" id="nomeCompleto" name="nomeCompleto" 
                                           placeholder="Ex: Carlos Eduardo da Silva" required>
                                </div>
                            </div>

                            <div class="mb-4">
                                <label for="matricula" class="form-label fw-semibold">Número de Matrícula</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-card-text"></i></span>
                                    <input type="text" class="form-control" id="matricula" name="matricula" 
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

                        <!-- Action aponta para o Servlet de autenticação -->
                        <form action="loginComissao" method="POST">
                            <div class="mb-3">
                                <label for="username" class="form-label fw-semibold">Usuário</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-person-badge"></i></span>
                                    <input type="text" class="form-control" id="username" name="username" 
                                           placeholder="Digite seu login institucional" required>
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
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>