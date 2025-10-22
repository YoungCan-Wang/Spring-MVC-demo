package com.example.demo.controller;

import com.example.demo.service.TransactionExperimentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionExperimentService transactionService;

    public TransactionController(TransactionExperimentService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * 实验1：正常事务提交
     */
    @PostMapping("/test-success")
    public ResponseEntity<Map<String, String>> testTransactionSuccess() {
        try {
            transactionService.createTwoUsersSuccess();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "两个用户创建成功，事务已提交"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 实验2：事务回滚
     */
    @PostMapping("/test-rollback")
    public ResponseEntity<Map<String, String>> testTransactionRollback() {
        try {
            transactionService.createUserThenFail();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "不应该看到这条消息"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "status", "rollback",
                "message", "事务已回滚: " + e.getMessage()
            ));
        }
    }

    /**
     * 实验3：查询用户总数（只读事务）
     */
    @GetMapping("/count-users")
    public ResponseEntity<Map<String, Object>> countUsers() {
        long count = transactionService.countAllUsers();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "userCount", count,
            "message", "使用只读事务查询"
        ));
    }

    /**
     * 实验4：事务传播测试
     */
    @PostMapping("/test-propagation")
    public ResponseEntity<Map<String, String>> testTransactionPropagation() {
        try {
            transactionService.testTransactionPropagation();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "事务传播测试成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 实验5：新事务传播测试
     */
    @PostMapping("/test-new-transaction")
    public ResponseEntity<Map<String, String>> testNewTransactionPropagation() {
        try {
            transactionService.testNewTransactionPropagation();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "不应该看到这条消息"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "status", "partial_success",
                "message", "主事务失败但新事务应该成功: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取所有实验的说明
     */
    @GetMapping("/experiments")
    public ResponseEntity<Map<String, Object>> getExperiments() {
        Map<String, Object> experiments = new HashMap<>();
        
        experiments.put("1. 正常事务提交", Map.of(
            "endpoint", "POST /api/transaction/test-success",
            "description", "创建两个用户，观察事务正常提交"
        ));
        
        experiments.put("2. 事务回滚", Map.of(
            "endpoint", "POST /api/transaction/test-rollback",
            "description", "创建用户后抛出异常，观察事务回滚"
        ));
        
        experiments.put("3. 只读事务", Map.of(
            "endpoint", "GET /api/transaction/count-users",
            "description", "使用只读事务查询用户总数"
        ));
        
        experiments.put("4. 事务传播", Map.of(
            "endpoint", "POST /api/transaction/test-propagation",
            "description", "测试REQUIRED传播行为"
        ));
        
        experiments.put("5. 新事务传播", Map.of(
            "endpoint", "POST /api/transaction/test-new-transaction",
            "description", "测试REQUIRES_NEW传播行为"
        ));
        
        return ResponseEntity.ok(Map.of(
            "message", "事务实验列表",
            "experiments", experiments
        ));
    }
}
