# Dúvidas e Decisões do Grupo

## Decisões tomadas sobre pontos do enunciado

1. **Critério de semelhança no Alerta de Duplicidade Cruzada**:
   - *Dúvida*: O enunciado pede para alertar quando existir outra solicitação do mesmo discente para "documento semelhante ou mesma ação", mas não fixa um algoritmo específico.
   - *Decisão*: Normalizamos o nome do documento (removendo acentos, extensão `.pdf/.png/.jpg` e palavras genéricas como "certificado", "declaração") e comparamos se os nomes coincidem ou compartilham pelo menos duas palavras-chave relevantes. Simples de explicar na arguição e funciona tanto para nomes idênticos quanto parecidos.

2. **Diretório `/uploads/sgha/` no Linux**:
   - *Dúvida*: O Quadro 1 do enunciado cita `ex: /uploads/sgha/` como exemplo de pasta fora do container web. No Linux, criar pastas direto em `/` exige `root`.
   - *Decisão*: O `SolicitacaoServlet` verifica se `/uploads/sgha` existe e permite escrita; se não permitir, cria e usa automaticamente `~/uploads/sgha` (`/home/srtetbald/uploads/sgha`), que também fica fora do Tomcat e funciona com qualquer usuário no Eclipse.

