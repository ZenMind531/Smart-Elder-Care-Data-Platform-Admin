#!/bin/bash
# 一键安装 systemd 服务（开机自启）
# 在服务器上执行一次即可

set -e

cd "$(dirname "$0")"

# 检查 Maven，没有就装
if ! command -v mvn &> /dev/null; then
    echo "🔄 正在安装 Maven..."
    apt update && apt install -y maven
fi

# 加载环境变量
if [ ! -f ".env" ]; then
    echo "❌ .env 不存在，请先运行 ./start.sh 配置数据库信息"
    exit 1
fi
source .env

echo "🔄 正在编译打包..."
mvn clean package -DskipTests -q

echo "🔄 安装 service..."
PROJECT_DIR="$(pwd)"
sed "s|__PROJECT_DIR__|$PROJECT_DIR|g" smart-eldercare.service > /etc/systemd/system/smart-eldercare.service
systemctl daemon-reload
systemctl enable smart-eldercare
systemctl start smart-eldercare

echo "✅ 安装完成，已开机自启"
echo "   状态: systemctl status smart-eldercare"
echo "   日志: journalctl -u smart-eldercare -f"
echo ""
echo "   修改代码后重启: ./restart.sh"
