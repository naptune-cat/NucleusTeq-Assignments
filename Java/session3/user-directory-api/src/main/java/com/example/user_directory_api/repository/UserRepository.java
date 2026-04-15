package com.example.user_directory_api.repository;

import java.util.ArrayList;
import java.util.List;
import com.example.user_directory_api.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final List<User> users = new ArrayList<>();

    //filling the list with dummy data
    //constructor
    public UserRepository() {
        users.add(new User(1, "Anya", 23, "seller"));
        users.add(new User(2, "Loid", 50, "customer"));
        users.add(new User(3, "Megha", 28, "Tester"));
        users.add(new User(4, "Chris", 60, "seller"));
        users.add(new User(5, "Ziyush", 16, "customer"));
        users.add(new User(6, "Tara", 23, "customer"));
    }

    //method for retrieving all users
    public List<User> getAllUsers() {
        return users;
    }

    // function for deleting a user using id
    public boolean deleteUser(Integer id) {
        for (int i = 0; i < users.size(); i++) {
            User currUser = users.get(i);
            if (currUser.getId().equals(id)) {
                users.remove(i);
                return true;
            }
        }
        return false;
    }

    
    public User getUserById(Integer id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public boolean addUser(User currUser){
        return users.add(new User(
            currUser.getId(),
            currUser.getName(),
            currUser.getAge(),
            currUser.getRole()
        ));
    }

}
