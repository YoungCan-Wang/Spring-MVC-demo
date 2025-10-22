package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionExperimentService {

    private final UserRepository userRepository;

    public TransactionExperimentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 实验1：正常事务提交
     * 创建两个用户，都成功则提交事务
     */
    @Transactional
    public void createTwoUsersSuccess() {
        long timestamp = System.currentTimeMillis();
        
        User user1 = new User();
        user1.setName("TxUser1_" + timestamp);
        user1.setRole("tester");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("TxUser2_" + timestamp);
        user2.setRole("developer");
        userRepository.save(user2);

        System.out.println("✅ 两个用户创建成功，事务提交");
    }

    /**
     * 实验2：事务回滚
     * 创建一个用户后抛出异常，整个事务回滚
     */
    @Transactional
    public void createUserThenFail() {
        long timestamp = System.currentTimeMillis();
        
        User user = new User();
        user.setName("RollbackUser_" + timestamp);
        user.setRole("tester");
        userRepository.save(user);

        System.out.println("用户已保存到数据库（但事务未提交）");

        // 模拟业务异常
        throw new RuntimeException("💥 模拟业务异常，触发事务回滚");
    }

    /**
     * 实验3：只读事务
     * 用于查询操作，可以优化性能
     */
    @Transactional(readOnly = true)
    public long countAllUsers() {
        return userRepository.count();
    }

    /**
     * 实验4：事务传播 - REQUIRED（默认）
     * 调用其他事务方法，加入当前事务
     */
    @Transactional
    public void testTransactionPropagation() {
        long timestamp = System.currentTimeMillis();
        
        User user1 = new User();
        user1.setName("PropTest1_" + timestamp);
        user1.setRole("admin");
        userRepository.save(user1);

        // 调用另一个事务方法，会加入当前事务
        createUserInSameTransaction(timestamp);

        System.out.println("✅ 事务传播测试完成");
    }

    /**
     * 被调用的事务方法 - 加入调用者的事务
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void createUserInSameTransaction(long timestamp) {
        User user2 = new User();
        user2.setName("PropTest2_" + timestamp);
        user2.setRole("tester");
        userRepository.save(user2);

        System.out.println("在同一事务中创建第二个用户");
    }

    /**
     * 实验5：新事务传播
     * 无论调用者是否有事务，都创建新事务
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createUserInNewTransaction() {
        long timestamp = System.currentTimeMillis();
        
        User user = new User();
        user.setName("NewTxUser_" + timestamp);
        user.setRole("manager");
        userRepository.save(user);

        System.out.println("✅ 在新事务中创建用户");
    }

    /**
     * 实验6：测试新事务传播的独立性
     */
    @Transactional
    public void testNewTransactionPropagation() {
        long timestamp = System.currentTimeMillis();
        
        User user1 = new User();
        user1.setName("MainTxUser_" + timestamp);
        user1.setRole("admin");
        userRepository.save(user1);

        // 这个方法会在新事务中执行，即使主事务失败也会提交
        createUserInNewTransaction();

        // 主事务失败
        throw new RuntimeException("💥 主事务失败，但新事务应该已经提交");
    }
}
