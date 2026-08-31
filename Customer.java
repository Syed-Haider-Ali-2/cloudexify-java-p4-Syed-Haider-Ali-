package com.ecommerce.app;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String customerID;
    private String name;
    private List<Order> orders;

    public Customer(String customerID, String name) {
        this.customerID = customerID;
        this.name = name;
        this.orders = new ArrayList<>();
    }

    public void purchaseOrder(Order order) {
        orders.add(order);
    }

    public String getCustomerID() { return customerID; }
    public String getName() { return name; }
    public List<Order> getOrders() { return orders; }
}
