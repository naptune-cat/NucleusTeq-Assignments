package com.example.usermanagementsystem.repository;

//imports
import org.springframework.stereotype.Repository;

import com.example.usermanagementsystem.model.User;

import java.util.List;
import java.util.ArrayList;

//repository is used to handle the db operations

@Repository
public class UserRepository {
    private List<User> users = new ArrayList<>();

    //constructor
    public UserRepository() {
        //filling our arrayList with dummy data
        users.add(new User(1, "Ritika", "ritika@gmail.com"));
        users.add(new User(2, "Piyush", "piyushv@gmailcom"));
        users.add((new User(3, "Arya", "arya@gmail.com")));
    }

    public List<User> getAllUsers() {
        return users;
    }

    public User getUserById(Integer id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    //function for saving Users after we use POST
    public User saveUser(User user) {
        users.add(user);
        return user;
    }

    // function for deleting a user using id
    public boolean deleteUser(Integer id) {
        for (int i = 0; i < users.size(); i++) {
            User currUser = users.get(i);
            if (currUser.getId() == id) {
                users.remove(currUser);
                return true;
            }
        }
        return false;
    }
    
    // function for updating a user
    public boolean updateUser(Integer id,User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            User currUser = users.get(i);
            if (currUser.getId() == id) {
                currUser.setEmail(updatedUser.getEmail());
                currUser.setName(updatedUser.getName());
                return true;
            }
        }
        return false;
    }
    // function for updating a user
    public boolean updatePartialUserDetail(Integer id,User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            User currUser = users.get(i);
            if (currUser.getId() == id) {
                if(updatedUser.getEmail() != null)
                    currUser.setEmail(updatedUser.getEmail());
                if(updatedUser.getName() != null)
                    currUser.setName(updatedUser.getName());
                return true;
            }
        }
        return false;
    }
}
