@echo off
rem Thin launcher; real logic lives in the gradle-tools submodule (gradle-tools\docker\).
"%~dp0gradle-tools\docker\build-docker.bat" %*
