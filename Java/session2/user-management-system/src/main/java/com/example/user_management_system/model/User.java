package com.example.user_management_system.model;

//User is like our DB It shows how the schema of our db will be
public class User {
    private int id;
    private String name;
    private String email;

    //constructor used to set values
    public User(int id, String name, String email){
        this.id =id;
        this.name = name;
        this.email=email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}