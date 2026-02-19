package com.example.foodprice;

import java.io.Serializable;

public class Product implements Serializable {
    private String nameEn, nameBn, category, region, unit, source;
    private double currentPrice, prevPrice, stock, importAmount, hoarderStock, hoarderPrice;
    private boolean isEssential;

    public Product(String nameEn, String nameBn, String category, String region,
                   double currentPrice, double prevPrice, double stock, String unit,
                   String source, double importAmount, double hoarderStock,
                   double hoarderPrice, boolean isEssential) {
        this.nameEn = nameEn;
        this.nameBn = nameBn;
        this.category = category;
        this.region = region;
        this.currentPrice = currentPrice;
        this.prevPrice = prevPrice;
        this.stock = stock;
        this.unit = unit;
        this.source = source;
        this.importAmount = importAmount;
        this.hoarderStock = hoarderStock;
        this.hoarderPrice = hoarderPrice;
        this.isEssential = isEssential;
    }

    // Getters
    public String getNameBn() { return nameBn; }
    public String getCategory() { return category; }
    public double getCurrentPrice() { return currentPrice; }
    public double getStock() { return stock; }
    public String getUnit() { return unit; }
    // ... প্রয়োজনে অন্য সব Getter যোগ করতে পারেন
}