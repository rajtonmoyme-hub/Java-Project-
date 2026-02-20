package com.example.foodprice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    // JSON ফাইলের নামসমূহ
    private static final String FARMER_JSON = "farmers.json";
    private static final String PRODUCT_JSON = "products.json";

    // Gson অবজেক্ট (Pretty Printing অন করা হয়েছে যাতে ফাইলটি সুন্দর দেখায়)
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // ─────────────────────────────────────────────────
    //              FARMER (কৃষক) সেকশন
    // ─────────────────────────────────────────────────

    /**
     * কৃষকদের লিস্ট JSON ফাইলে সেভ করে
     */
    public static void saveFarmers(List<Farmer> farmers) {
        try (Writer writer = new FileWriter(FARMER_JSON)) {
            gson.toJson(farmers, writer);
            System.out.println("✅ Farmers data saved to JSON!");
        } catch (IOException e) {
            System.err.println("❌ Error saving farmers: " + e.getMessage());
        }
    }

    /**
     * JSON ফাইল থেকে কৃষকদের লিস্ট লোড করে
     */
    public static List<Farmer> loadFarmers() {
        File file = new File(FARMER_JSON);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Farmer>>(){}.getType();
            List<Farmer> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }


    // ─────────────────────────────────────────────────
    //              PRODUCT (পণ্য) সেকশন
    // ─────────────────────────────────────────────────

    /**
     * পণ্যের লিস্ট JSON ফাইলে সেভ করে
     */
    public static void saveProducts(List<Product> products) {
        try (Writer writer = new FileWriter(PRODUCT_JSON)) {
            gson.toJson(products, writer);
            System.out.println("✅ Products data saved to JSON!");
        } catch (IOException e) {
            System.err.println("❌ Error saving products: " + e.getMessage());
        }
    }

    /**
     * JSON ফাইল থেকে পণ্যের লিস্ট লোড করে
     */
    public static List<Product> loadProducts() {
        File file = new File(PRODUCT_JSON);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Product>>(){}.getType();
            List<Product> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // Warehouse er jonno
    private static final String WAREHOUSE_JSON = "warehouse.json";
   // private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveWarehouses(List<Warehouse> list) {
        try (Writer writer = new FileWriter(WAREHOUSE_JSON)) {
            gson.toJson(list, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<Warehouse> loadWarehouses() {
        File file = new File(WAREHOUSE_JSON);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Warehouse>>(){}.getType();
            List<Warehouse> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) { return new ArrayList<>(); }
    }
}