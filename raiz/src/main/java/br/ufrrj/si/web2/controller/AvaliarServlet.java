package br.ufrrj.si.web2.controller;

import java.io.IOException;

import br.ufrrj.si.web2.dao.BancoMemoria;
import br.ufrrj.si.web2.model.AtuacaoComissao;
import br.ufrrj.si.web2.model.Solicitacao;
import br.ufrrj.si.web2.model.StatusSolicitacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/avaliar")
public class AvaliarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("atuacaoVigente") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        AtuacaoComissao atuacao = (AtuacaoComissao) session.getAttribute("atuacaoVigente");
        Long id = Long.valueOf(request.getParameter("idSolicitacao"));
        String decisao = request.getParameter("decisao");
        Solicitacao s = BancoMemoria.buscarSolicitacaoPorId(id);

        try {
            if (s != null) {
                s.setNomeDocumento(request.getParameter("nomeDocumento"));
                s.setObservacaoParecerPublico(request.getParameter("observacaoParecerPublico"));
                s.setObservacaoAdministrativa(request.getParameter("observacaoAdministrativa"));

                if ("REJEITAR".equals(decisao)) {
                    s.setHorasConcedidas(0);
                } else {
                    s.setHorasConcedidas(Integer.valueOf(request.getParameter("horasConcedidas")));
                }

                s.atualizarStatusPorComissao(StatusSolicitacao.AVALIADA, "Parecer registrado pela comissão.", atuacao);
                request.setAttribute("msg", "Parecer salvo na solicitação #" + id);
            }
        } catch (Exception e) {
            request.setAttribute("erro", e.getMessage());
        }

        LoginComissaoServlet.carregarTela(request, response);
    }
}
