package com.ecommerce.app;

public class Book extends Product {
    private String author;
    private String isbn;
    private int pages;

    public Book(String id, String name, double price, int stock, String author, String isbn, int pages) {
        super(id, name, price, stock);
        this.author = author;
        this.isbn = isbn;
        this.pages = pages;
    }

    @Override
    public double getDiscount() {
        return price * 0.15;
    }

    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public int getPages() { return pages; }
}
