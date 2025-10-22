package com.example.demo.repository;

import com.example.demo.dto.RoleCountDto;

import com.example.demo.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;



import java.util.List;



public interface UserRepository extends JpaRepository<User, Integer> {



    @Query("SELECT new com.example.demo.dto.RoleCountDto(u.role, COUNT(u)) FROM User u GROUP BY u.role ORDER BY u.role")

    List<RoleCountDto> countUsersByRole();



    @Query("SELECT new com.example.demo.dto.RoleCountDto(u.role, COUNT(u)) FROM User u GROUP BY u.role HAVING COUNT(u) > :minCount ORDER BY u.role")

    List<RoleCountDto> countUsersByRoleHavingMinCount(@Param("minCount") long minCount);



    @Query("SELECT u FROM User u WHERE u.name = :name AND u.role = :role")

    List<User> findByNameAndRole(@Param("name") String name, @Param("role") String role);

}


