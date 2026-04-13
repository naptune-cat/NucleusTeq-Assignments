package com.example.usermanagementsystem.model;

//User is like our DB It shows how the schema of our db will be
public class User {
    private Integer id;
    private String name;
    private String email;

    //constructor used to set values
    public User(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    //empty constructor for PUT method
    public User() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Integer id) {
    this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setName(String name) {
        this.name = name;
    }
}