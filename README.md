# Sistema de Gestao de Vendas

Projeto full stack com React no frontend e Spring Boot no backend.

## Video do sistema

https://youtu.be/fXHihUuA-7o

## Funcionalidades

- Cadastro de produtos
- Controle de estoque
- Registro de vendas
- Dashboard com graficos
- Exportacao de relatorio em Excel e PDF

## Execucao

### Backend

PowerShell/Windows:

```powershell
cd C:\Users\pitoco\sgvpdv\backend
$env:JAVA_HOME="C:\Program Files\Java\jdk-26"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd spring-boot:run
```

Git Bash:

```bash
cd backend
./mvnw spring-boot:run
```

Configure o MySQL em `backend/src/main/resources/application.properties`.

Se o MySQL local nao usar senha para o usuario `root`, deixe:

```properties
spring.datasource.username=root
spring.datasource.password=
```

### Frontend

```powershell
cd C:\Users\pitoco\sgvpdv\frontend
npm install
npm run dev
```
