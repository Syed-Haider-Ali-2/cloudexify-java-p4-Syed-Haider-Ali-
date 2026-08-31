package com.ecommerce.app;

public class Electronics extends Product {
    private String brand;
    private int warrantyMonths;

    public Electronics(String id, String name, double price, int stock, String brand, int warrantyMonths) {
        super(id, name, price, stock);
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public double getDiscount() {
        return price * 0.10;
    }

    public String getBrand() { return brand; }
    public int getWarrantyMonths() { return warrantyMonths; }
}
