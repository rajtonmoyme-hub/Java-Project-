package com.example.foodprice;

import java.io.Serializable;

public class SupplyEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private String product, stage, location, region, amount, price, transportMode;
    private double efficiency, wastePercent, delayHours;
    private boolean completed;
    private String completedAt;

    public SupplyEntry(String product, String stage, String location, String region, String amount, String price,
                       String transportMode, double efficiency, double wastePercent, double delayHours) {
        this(product, stage, location, region, amount, price, transportMode, efficiency, wastePercent, delayHours, false, null);
    }

    public SupplyEntry(String product, String stage, String location, String region, String amount, String price,
                       String transportMode, double efficiency, double wastePercent, double delayHours,
                       boolean completed, String completedAt) {
        this.product = product;
        this.stage = stage;
        this.location = location;
        this.region = region;
        this.amount = amount;
        this.price = price;
        this.transportMode = transportMode;
        this.efficiency = efficiency;
        this.wastePercent = wastePercent;
        this.delayHours = delayHours;
        this.completed = completed;
        this.completedAt = completedAt;
    }

    // Getters
    public String getProduct() { return product; }
    public String getStage() { return stage; }
    public String getLocation() { return location; }
    public String getRegion() { return region; }
    public String getAmount() { return amount; }
    public String getPrice() { return price; }
    public String getTransportMode() { return transportMode; }
    public double getEfficiency() { return efficiency; }
    public double getWastePercent() { return wastePercent; }
    public double getDelayHours() { return delayHours; }
    public boolean isCompleted() { return completed; }
    public String getCompletedAt() { return completedAt; }
}
