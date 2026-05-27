@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set "PROJECT_DIR=d:\project\个人网站\--GodplaceBlog---main\my-project-frontend"

echo ==============================
echo    启动前端服务
echo ==============================
echo.

cd /d "%PROJECT_DIR%"

echo 当前目录: !cd!
echo.

echo 正在启动前端开发服务器...
"D:\Dev\Node.js\npm.cmd" run dev

pause