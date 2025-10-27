package com.example.demo.service;

import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Service
public class SlowQueryService {

    private final DataSource dataSource;
    private final UserRepository userRepository;

    public SlowQueryService(DataSource dataSource, UserRepository userRepository) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
    }

    /**
     * 模拟慢查询 - 使用 SLEEP 函数
     */
    @Transactional(readOnly = true)
    public String slowQuery(int sleepSeconds) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT SLEEP(?), COUNT(*) FROM users")) {
            
            stmt.setInt(1, sleepSeconds);
            System.out.println("🐌 开始执行慢查询，预计耗时: " + sleepSeconds + " 秒");
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userCount = rs.getInt(2);
                System.out.println("✅ 慢查询完成，用户总数: " + userCount);
                return "慢查询完成，耗时 " + sleepSeconds + " 秒，用户总数: " + userCount;
            }
            
        } catch (Exception e) {
            System.out.println("❌ 慢查询失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
        
        return "查询异常";
    }

    /**
     * 模拟复杂查询 - 占用连接时间较长
     */
    @Transactional(readOnly = true)
    public String complexQuery() {
        try {
            System.out.println("🔄 开始执行复杂查询...");
            
            // 模拟复杂业务逻辑
            Thread.sleep(3000); // 3秒
            
            long count = userRepository.count();
            System.out.println("✅ 复杂查询完成");
            
            return "复杂查询完成，用户总数: " + count;
            
        } catch (Exception e) {
            System.out.println("❌ 复杂查询失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }
}
