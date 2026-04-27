package com.example.demo.api;

import com.example.demo.core.Order;

public class OrderService {

    Order current;

    public Order place() {
        return new Order();
    }
}
