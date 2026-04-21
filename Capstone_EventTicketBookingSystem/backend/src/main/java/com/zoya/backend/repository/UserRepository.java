package com.zoya.backend.repository;

import org.springframework.stereotype.Repository;

import com.zoya.backend.entity.User;


@Repository
public class UserRepository extends JpaRepository<User,Long>{
    
}