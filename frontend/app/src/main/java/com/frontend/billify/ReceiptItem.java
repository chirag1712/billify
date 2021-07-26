package com.frontend.billify;

public class ReceiptItem {
    private String name;
    private double price;

    public ReceiptItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return Double.toString(price);
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ReceiptItem{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}