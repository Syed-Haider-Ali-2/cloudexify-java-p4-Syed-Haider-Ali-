package com.ecommerce.app;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    public static class CartItem {
        private Product product;
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public double getItemTotal() { return product.getFinalPrice() * quantity; }
    }

    private List<CartItem> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Product p, int qty) throws OutOfStockException {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (p.getStock() < qty) {
            throw new OutOfStockException("Not enough items in stock.");
        }

        for (CartItem item : items) {
            if (item.getProduct().getProductID().equalsIgnoreCase(p.getProductID())) {
                if (p.getStock() < (item.getQuantity() + qty)) {
                    throw new OutOfStockException("Cannot add more. Inventory limit reached.");
                }
                item.quantity += qty;
                return;
            }
        }
        items.add(new CartItem(p, qty));
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getItemTotal();
        }
        return total;
    }

    public void checkout() throws Exception {
        if (items.isEmpty()) {
            throw new Exception("Cart is empty.");
        }
        for (CartItem item : items) {
            item.getProduct().reduceStock(item.getQuantity());
        }
    }

    public List<CartItem> getItems() { return items; }
    public void clearCart() { items.clear(); }
}
