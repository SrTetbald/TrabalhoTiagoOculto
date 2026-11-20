package br.ufrrj.si.web2.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import br.ufrrj.si.web2.dao.BancoMemoria;
import br.ufrrj.si.web2.model.Discente;
import br.ufrrj.si.web2.model.Solicitacao;
import br.ufrrj.si.web2.model.StatusSolicitacao;
import br.ufrrj.si.web2.model.TipoHoras;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/solicitacao")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 5,       // 5 MB
    maxRequestSize = 1024 * 1024 * 10    // 10 MB
)
public class SolicitacaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("discenteLogado") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        Discente discente = (Discente) session.getAttribute("discenteLogado");
        String acao = request.getParameter("acao");

        // Cadastro de nova solicitação com upload de arquivo fora do container
        if ("salvar_rascunho".equals(acao) || "enviar_comissao".equals(acao)) {
            TipoHoras tipo = TipoHoras.valueOf(request.getParameter("tipoHoras"));
            String nomeDoc = request.getParameter("nomeDocumento");
            Integer horas = Integer.valueOf(request.getParameter("horasSolicitadas"));
            String obs = request.getParameter("observacaoDiscente");

            Part filePart = request.getPart("arquivoComprovante");
            String caminhoSalvo = "";

            if (filePart != null && filePart.getSize() > 0) {
                File pastaUploads = new File("/uploads/sgha");
                if (!pastaUploads.exists() && !pastaUploads.mkdirs()) {
                    pastaUploads = new File(System.getProperty("user.home"), "uploads/sgha");
                    pastaUploads.mkdirs();
                }
                String nomeOriginal = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String nomeUnico = discente.getMatricula() + "_" + System.currentTimeMillis() + "_" + nomeOriginal;
                File destino = new File(pastaUploads, nomeUnico);
                filePart.write(destino.getAbsolutePath());
                caminhoSalvo = destino.getAbsolutePath();
            }

            Solicitacao nova = new Solicitacao();
            nova.setId(BancoMemoria.proximoIdSolicitacao++);
            nova.setDiscente(discente);
            nova.setTipoHoras(tipo);
            nova.setNomeDocumento(nomeDoc);
            nova.setCaminhoArquivoComprovante(caminhoSalvo);
            nova.setHorasSolicitadas(horas);
            nova.setObservacaoDiscente(obs);

            nova.atualizarStatusPorDiscente(StatusSolicitacao.CRIADA, "Criada pelo discente.");
            if ("enviar_comissao".equals(acao)) {
                nova.atualizarStatusPorDiscente(StatusSolicitacao.ENVIADA, "Enviada para a comissão.");
            }

            discente.getSolicitacoes().add(nova);
            BancoMemoria.solicitacoes.add(nova);
            request.setAttribute("msg", "Solicitação cadastrada! Caminho salvo: " + caminhoSalvo);
        }

        request.getRequestDispatcher("/discente.jsp").forward(request, response);
    }
}
