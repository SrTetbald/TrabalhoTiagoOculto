package br.ufrrj.si.web2.controller;

import java.io.IOException;

import br.ufrrj.si.web2.model.AtuacaoComissao;
import br.ufrrj.si.web2.model.MembroComissao;
import br.ufrrj.si.web2.repositorio.RepositorioMemoria;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet de autenticação dos membros de comissão, coordenador e secretário.
 * Valida login, senha e se existe uma atuação (mandato) ativa e dentro da vigência.
 */
@WebServlet("/loginComissao")
public class LoginComissaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String senha = request.getParameter("senha");

        MembroComissao membro = RepositorioMemoria.getInstancia().buscarMembroPorUsername(username);
        if (membro == null || !membro.getSenha().equals(senha)) {
            request.setAttribute("erroComissao", "Usuário ou senha inválidos.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        // Regra de vigência de mandato: apenas contas com atuação ativa e dentro da validade podem logar
        AtuacaoComissao atuacaoVigente = membro.getAtuacaoVigente();
        if (atuacaoVigente == null) {
            request.setAttribute("erroComissao",
                    "Acesso bloqueado para " + membro.getNomeCompleto()
                    + ": seu mandato/atuação na comissão está expirado ou inativo.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        HttpSession sessao = request.getSession(true);
        sessao.setAttribute("membroLogado", membro);
        sessao.setAttribute("atuacaoVigente", atuacaoVigente);

        response.sendRedirect(request.getContextPath() + "/painelComissao");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/painelComissao");
    }
}

