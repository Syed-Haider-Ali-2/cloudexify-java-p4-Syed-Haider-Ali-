package com.ecommerce.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderID;
    private Customer customer;
    private List<ShoppingCart.CartItem> items;
    private double totalAmount;
    private String status;
    private String orderDate;

    public Order(String orderID, Customer customer, List<ShoppingCart.CartItem> items, double totalAmount) {
        this.orderID = orderID;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.status = "Shipped";
        this.orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getOrderID() { return orderID; }
    public Customer getCustomer() { return customer; }
    public List<ShoppingCart.CartItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getOrderDate() { return orderDate; }
}
