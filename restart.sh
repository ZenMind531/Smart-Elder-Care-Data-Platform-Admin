#!/bin/bash
# 重启后端服务
# 用法：拉完代码后执行 ./restart.sh

cd "$(dirname "$0")"
source .env

echo "🔄 正在编译..."
mvn clean package -DskipTests -q

echo "🔄 正在重启服务..."
sudo systemctl restart smart-eldercare

echo "✅ 重启完成"
echo "   查看日志: journalctl -u smart-eldercare -f"
