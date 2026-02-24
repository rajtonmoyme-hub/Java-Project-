package com.example.foodprice;

import java.io.Serializable;

public class ImportRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String product;
    private String source;
    private String category;
    private double quantityMt;
    private double fobPrice;
    private double landingCost;
    private String port;
    private String date;
    private String importerName;
    private String licenseNumber;
    private String lcNumber;
    private double dutyPaid;
    private boolean customsCleared;
    private String status;


    public ImportRecord(String product, String source, String category, double quantityMt, double fobPrice, double landingCost,
                        String port, String date, String importerName, String licenseNumber, String lcNumber,
                        double dutyPaid, boolean customsCleared, String status) {
        this.product = product;
        this.source = source;
        this.category = category;
        this.quantityMt = quantityMt;
        this.fobPrice = fobPrice;
        this.landingCost = landingCost;
        this.port = port;
        this.date = date;
        this.importerName = importerName;
        this.licenseNumber = licenseNumber;
        this.lcNumber = lcNumber;
        this.dutyPaid = dutyPaid;
        this.customsCleared = customsCleared;
        this.status = status;
    }

    public String getProduct() { return product; }
    public String getSource() { return source; }
    public String getCategory() { return category; }
    public double getQuantityMt() { return quantityMt; }
    public double getFobPrice() { return fobPrice; }
    public double getLandingCost() { return landingCost; }
    public String getPort() { return port; }
    public String getDate() { return date; }
    public String getImporterName() { return importerName; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getLcNumber() { return lcNumber; }
    public double getDutyPaid() { return dutyPaid; }
    public boolean isCustomsCleared() { return customsCleared; }
    public String getStatus() { return status; }
}
