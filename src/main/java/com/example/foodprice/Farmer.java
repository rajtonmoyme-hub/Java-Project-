package com.example.foodprice;

import java.io.Serializable;

public class Farmer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name, phone, nid, division, district, upazila, village, landAmount, banking, account, crops, totalSales;
    private int score;
    private boolean verified;

    public Farmer() {}

    public Farmer(String name, String phone, String nid, String division, String district, String upazila,
                  String village, String landAmount, String banking, String account, String crops,
                  String totalSales, int score, boolean verified) {
        this.name = name;
        this.phone = phone;
        this.nid = nid;
        this.division = division;
        this.district = district;
        this.upazila = upazila;
        this.village = village;
        this.landAmount = landAmount;
        this.banking = banking;
        this.account = account;
        this.crops = crops;
        this.totalSales = totalSales;
        this.score = score;
        this.verified = verified;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNid() { return nid; }
    public String getDivision() { return division; }
    public String getDistrict() { return district; }
    public String getUpazila() { return upazila; }
    public String getVillage() { return village; }
    public String getLandAmount() { return landAmount; }
    public String getBanking() { return banking; }
    public String getAccount() { return account; }
    public String getCrops() { return crops; }
    public void setCrops(String crops) { this.crops = crops; }
    public String getTotalSales() { return totalSales; }
    public int getScore() { return score; }
    public boolean isVerified() { return verified; }

    // UI এর জন্য লোকেশন স্ট্রিং তৈরি
    public String getLocation() {
        return village + ", " + upazila + ", " + district;
    }
}