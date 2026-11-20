package br.ufrrj.si.web2.controller;

import java.io.IOException;

import br.ufrrj.si.web2.dao.BancoMemoria;
import br.ufrrj.si.web2.model.AtuacaoComissao;
import br.ufrrj.si.web2.model.MembroComissao;
import br.ufrrj.si.web2.model.Solicitacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/loginComissao")
public class LoginComissaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String senha = request.getParameter("senha");

        MembroComissao membro = BancoMemoria.buscarMembro(username);
        if (membro == null || !membro.getSenha().equals(senha)) {
            request.setAttribute("erroLogin", "Usuário ou senha incorretos.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        AtuacaoComissao atuacao = membro.getAtuacaoVigente();
        if (atuacao == null) {
            request.setAttribute("erroLogin", "Mandato expirado ou inativo para " + membro.getNomeCompleto());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("membroLogado", membro);
        session.setAttribute("atuacaoVigente", atuacao);

        carregarTela(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        carregarTela(request, response);
    }

    public static void carregarTela(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("listaSolicitacoes", BancoMemoria.solicitacoes);

        Solicitacao selecionada = null;
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            selecionada = BancoMemoria.buscarSolicitacaoPorId(Long.valueOf(idParam));
        } else if (!BancoMemoria.solicitacoes.isEmpty()) {
            selecionada = BancoMemoria.solicitacoes.get(0);
        }

        request.setAttribute("solicitacaoSelecionada", selecionada);
        request.getRequestDispatcher("/comissao.jsp").forward(request, response);
    }
}
