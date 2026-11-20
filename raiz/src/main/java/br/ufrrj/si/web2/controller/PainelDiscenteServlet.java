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
 * Controlador que carrega os dados do Painel do Discente (horas consolidadas e lista de solicitações).
 */
@WebServlet("/painelDiscente")
public class PainelDiscenteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sessao = request.getSession(false);
        if (sessao == null || sessao.getAttribute("discenteLogado") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?erro=sessao");
            return;
        }

        Discente discenteSessao = (Discente) sessao.getAttribute("discenteLogado");
        // Atualiza a referência a partir do repositório em memória
        Discente discente = RepositorioMemoria.getInstancia().buscarDiscentePorMatricula(discenteSessao.getMatricula());
        sessao.setAttribute("discenteLogado", discente);

        request.setAttribute("discente", discente);
        request.setAttribute("solicitacoes", discente.getSolicitacoes());

        // Transfere mensagens flash da sessão para a requisição
        transferirMensagemFlash(sessao, request, "mensagemSucesso");
        transferirMensagemFlash(sessao, request, "mensagemErro");

        request.getRequestDispatcher("/painel-discente.jsp").forward(request, response);
    }

    private void transferirMensagemFlash(HttpSession sessao, HttpServletRequest request, String chave) {
        Object valor = sessao.getAttribute(chave);
        if (valor != null) {
            request.setAttribute(chave, valor);
            sessao.removeAttribute(chave);
        }
    }
}

