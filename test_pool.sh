#!/bin/bash

echo "=== 连接池压力测试 ==="

# 查看初始状态
echo "1. 初始状态:"
curl -s http://localhost:8080/api/monitor/pool-status | python3 -c "
import json, sys
data = json.load(sys.stdin)
print(f'  活跃连接: {data[\"activeConnections\"]}')
print(f'  空闲连接: {data[\"idleConnections\"]}')
print(f'  总连接数: {data[\"totalConnections\"]}')
"

echo -e "\n2. 启动8个并发慢查询(每个10秒)..."

# 启动8个并发慢查询
for i in {1..8}; do
  echo "  启动查询 $i"
  curl -s "http://localhost:8080/api/slow/query/10" > /dev/null &
done

echo -e "\n3. 等待2秒后查看连接池状态..."
sleep 2

echo "忙碌时状态:"
curl -s http://localhost:8080/api/monitor/pool-status | python3 -c "
import json, sys
data = json.load(sys.stdin)
print(f'  🔴 活跃连接: {data[\"activeConnections\"]} (正在执行查询)')
print(f'  💤 空闲连接: {data[\"idleConnections\"]}')
print(f'  📊 总连接数: {data[\"totalConnections\"]} / {data[\"maximumPoolSize\"]}')
print(f'  ⏳ 等待线程: {data[\"threadsAwaitingConnection\"]} (排队中)')
print(f'  📈 使用率: {data[\"utilizationRate\"]}')
"

echo -e "\n4. 再等待3秒查看状态变化..."
sleep 3

echo "3秒后状态:"
curl -s http://localhost:8080/api/monitor/pool-status | python3 -c "
import json, sys
data = json.load(sys.stdin)
print(f'  🔴 活跃连接: {data[\"activeConnections\"]}')
print(f'  💤 空闲连接: {data[\"idleConnections\"]}')
print(f'  📊 总连接数: {data[\"totalConnections\"]} / {data[\"maximumPoolSize\"]}')
print(f'  ⏳ 等待线程: {data[\"threadsAwaitingConnection\"]}')
"

echo -e "\n测试完成！等待所有查询结束..."
