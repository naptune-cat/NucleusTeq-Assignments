package com.example.user_directory_api.model;

public class User {
    private Integer id;
    private String name;
    private Integer age;
    private String role;

    //constructor
    public User(Integer id, String name, Integer age, String role) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.role = role;
    }


    //getters
    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getId() {
        return id;
    }

    //setters

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

}