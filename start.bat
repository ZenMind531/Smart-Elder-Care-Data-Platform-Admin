@echo off
chcp 65001 >nul
title 智慧养老平台 - 后端启动

echo ==============================
echo   智慧养老平台 - 后端启动
echo ==============================
echo.

setlocal enabledelayedexpansion

if not exist .env.bat goto first_run
call .env.bat
goto start_app

:first_run
echo 首次启动，请配置数据库信息
echo.
echo 选择数据库账号：
echo   A) reader   - 只读查询
echo   B) admin    - 万能账号
echo   C) backup   - 备份专用
echo   D) 自定义
echo.

set "choice="
set /p "choice=请选择 [A/B/C/D]: "
set "DB_USERNAME="
set "DB_PASSWORD="

if /i "%choice%"=="A" set "DB_USERNAME=reader"
if /i "%choice%"=="B" set "DB_USERNAME=admin"
if /i "%choice%"=="C" set "DB_USERNAME=backup"
if /i "%choice%"=="D" set /p "DB_USERNAME=请输入数据库用户名: "
if "%DB_USERNAME%"=="" set /p "DB_USERNAME=请输入数据库用户名: "

set /p "DB_PASSWORD=请输入数据库密码: "
if "%DB_PASSWORD%"=="" (
    echo ❌ 密码不能为空
    pause
    exit /b
)

> .env.bat (
    echo set DB_USERNAME=!DB_USERNAME!
    echo set DB_PASSWORD=!DB_PASSWORD!
)
echo ✅ 配置已保存
echo.

:start_app
echo ✅ 数据库用户: %DB_USERNAME%
echo.
echo 🚀 正在启动后端服务...
echo    启动后访问: http://localhost:8080
echo.

rem 把 .env.bat 的变量传给 mvn
if not exist .env.bat goto run
call .env.bat
:run
mvn spring-boot:run
endlocal
