package com.ecommerce.app;

public class Clothing extends Product {
    private String size;
    private String material;
    private String color;

    public Clothing(String id, String name, double price, int stock, String size, String material, String color) {
        super(id, name, price, stock);
        this.size = size;
        this.material = material;
        this.color = color;
    }

    @Override
    public double getDiscount() {
        return price * 0.20;
    }

    public String getSize() { return size; }
    public String getMaterial() { return material; }
    public String getColor() { return color; }
}
