@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "WRAPPER_DIR=%SCRIPT_DIR%.mvn\wrapper"
set "PROPS_FILE=%WRAPPER_DIR%\maven-wrapper.properties"

if not exist "%PROPS_FILE%" (
  echo Arquivo de propriedades do Maven Wrapper nao encontrado.
  exit /b 1
)

for /f "tokens=1,* delims==" %%A in (%PROPS_FILE%) do (
  if "%%A"=="distributionUrl" set "DISTRIBUTION_URL=%%B"
  if "%%A"=="distributionPath" set "DISTRIBUTION_PATH=%%B"
  if "%%A"=="zipStorePath" set "ZIP_STORE_PATH=%%B"
)

if "%DISTRIBUTION_URL%"=="" (
  echo distributionUrl nao definido em maven-wrapper.properties.
  exit /b 1
)

set "MAVEN_HOME=%WRAPPER_DIR%\apache-maven\apache-maven-3.9.9"
set "ZIP_FILE=%WRAPPER_DIR%\apache-maven\apache-maven-3.9.9-bin.zip"
set "MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

if not exist "%MVN_CMD%" (
  echo Baixando Maven Wrapper...
  if not exist "%WRAPPER_DIR%\apache-maven" mkdir "%WRAPPER_DIR%\apache-maven"
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ProgressPreference='SilentlyContinue';" ^
    "Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '%ZIP_FILE%';" ^
    "Expand-Archive -Path '%ZIP_FILE%' -DestinationPath '%WRAPPER_DIR%\apache-maven' -Force"
  if errorlevel 1 (
    echo Falha ao baixar ou extrair o Maven.
    exit /b 1
  )
)

call "%MVN_CMD%" %*
endlocal
