package com.example.foodprice;

public class Farmer {
    private String name;
    private String phone;
    private String location; // "Village, Upazila, District"
    private String landAmount;
    private String crops;
    private String totalSales;
    private int score;
    private boolean isVerified;

    public Farmer(String name, String phone, String location, String landAmount, String crops, String totalSales, int score, boolean isVerified) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.landAmount = landAmount;
        this.crops = crops;
        this.totalSales = totalSales;
        this.score = score;
        this.isVerified = isVerified;
    }

    // Getters
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getLocation() { return location; }
    public String getLandAmount() { return landAmount; }
    public String getCrops() { return crops; }
    public String getTotalSales() { return totalSales; }
    public int getScore() { return score; }
    public boolean isVerified() { return isVerified; }
}