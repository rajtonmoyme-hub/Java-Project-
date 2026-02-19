package com.example.foodprice;

import java.io.Serializable;

/**
 * Represents a farmer in the food price / farmer management system.
 * Updated to implement Serializable for file persistence.
 */
public class Farmer implements Serializable {

    // serialVersionUID ensures that saved data remains compatible
    // even if you change the class slightly later.
    private static final long serialVersionUID = 1L;

    private String name;
    private String phone;
    private String location;
    private String landAmount;
    private String crops;
    private String totalSales;
    private int score;
    private boolean verified;

    // Default constructor - REQUIRED for Gson and some serialization frameworks
    public Farmer() {
    }

    // Parameterized constructor - used when creating new farmers
    public Farmer(String name, String phone, String location,
                  String landAmount, String crops, String totalSales,
                  int score, boolean verified) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.landAmount = landAmount;
        this.crops = crops;
        this.totalSales = totalSales;
        this.score = score;
        this.verified = verified;
    }

    // ───────────────────────────────────────────────
    // Getters
    // ───────────────────────────────────────────────

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getLocation() { return location; }
    public String getLandAmount() { return landAmount; }
    public String getCrops() { return crops; }
    public String getTotalSales() { return totalSales; }
    public int getScore() { return score; }
    public boolean isVerified() { return verified; }

    // ───────────────────────────────────────────────
    // Setters
    // ───────────────────────────────────────────────

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setLocation(String location) { this.location = location; }
    public void setLandAmount(String landAmount) { this.landAmount = landAmount; }
    public void setCrops(String crops) { this.crops = crops; }
    public void setTotalSales(String totalSales) { this.totalSales = totalSales; }
    public void setScore(int score) { this.score = score; }
    public void setVerified(boolean verified) { this.verified = verified; }

    // ───────────────────────────────────────────────
    // Optional: toString() - Useful for debugging
    // ───────────────────────────────────────────────

    @Override
    public String toString() {
        return "Farmer{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                ", verified=" + verified +
                '}';
    }
}