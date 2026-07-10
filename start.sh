#!/bin/bash

# 智慧养老平台 - 后端启动脚本

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# 首次运行：交互式配置
if [ ! -f ".env" ]; then
    echo "=============================="
    echo "  首次启动，请配置数据库信息"
    echo "=============================="
    echo ""
    echo "选择数据库账号："
    echo "  A) reader   - 只读查询"
    echo "  B) admin    - 万能账号"
    echo "  C) backup   - 备份专用"
    echo "  D) 自定义"
    echo ""

    read -p "请选择 [A/B/C/D]: " choice

    case $choice in
        A|a) DB_USERNAME=reader ;;
        B|b) DB_USERNAME=admin ;;
        C|c) DB_USERNAME=backup ;;
        D|d)
            read -p "请输入数据库用户名: " DB_USERNAME
            ;;
        *)
            echo "❌ 无效选择"
            exit 1
            ;;
    esac

    read -s -p "请输入数据库密码: " DB_PASSWORD
    echo ""

    if [ -z "$DB_PASSWORD" ]; then
        echo "❌ 密码不能为空"
        exit 1
    fi

    printf "export DB_USERNAME=%q\n" "$DB_USERNAME" > .env
    printf "export DB_PASSWORD=%q\n" "$DB_PASSWORD" >> .env

    echo "✅ 配置已保存到 .env"
    echo ""
fi

# 加载环境变量
source .env

echo "✅ 数据库用户: $DB_USERNAME"

# 启动 Spring Boot（直接传入变量，不管 source 是否生效）
echo "🚀 正在启动后端服务..."
echo "   启动后访问: http://localhost:8080"
echo ""

DB_USERNAME="$DB_USERNAME" DB_PASSWORD="$DB_PASSWORD" mvn spring-boot:run
