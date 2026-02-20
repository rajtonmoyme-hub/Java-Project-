package com.example.foodprice;

import java.io.Serializable;

public class Warehouse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String owner;
    private String type;
    private String region;
    private String address;
    private String licenseNo;
    private String contact;
    private double capacity;
    private double currentStock;
    private String expiryDate;
    private boolean hasColdStorage;

    // Constructor
    public Warehouse(String name, String owner, String type, String region, String address,
                     double capacity, double currentStock, String licenseNo,
                     String expiryDate, String contact, boolean hasColdStorage) {
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.region = region;
        this.address = address;
        this.capacity = capacity;
        this.currentStock = currentStock;
        this.licenseNo = licenseNo;
        this.expiryDate = expiryDate;
        this.contact = contact;
        this.hasColdStorage = hasColdStorage;
    }

    // ─────────────────────────────────────────────────
    //              সবগুলো GETTER মেথড (এরর ফিক্স)
    // ─────────────────────────────────────────────────

    public String getName() { return name; }
    public String getOwner() { return owner; } // এই মেথডটি মিসিং ছিল
    public String getType() { return type; }
    public String getRegion() { return region; }
    public String getAddress() { return address; }
    public String getLicenseNo() { return licenseNo; }
    public String getContact() { return contact; }
    public double getCapacity() { return capacity; }
    public double getCurrentStock() { return currentStock; }
    public String getExpiryDate() { return expiryDate; }
    public boolean isHasColdStorage() { return hasColdStorage; }

    // ─────────────────────────────────────────────────
    //              সবগুলো SETTER মেথড
    // ─────────────────────────────────────────────────

    public void setName(String name) { this.name = name; }
    public void setOwner(String owner) { this.owner = owner; }
    public void setType(String type) { this.type = type; }
    public void setRegion(String region) { this.region = region; }
    public void setAddress(String address) { this.address = address; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    public void setContact(String contact) { this.contact = contact; }
    public void setCapacity(double capacity) { this.capacity = capacity; }
    public void setCurrentStock(double currentStock) { this.currentStock = currentStock; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public void setHasColdStorage(boolean hasColdStorage) { this.hasColdStorage = hasColdStorage; }
}