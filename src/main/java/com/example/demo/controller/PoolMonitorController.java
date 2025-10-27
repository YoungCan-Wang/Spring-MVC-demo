package com.example.demo.controller;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
public class PoolMonitorController {

    @Autowired
    private DataSource dataSource;

    /**
     * 获取连接池状态信息
     */
    @GetMapping("/pool-status")
    public ResponseEntity<Map<String, Object>> getPoolStatus() {
        try {
            HikariDataSource hikariDS = (HikariDataSource) dataSource;
            HikariPoolMXBean poolBean = hikariDS.getHikariPoolMXBean();

            Map<String, Object> status = new HashMap<>();
            
            // 连接数信息
            status.put("activeConnections", poolBean.getActiveConnections());
            status.put("idleConnections", poolBean.getIdleConnections());
            status.put("totalConnections", poolBean.getTotalConnections());
            status.put("threadsAwaitingConnection", poolBean.getThreadsAwaitingConnection());
            
            // 配置信息
            status.put("maximumPoolSize", hikariDS.getMaximumPoolSize());
            status.put("minimumIdle", hikariDS.getMinimumIdle());
            status.put("poolName", hikariDS.getPoolName());
            
            // 计算使用率
            int totalConnections = poolBean.getTotalConnections();
            int activeConnections = poolBean.getActiveConnections();
            double utilizationRate = totalConnections > 0 ? 
                (double) activeConnections / totalConnections * 100 : 0;
            status.put("utilizationRate", String.format("%.2f%%", utilizationRate));
            
            // 健康状态
            boolean isHealthy = poolBean.getThreadsAwaitingConnection() == 0 && 
                               activeConnections < hikariDS.getMaximumPoolSize();
            status.put("isHealthy", isHealthy);
            status.put("status", isHealthy ? "HEALTHY" : "WARNING");

            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get pool status: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 获取连接池详细配置信息
     */
    @GetMapping("/pool-config")
    public ResponseEntity<Map<String, Object>> getPoolConfig() {
        try {
            HikariDataSource hikariDS = (HikariDataSource) dataSource;

            Map<String, Object> config = new HashMap<>();
            config.put("poolName", hikariDS.getPoolName());
            config.put("maximumPoolSize", hikariDS.getMaximumPoolSize());
            config.put("minimumIdle", hikariDS.getMinimumIdle());
            config.put("connectionTimeout", hikariDS.getConnectionTimeout());
            config.put("idleTimeout", hikariDS.getIdleTimeout());
            config.put("maxLifetime", hikariDS.getMaxLifetime());
            config.put("validationTimeout", hikariDS.getValidationTimeout());
            config.put("leakDetectionThreshold", hikariDS.getLeakDetectionThreshold());
            config.put("connectionTestQuery", hikariDS.getConnectionTestQuery());
            config.put("jdbcUrl", hikariDS.getJdbcUrl());
            config.put("username", hikariDS.getUsername());
            // 不返回密码信息

            return ResponseEntity.ok(config);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get pool config: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 获取连接池健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            HikariDataSource hikariDS = (HikariDataSource) dataSource;
            HikariPoolMXBean poolBean = hikariDS.getHikariPoolMXBean();

            Map<String, Object> health = new HashMap<>();
            
            // 检查各项指标
            boolean hasAvailableConnections = poolBean.getIdleConnections() > 0 || 
                poolBean.getTotalConnections() < hikariDS.getMaximumPoolSize();
            boolean noWaitingThreads = poolBean.getThreadsAwaitingConnection() == 0;
            boolean poolNotExhausted = poolBean.getTotalConnections() <= hikariDS.getMaximumPoolSize();
            
            boolean isHealthy = hasAvailableConnections && noWaitingThreads && poolNotExhausted;
            
            health.put("status", isHealthy ? "UP" : "DOWN");
            health.put("hasAvailableConnections", hasAvailableConnections);
            health.put("noWaitingThreads", noWaitingThreads);
            health.put("poolNotExhausted", poolNotExhausted);
            
            // 添加关键指标
            health.put("activeConnections", poolBean.getActiveConnections());
            health.put("totalConnections", poolBean.getTotalConnections());
            health.put("maxPoolSize", hikariDS.getMaximumPoolSize());

            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "DOWN");
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
