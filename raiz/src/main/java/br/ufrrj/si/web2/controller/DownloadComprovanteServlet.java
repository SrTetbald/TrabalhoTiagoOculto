package br.ufrrj.si.web2.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import br.ufrrj.si.web2.model.Solicitacao;
import br.ufrrj.si.web2.repositorio.RepositorioMemoria;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet que lê o arquivo comprovante salvo no diretório externo ao Tomcat
 * e envia para visualização/download no navegador.
 */
@WebServlet("/downloadComprovante")
public class DownloadComprovanteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sessao = request.getSession(false);
        if (sessao == null || (sessao.getAttribute("discenteLogado") == null && sessao.getAttribute("atuacaoVigente") == null)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?erro=sessao");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da solicitação não informado.");
            return;
        }

        Solicitacao sol = RepositorioMemoria.getInstancia().buscarSolicitacaoPorId(Long.valueOf(idParam.trim()));
        if (sol == null || sol.getCaminhoArquivoComprovante() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Comprovante não encontrado.");
            return;
        }

        Path caminho = Paths.get(sol.getCaminhoArquivoComprovante());
        if (Files.exists(caminho) && Files.isRegularFile(caminho)) {
            String mimeType = Files.probeContentType(caminho);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", "inline; filename=\"" + caminho.getFileName().toString() + "\"");
            Files.copy(caminho, response.getOutputStream());
            return;
        }

        // Caso seja um dos registros mockados de demonstração (ex: #88, #99, #101, #104), exibe um resumo textual
        response.setContentType("text/plain; charset=UTF-8");
        String conteudoMock = "==========================================================\n"
                + "   COMPROVANTE DEMONSTRATIVO (DADO MOCKADO EM MEMÓRIA)\n"
                + "==========================================================\n"
                + "Solicitação:  #" + sol.getId() + "\n"
                + "Discente:     " + sol.getDiscente().getNomeCompleto() + " (" + sol.getDiscente().getMatricula() + ")\n"
                + "Documento:    " + sol.getNomeDocumento() + "\n"
                + "Categoria:    " + sol.getTipoHoras().getDescricao() + "\n"
                + "Horas Sol.:   " + sol.getHorasSolicitadas() + "h\n"
                + "Caminho Ref.: " + sol.getCaminhoArquivoComprovante() + "\n"
                + "==========================================================\n"
                + "(Para testar upload e abertura de arquivo real, cadastre uma Nova Solicitação anexando um PDF, PNG ou JPG).\n";
        try (OutputStream out = response.getOutputStream()) {
            out.write(conteudoMock.getBytes(StandardCharsets.UTF_8));
        }
    }
}

