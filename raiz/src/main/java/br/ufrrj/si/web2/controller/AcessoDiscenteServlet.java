package br.ufrrj.si.web2.controller;

import java.io.IOException;

import br.ufrrj.si.web2.model.Discente;
import br.ufrrj.si.web2.repositorio.RepositorioMemoria;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet responsável pela identificação e cadastro simplificado do discente.
 * Se a matrícula não existir, cadastra o aluno automaticamente.
 * Se já existir, recupera o cadastro e suas solicitações anteriores.
 */
@WebServlet("/acessoDiscente")
public class AcessoDiscenteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String nomeCompleto = request.getParameter("nomeCompleto");
        String matricula = request.getParameter("matricula");

        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()
                || matricula == null || matricula.trim().isEmpty()) {
            request.setAttribute("erroDiscente", "Informe seu nome completo e número de matrícula.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        RepositorioMemoria repo = RepositorioMemoria.getInstancia();
        boolean jaExistia = (repo.buscarDiscentePorMatricula(matricula) != null);
        Discente discente = repo.buscarOuCadastrarDiscente(nomeCompleto, matricula);

        // Inicia a sessão do discente 
        HttpSession sessao = request.getSession(true);
        sessao.setAttribute("discenteLogado", discente);
        if (!jaExistia) {
            sessao.setAttribute("mensagemSucesso",
                    "Primeiro acesso identificado! Cadastro criado automaticamente para a matrícula " + discente.getMatricula() + ".");
        } else {
            sessao.setAttribute("mensagemSucesso",
                    "Bem-vindo(a) de volta, " + discente.getNomeCompleto() + "! Suas solicitações anteriores foram carregadas.");
        }

        response.sendRedirect(request.getContextPath() + "/painelDiscente");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/painelDiscente");
    }
}

