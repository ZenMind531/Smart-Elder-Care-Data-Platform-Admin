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

    cat > .env << EOF
export DB_USERNAME=$DB_USERNAME
export DB_PASSWORD=$DB_PASSWORD
EOF

    echo "✅ 配置已保存到 .env"
    echo ""
fi

# 加载环境变量
source .env

echo "✅ 数据库用户: $DB_USERNAME"

# 测试数据库连接
echo "🔄 正在测试数据库连接..."
mysql -u "$DB_USERNAME" -p"$DB_PASSWORD" -e "SELECT 1;" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ 数据库连接失败！请检查用户名和密码"
    echo "   运行: rm .env && ./start.sh 重新配置"
    exit 1
fi
echo "✅ 数据库连接成功"
echo ""

# 启动 Spring Boot
echo "🚀 正在启动后端服务..."
echo "   启动后访问: http://localhost:8080"
echo ""

mvn spring-boot:run
