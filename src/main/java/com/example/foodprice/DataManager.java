package com.example.foodprice;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    // ফাইলের নামসমূহ
    private static final String FARMER_FILE = "farmers_data.bin";
    private static final String PRODUCT_FILE = "products_data.bin";

    // --- কৃষকদের জন্য মেথড ---
    public static void saveFarmers(List<Farmer> farmers) {
        saveToFile(FARMER_FILE, farmers);
    }

    public static List<Farmer> loadFarmers() {
        return loadFromFile(FARMER_FILE);
    }

    // --- পণ্যের জন্য মেথড ---
    public static void saveProducts(List<Product> products) {
        saveToFile(PRODUCT_FILE, products);
    }

    public static List<Product> loadProducts() {
        return loadFromFile(PRODUCT_FILE);
    }

    // --- জেনেরিক সেভ ও লোড লজিক (কোড ডুপ্লিকেশন কমানোর জন্য) ---
    private static void saveToFile(String fileName, List<?> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(new ArrayList<>(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> List<T> loadFromFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}