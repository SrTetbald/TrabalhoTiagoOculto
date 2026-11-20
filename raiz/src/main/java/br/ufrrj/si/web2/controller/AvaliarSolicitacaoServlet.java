package br.ufrrj.si.web2.controller;

import java.io.IOException;

import br.ufrrj.si.web2.model.AtuacaoComissao;
import br.ufrrj.si.web2.model.Solicitacao;
import br.ufrrj.si.web2.model.StatusSolicitacao;
import br.ufrrj.si.web2.repositorio.RepositorioMemoria;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controlador que processa as decisões da Comissão e do Coordenador:
 * - Emissão de parecer (APROVAR ou REJEITAR/INDEFERIR)
 * - Ajuste do nome do documento
 * - Restauração de solicitação cancelada (exclusivo do Coordenador)
 * - Cancelamento administrativo de solicitação concluída (exclusivo do Coordenador)
 */
@WebServlet("/avaliar")
public class AvaliarSolicitacaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession sessao = request.getSession(false);
        if (sessao == null || sessao.getAttribute("atuacaoVigente") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?erro=sessao");
            return;
        }

        AtuacaoComissao atuacao = (AtuacaoComissao) sessao.getAttribute("atuacaoVigente");
        String idParam = request.getParameter("idSolicitacao");
        String decisao = request.getParameter("decisao");

        if (idParam == null || idParam.trim().isEmpty()) {
            sessao.setAttribute("mensagemErro", "ID da solicitação não informado.");
            response.sendRedirect(request.getContextPath() + "/painelComissao");
            return;
        }

        Long idSol = Long.valueOf(idParam.trim());
        Solicitacao sol = RepositorioMemoria.getInstancia().buscarSolicitacaoPorId(idSol);

        if (sol == null) {
            sessao.setAttribute("mensagemErro", "Solicitação #" + idSol + " não encontrada.");
            response.sendRedirect(request.getContextPath() + "/painelComissao");
            return;
        }

        try {
            if (atuacao.isSecretario()) {
                throw new IllegalStateException("O perfil de Secretário possui acesso exclusivo de consulta.");
            }

            String nomeDocumento = request.getParameter("nomeDocumento");
            String obsAdmin = request.getParameter("observacaoAdministrativa");

            if ("AJUSTAR_NOME".equals(decisao)) {
                if (!atuacao.podeVisualizarTipo(sol.getTipoHoras())) {
                    throw new IllegalStateException("Sem permissão para alterar documento desta comissão.");
                }
                if (nomeDocumento != null && !nomeDocumento.trim().isEmpty()) {
                    sol.setNomeDocumento(nomeDocumento.trim());
                    sessao.setAttribute("mensagemSucesso", "Nome do documento da solicitação #" + idSol + " ajustado com sucesso.");
                }
            } else if ("RESTAURAR".equals(decisao)) {
                // Regra: Apenas o Coordenador pode restaurar uma solicitação cancelada para o seu status anterior
                StatusSolicitacao anterior = sol.getStatusAnteriorAoCancelamento() != null
                        ? sol.getStatusAnteriorAoCancelamento()
                        : StatusSolicitacao.ENVIADA;
                sol.atualizarStatusPorComissao(
                        anterior,
                        "Solicitação cancelada foi restaurada pelo Coordenador para o status anterior (" + anterior + ").",
                        atuacao
                );
                sessao.setAttribute("mensagemSucesso",
                        "Solicitação #" + idSol + " restaurada pelo Coordenador para o status " + anterior + "!");
            } else if ("CANCELAR_ADMIN".equals(decisao)) {
                if (!atuacao.isCoordenador()) {
                    throw new IllegalStateException("Apenas o Coordenador pode realizar o cancelamento administrativo.");
                }
                sol.atualizarStatusPorComissao(
                        StatusSolicitacao.CANCELADA,
                        "Cancelamento administrativo realizado pelo Coordenador.",
                        atuacao
                );
                sessao.setAttribute("mensagemSucesso", "Solicitação #" + idSol + " cancelada administrativamente.");
            } else if ("APROVAR".equals(decisao) || "REJEITAR".equals(decisao)) {
                String parecerPublico = request.getParameter("observacaoParecerPublico");
                if (parecerPublico == null || parecerPublico.trim().isEmpty()) {
                    throw new IllegalArgumentException("O campo de Parecer da Comissão (Público) é obrigatório.");
                }

                int horasConcedidas = 0;
                if ("APROVAR".equals(decisao)) {
                    String horasStr = request.getParameter("horasConcedidas");
                    horasConcedidas = (horasStr != null && !horasStr.trim().isEmpty())
                            ? Integer.parseInt(horasStr.trim())
                            : 0;
                } else {
                    // Se clicou em Indeferir / Rejeitar, zera as horas concedidas
                    horasConcedidas = 0;
                }

                sol.emitirParecerDaComissao(atuacao, horasConcedidas, parecerPublico.trim(), obsAdmin, nomeDocumento);
                sessao.setAttribute("mensagemSucesso",
                        "Parecer registrado com sucesso na solicitação #" + idSol + " (Status atual: " + sol.getStatus() + ").");
            }
        } catch (Exception e) {
            sessao.setAttribute("mensagemErro", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/painelComissao?id=" + idSol);
    }
}

