package com.example.foodprice;

import java.io.Serializable;

/**
 * Product মডেল ক্লাস।
 * এটি Serializable ইমপ্লিমেন্ট করে যাতে ফাইল সিস্টেমে সেভ করা যায়।
 */
public class Product implements Serializable {

    // সিরিয়ালাইজেশনের সময় ডাটা স্ট্যাবিলিটি নিশ্চিত করার জন্য আইডি
    private static final long serialVersionUID = 1L;

    private String nameEn;
    private String nameBn;
    private String category;
    private String region;
    private String unit;
    private String source;
    private double currentPrice;
    private double prevPrice;
    private double stock;
    private double importAmount;
    private double hoarderStock;
    private double hoarderPrice;
    private boolean isEssential;

    // Constructor (সবগুলো প্যারামিটারসহ)
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

    // ─────────────────────────────────────────────────
    //              GETTER METHODS (এরর ফিক্স)
    // ─────────────────────────────────────────────────

    public String getNameEn() { return nameEn; }
    public String getNameBn() { return nameBn; }
    public String getCategory() { return category; }
    public String getRegion() { return region; }
    public String getUnit() { return unit; }
    public String getSource() { return source; }

    public double getCurrentPrice() { return currentPrice; }
    public double getPrevPrice() { return prevPrice; }
    public double getStock() { return stock; }
    public double getImportAmount() { return importAmount; }
    public double getHoarderStock() { return hoarderStock; }
    public double getHoarderPrice() { return hoarderPrice; }

    public boolean isEssential() { return isEssential; }

    // ─────────────────────────────────────────────────
    //              SETTER METHODS (প্রয়োজনীয়)
    // ─────────────────────────────────────────────────

    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public void setNameBn(String nameBn) { this.nameBn = nameBn; }
    public void setCategory(String category) { this.category = category; }
    public void setRegion(String region) { this.region = region; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setSource(String source) { this.source = source; }

    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public void setPrevPrice(double prevPrice) { this.prevPrice = prevPrice; }
    public void setStock(double stock) { this.stock = stock; }
    public void setImportAmount(double importAmount) { this.importAmount = importAmount; }
    public void setHoarderStock(double hoarderStock) { this.hoarderStock = hoarderStock; }
    public void setHoarderPrice(double hoarderPrice) { this.hoarderPrice = hoarderPrice; }

    public void setEssential(boolean essential) { isEssential = essential; }
}