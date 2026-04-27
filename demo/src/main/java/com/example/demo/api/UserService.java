package com.example.demo.api;

import com.example.demo.core.User;
import com.example.demo.persistence.OrderRepository;

public class UserService {

    OrderRepository repo;

    public User loadUser(int id) {
        return new User();
    }
}
