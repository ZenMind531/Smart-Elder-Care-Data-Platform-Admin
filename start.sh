#!/bin/bash

# 智慧养老平台 - 后端启动脚本
#
# 首次使用：
#   1. cp .env.example .env
#   2. 编辑 .env 填入你的数据库密码
#   3. chmod +x start.sh
#   4. ./start.sh

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 检查 .env 是否存在
if [ ! -f "$SCRIPT_DIR/.env" ]; then
    echo "❌ 找不到 .env 文件！"
    echo "   请先执行：cp .env.example .env"
    echo "   然后编辑 .env 填入你的数据库用户名和密码"
    exit 1
fi

# 加载环境变量
source "$SCRIPT_DIR/.env"

# 检查必要的环境变量
if [ -z "$DB_USERNAME" ] || [ -z "$DB_PASSWORD" ]; then
    echo "❌ .env 文件中 DB_USERNAME 或 DB_PASSWORD 未设置！"
    echo "   请编辑 .env 填入正确的值"
    exit 1
fi

export DB_USERNAME
export DB_PASSWORD

echo "✅ 环境变量加载成功"
echo "   数据库用户: $DB_USERNAME"
echo ""

# 启动 Spring Boot
cd "$SCRIPT_DIR"
echo "🚀 正在启动后端服务..."
echo "   查看日志: tail -f nohup.out"
echo "   停止服务: kill -9 \$(lsof -ti :8080)"
echo ""

mvn spring-boot:run
