package br.ufrrj.si.web2.controller;

import java.io.IOException;
import java.util.List;

import br.ufrrj.si.web2.model.AtuacaoComissao;
import br.ufrrj.si.web2.model.Solicitacao;
import br.ufrrj.si.web2.repositorio.RepositorioMemoria;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controlador que monta o Painel de Avaliação da Comissão / Coordenação / Secretaria.
 * Filtra as solicitações conforme o perfil logado e verifica duplicidades cruzadas.
 */
@WebServlet("/painelComissao")
public class PainelComissaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sessao = request.getSession(false);
        if (sessao == null || sessao.getAttribute("atuacaoVigente") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?erro=sessao");
            return;
        }

        AtuacaoComissao atuacao = (AtuacaoComissao) sessao.getAttribute("atuacaoVigente");
        RepositorioMemoria repo = RepositorioMemoria.getInstancia();

        String busca = request.getParameter("busca");
        String filtroComissao = request.getParameter("filtroComissao");

        List<Solicitacao> filaVisivel = repo.listarSolicitacoesVisiveisParaComissao(atuacao, busca, filtroComissao);
        request.setAttribute("solicitacoesVisiveis", filaVisivel);
        request.setAttribute("buscaAtual", busca);
        request.setAttribute("filtroComissaoAtual", filtroComissao);

        // Seleciona a solicitação detalhada no painel (pelo parâmetro ?id= ou a primeira da lista)
        Solicitacao selecionada = null;
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                Long idSol = Long.valueOf(idParam.trim());
                Solicitacao candidata = repo.buscarSolicitacaoPorId(idSol);
                if (candidata != null && filaVisivel.contains(candidata)) {
                    selecionada = candidata;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        if (selecionada == null && !filaVisivel.isEmpty()) {
            selecionada = filaVisivel.get(0);
        }

        if (selecionada != null) {
            request.setAttribute("solicitacaoSelecionada", selecionada);
            request.setAttribute("duplicidadesCruzadas", repo.buscarDuplicidadesCruzadas(selecionada));
            request.setAttribute("podeAvaliarSelecionada", atuacao.podeAvaliar(selecionada));
        }

        transferirMensagemFlash(sessao, request, "mensagemSucesso");
        transferirMensagemFlash(sessao, request, "mensagemErro");

        request.getRequestDispatcher("/painel-comissao.jsp").forward(request, response);
    }

    private void transferirMensagemFlash(HttpSession sessao, HttpServletRequest request, String chave) {
        Object valor = sessao.getAttribute(chave);
        if (valor != null) {
            request.setAttribute(chave, valor);
            sessao.removeAttribute(chave);
        }
    }
}

