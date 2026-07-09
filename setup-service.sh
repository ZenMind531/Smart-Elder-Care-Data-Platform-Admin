#!/bin/bash
# 一键安装 systemd 服务（开机自启）
# 在服务器上执行一次即可

cd "$(dirname "$0")"

echo "🔄 正在编译打包..."
source .env
mvn clean package -DskipTests -q

echo "🔄 安装 service..."
cp smart-eldercare.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable smart-eldercare
systemctl start smart-eldercare

echo "✅ 安装完成，已开机自启"
echo "   状态: systemctl status smart-eldercare"
echo "   日志: journalctl -u smart-eldercare -f"
echo ""
echo "   修改代码后重启: ./restart.sh"
