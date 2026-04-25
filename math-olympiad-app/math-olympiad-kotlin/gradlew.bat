@echo off
REM Gradle start up script for Windows
SETLOCAL

set APP_HOME=%~dp0

REM Determine the Java command to use
if defined JAVA_HOME goto findJdkHome

set JAVACMD=java
goto checkJavaCmd

:findJdkHome
if exist "%JAVA_HOME%\jre\sh\java" (
    set JAVACMD=%JAVA_HOME%\jre\sh\java
) else (
    set JAVACMD=%JAVA_HOME%\bin\java
)

:checkJavaCmd
where java >nul 2>nul
if errorlevel 1 (
    echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
    exit /b 1
)

REM Setup the classpath
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

REM Execute Gradle
call %JAVACMD% %JAVA_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
ENDLOCAL