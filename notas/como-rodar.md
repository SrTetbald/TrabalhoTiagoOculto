# Como Rodar o Projeto no Eclipse (Entregável 2)

## 1. Importar o Projeto no Eclipse

1. Abra o Eclipse.
2. Vá em **File -> Import... -> General -> Existing Projects into Workspace**.
3. Em **Select root directory**, selecione a pasta:
   `/home/srtetbald/Documentos/faculdade/WEB2/raiz`
4. O projeto **SGHA** aparecerá marcado. Clique em **Finish**.

*(O projeto já está configurado para o Server Runtime `Apache Tomcat v10.1` do seu workspace e já traz os JARs do JSTL 3.0 em `src/main/webapp/WEB-INF/lib`).*

## 2. Rodar o `main` de Validação do Modelo

1. No Eclipse, abra `src/main/java/br/ufrrj/si/web2/util/TesteDominioSGHA.java`.
2. Clique com o botão direito -> **Run As -> Java Application**.
3. Veja no Console a simulação completa da vigência de mandatos, máquina de estados, bloqueio de restauração por membro comum, restauração pelo Coordenador, alerta de duplicidade e trilha de auditoria.

## 3. Rodar a Aplicação Web no Tomcat 10.1

1. Clique com o botão direito no projeto **SGHA** -> **Run As -> Run on Server**.
2. Selecione o **Apache Tomcat v10.1** e clique em **Finish**.
3. Acesse no navegador: `http://localhost:8080/SGHA/`

## 4. Onde ficam os arquivos de Upload

- Os comprovantes enviados no formulário de Nova Solicitação são salvos fora do Tomcat na pasta:
  `/home/srtetbald/uploads/sgha/` (ou `/uploads/sgha/` caso exista com permissão de escrita).
- O caminho completo do arquivo gerado aparece na mensagem de confirmação logo após o envio e pode ser aberto clicando em **Ver comprovante / Abrir Arquivo**.

## 5. Contas Pré-Cadastradas para Teste

- **Discente com solicitações de exemplo**:
  - Nome: `Carlos Eduardo da Silva` | Matrícula: `2023019823`
- **Comissões e Gestão**:
  - Coordenador: `tfranca` / `admin123`
  - Secretário (somente consulta): `msecretaria` / `123456`
  - Comissão de Extensão: `cmendes` / `123456`
  - Comissão de Atividades Complementares: `apatricia` / `123456`
  - Comissão de Estágio: `rmarques` / `123456`
  - Mandato Expirado (bloqueado no login): `hsouza` / `123456`

