package com.example.repository;

import com.example.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;


// using interface because with this we do no require to manually write implementation spring will make it automatically for us
public interface TodoRepository extends JpaRepository<Todo,Long> {
    
}
