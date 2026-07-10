#!/bin/bash

# 智慧养老平台 - Docker 部署脚本
# 在空白服务器上一条命令部署

set -e

echo "=============================="
echo "  智慧养老平台 - Docker 部署"
echo "=============================="
echo ""

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "🔄 正在安装 Docker..."
    curl -fsSL https://get.docker.com | sh
    echo "✅ Docker 安装完成"
fi

# 检查 docker compose
if ! docker compose version &> /dev/null; then
    echo "🔄 正在安装 Docker Compose..."
    if [ "$(id -u)" -ne 0 ]; then
        echo "❌ 需要 root 权限安装 Docker Compose，请用 sudo 执行"
        exit 1
    fi
    apt update && apt install -y docker-compose-plugin
fi

# 配置数据库密码
if [ ! -f ".env" ]; then
    echo ""
    read -s -p "设置 MySQL root 密码 [默认: root123]: " input_pass
    echo ""
    MYSQL_PASS=${input_pass:-root123}

    # 用单引号包裹（避免 $ ` 被 shell 展开）
    cat > .env << 'EOF'
MYSQL_ROOT_PASSWORD=__MYSQL_PASS__
DB_USERNAME=root
DB_PASSWORD=__MYSQL_PASS__
EOF
    sed -i "s|__MYSQL_PASS__|$MYSQL_PASS|g" .env
    echo "✅ 配置已保存"
fi

# 合并所有 SQL 文件（去掉 CREATE DATABASE / USE 语句）
echo "🔄 正在准备 SQL 文件..."
mkdir -p docker-init
> docker-init/init.sql
sql_files=(src/main/resources/sql/*.sql)
if [ ! -e "${sql_files[0]}" ]; then
    echo "❌ src/main/resources/sql/ 下没有 .sql 文件"
    exit 1
fi
for f in "${sql_files[@]}"; do
    sed '/CREATE DATABASE/d; /USE /d' "$f" >> docker-init/init.sql
    echo "" >> docker-init/init.sql
done

# 启动
echo "🚀 正在启动服务..."
docker compose up -d --build

echo ""
echo "✅ 部署完成！"
echo "   后端地址: http://localhost:8080"
echo "   数据库:    localhost:3306"
echo ""
echo "   查看日志: docker compose logs -f"
echo "   停止服务: docker compose down"
