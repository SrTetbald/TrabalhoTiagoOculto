package br.ufrrj.si.web2.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import br.ufrrj.si.web2.model.Discente;
import br.ufrrj.si.web2.model.Solicitacao;
import br.ufrrj.si.web2.model.StatusSolicitacao;
import br.ufrrj.si.web2.model.TipoHoras;
import br.ufrrj.si.web2.repositorio.RepositorioMemoria;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 * Controlador das operações do discente sobre solicitações:
 * - Criação com upload de comprovante (@MultipartConfig) salvando fora do container web
 * - Edição, Envio, Cancelamento e Exclusão (exclusão somente em CRIADA)
 * - Aceite de parecer e abertura de Recurso / Recurso Colegiado
 */
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
        HttpSession sessao = request.getSession(false);
        if (sessao == null || sessao.getAttribute("discenteLogado") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?erro=sessao");
            return;
        }

        Discente discente = (Discente) sessao.getAttribute("discenteLogado");
        RepositorioMemoria repo = RepositorioMemoria.getInstancia();
        String acao = request.getParameter("acao");

        try {
            if ("salvar_rascunho".equals(acao) || "enviar_comissao".equals(acao)) {
                boolean enviarDireto = "enviar_comissao".equals(acao);
                processarCadastroOuEdicao(request, discente, repo, enviarDireto, sessao);
            } else {
                processarTransicaoDeStatus(request, discente, repo, acao, sessao);
            }
        } catch (Exception e) {
            sessao.setAttribute("mensagemErro", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/painelDiscente");
    }

    private void processarCadastroOuEdicao(HttpServletRequest request, Discente discente,
                                           RepositorioMemoria repo, boolean enviarDireto, HttpSession sessao)
            throws IOException, ServletException {

        String idParam = request.getParameter("idSolicitacao");
        String tipoHorasStr = request.getParameter("tipoHoras");
        String nomeDocumento = request.getParameter("nomeDocumento");
        String horasStr = request.getParameter("horasSolicitadas");
        String observacaoDiscente = request.getParameter("observacaoDiscente");

        TipoHoras tipoHoras = TipoHoras.valueOf(tipoHorasStr);
        int horasSolicitadas = Integer.parseInt(horasStr);
        if (horasSolicitadas <= 0) {
            throw new IllegalArgumentException("A quantidade de horas pretendidas deve ser maior que zero.");
        }

        Part arquivoPart = null;
        try {
            arquivoPart = request.getPart("arquivoComprovante");
        } catch (Exception ignored) {
            // Caso a requisição não tenha parte de arquivo nova
        }

        // Edição de uma solicitação existente no status CRIADA
        if (idParam != null && !idParam.trim().isEmpty()) {
            Long idSol = Long.valueOf(idParam.trim());
            Solicitacao existente = repo.buscarSolicitacaoPorId(idSol);
            if (existente == null || !existente.getDiscente().getMatricula().equals(discente.getMatricula())) {
                throw new IllegalArgumentException("Solicitação não encontrada para este discente.");
            }
            if (!existente.getStatus().permiteEdicaoPeloDiscente()) {
                throw new IllegalStateException("Apenas solicitações no status CRIADA podem ser editadas.");
            }

            existente.setTipoHoras(tipoHoras);
            existente.setNomeDocumento(nomeDocumento != null ? nomeDocumento.trim() : "");
            existente.setHorasSolicitadas(horasSolicitadas);
            existente.setObservacaoDiscente(observacaoDiscente);

            if (arquivoPart != null && arquivoPart.getSize() > 0) {
                String caminhoSalvo = salvarArquivoComprovanteForaDoContainer(arquivoPart, discente.getMatricula());
                existente.setCaminhoArquivoComprovante(caminhoSalvo);
            }

            if (enviarDireto) {
                existente.atualizarStatusPorDiscente(
                        StatusSolicitacao.ENVIADA,
                        "Solicitação editada e enviada para a " + tipoHoras.getDescricao() + "."
                );
                sessao.setAttribute("mensagemSucesso", "Solicitação #" + existente.getId() + " atualizada e enviada para a comissão!");
            } else {
                sessao.setAttribute("mensagemSucesso", "Rascunho da solicitação #" + existente.getId() + " atualizado com sucesso.");
            }
            return;
        }

        // Criação de nova solicitação
        if (arquivoPart == null || arquivoPart.getSize() == 0) {
            throw new IllegalArgumentException("É obrigatório anexar o arquivo comprovante (PDF, PNG ou JPG).");
        }

        String caminhoSalvo = salvarArquivoComprovanteForaDoContainer(arquivoPart, discente.getMatricula());

        Solicitacao nova = new Solicitacao();
        nova.setDiscente(discente);
        nova.setTipoHoras(tipoHoras);
        nova.setNomeDocumento(nomeDocumento != null ? nomeDocumento.trim() : "");
        nova.setCaminhoArquivoComprovante(caminhoSalvo);
        nova.setHorasSolicitadas(horasSolicitadas);
        nova.setObservacaoDiscente(observacaoDiscente);

        repo.salvarNovaSolicitacao(nova, enviarDireto);

        if (enviarDireto) {
            sessao.setAttribute("mensagemSucesso",
                    "Solicitação #" + nova.getId() + " criada e enviada para a " + tipoHoras.getDescricao()
                    + "! Comprovante salvo em: " + caminhoSalvo);
        } else {
            sessao.setAttribute("mensagemSucesso",
                    "Solicitação #" + nova.getId() + " salva como CRIADA (rascunho). Comprovante salvo em: " + caminhoSalvo);
        }
    }

    private void processarTransicaoDeStatus(HttpServletRequest request, Discente discente,
                                            RepositorioMemoria repo, String acao, HttpSession sessao) {

        String idParam = request.getParameter("idSolicitacao");
        if (idParam == null || idParam.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da solicitação não informado.");
        }
        Long idSol = Long.valueOf(idParam.trim());
        Solicitacao sol = repo.buscarSolicitacaoPorId(idSol);

        if (sol == null || !sol.getDiscente().getMatricula().equals(discente.getMatricula())) {
            throw new IllegalArgumentException("Solicitação não encontrada para o discente logado.");
        }

        String justificativa = request.getParameter("justificativa");

        switch (acao) {
            case "enviar":
                sol.atualizarStatusPorDiscente(
                        StatusSolicitacao.ENVIADA,
                        "Solicitação encaminhada pelo discente para a " + sol.getTipoHoras().getDescricao() + "."
                );
                sessao.setAttribute("mensagemSucesso", "Solicitação #" + idSol + " enviada para análise da comissão!");
                break;

            case "cancelar":
                String motivoCancelamento = (justificativa != null && !justificativa.trim().isEmpty())
                        ? justificativa.trim()
                        : "Solicitação cancelada pelo próprio discente.";
                sol.atualizarStatusPorDiscente(StatusSolicitacao.CANCELADA, motivoCancelamento);
                sessao.setAttribute("mensagemSucesso", "Solicitação #" + idSol + " foi cancelada.");
                break;

            case "excluir":
                repo.excluirSolicitacao(idSol, discente);
                sessao.setAttribute("mensagemSucesso", "Solicitação #" + idSol + " (rascunho CRIADA) foi excluída definitivamente.");
                break;

            case "aceitar":
                sol.atualizarStatusPorDiscente(
                        StatusSolicitacao.ACEITA,
                        "Discente concordou com o parecer (" + sol.getHorasConcedidas() + "h concedidas)."
                );
                sessao.setAttribute("mensagemSucesso",
                        "Parecer da solicitação #" + idSol + " aceito! O status avançou para CONCLUÍDO e as horas foram contabilizadas.");
                break;

            case "recurso":
                String textoRecurso = (justificativa != null && !justificativa.trim().isEmpty())
                        ? justificativa.trim()
                        : "Discente solicitou revisão da carga horária concedida.";
                sol.atualizarStatusPorDiscente(StatusSolicitacao.RECURSO, "Recurso aberto pelo discente: " + textoRecurso);
                sessao.setAttribute("mensagemSucesso", "Recurso aberto para a solicitação #" + idSol + ". Devolvida para a comissão reavaliar.");
                break;

            case "recurso_colegiado":
                String textoColegiado = (justificativa != null && !justificativa.trim().isEmpty())
                        ? justificativa.trim()
                        : "Discente acionou recurso ao Colegiado/Coordenação após reavaliação da comissão.";
                sol.atualizarStatusPorDiscente(
                        StatusSolicitacao.RECURSO_COLEGIADO,
                        "Recurso ao Colegiado aberto pelo discente: " + textoColegiado
                );
                sessao.setAttribute("mensagemSucesso",
                        "Recurso ao Colegiado enviado para decisão final do Coordenador na solicitação #" + idSol + ".");
                break;

            default:
                throw new IllegalArgumentException("Operação desconhecida: " + acao);
        }
    }

    /**
     * Salva o arquivo enviado em um diretório fora do container web (Tomcat).
     * Tenta usar /uploads/sgha se o sistema operacional permitir escrita; caso contrário,
     * cria e usa ~/uploads/sgha na pasta do usuário.
     */
    public static Path obterDiretorioExternoUploads() throws IOException {
        Path dirRaiz = Paths.get("/uploads/sgha");
        try {
            if (!Files.exists(dirRaiz)) {
                Files.createDirectories(dirRaiz);
            }
            if (Files.isWritable(dirRaiz)) {
                return dirRaiz;
            }
        } catch (Exception ignored) {
            // Sem permissão na raiz "/" do Linux (comum quando o Tomcat não roda como root)
        }

        Path dirUsuario = Paths.get(System.getProperty("user.home"), "uploads", "sgha");
        if (!Files.exists(dirUsuario)) {
            Files.createDirectories(dirUsuario);
        }
        return dirUsuario;
    }

    private String salvarArquivoComprovanteForaDoContainer(Part arquivoPart, String matricula) throws IOException {
        Path diretorioDestino = obterDiretorioExternoUploads();
        String nomeOriginal = Paths.get(arquivoPart.getSubmittedFileName()).getFileName().toString();
        String nomeLimpo = nomeOriginal.replaceAll("[^a-zA-Z0-9._-]", "_");
        String nomeUnico = matricula + "_" + System.currentTimeMillis() + "_" + nomeLimpo;

        Path arquivoDestino = diretorioDestino.resolve(nomeUnico);
        try (InputStream entrada = arquivoPart.getInputStream()) {
            Files.copy(entrada, arquivoDestino, StandardCopyOption.REPLACE_EXISTING);
        }
        return arquivoDestino.toAbsolutePath().toString();
    }
}

