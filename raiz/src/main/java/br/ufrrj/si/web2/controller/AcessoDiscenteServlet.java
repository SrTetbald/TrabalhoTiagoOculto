package br.ufrrj.si.web2.controller;

import java.io.IOException;

import br.ufrrj.si.web2.dao.BancoMemoria;
import br.ufrrj.si.web2.model.Discente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/acessoDiscente")
public class AcessoDiscenteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String nome = request.getParameter("nomeCompleto");
        String matricula = request.getParameter("matricula");

        Discente discente = BancoMemoria.buscarOuCriarDiscente(nome, matricula);

        HttpSession session = request.getSession(true);
        session.setAttribute("discenteLogado", discente);

        request.getRequestDispatcher("/discente.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("discenteLogado") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        request.getRequestDispatcher("/discente.jsp").forward(request, response);
    }
}
