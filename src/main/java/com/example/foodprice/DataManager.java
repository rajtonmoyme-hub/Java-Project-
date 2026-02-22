package com.example.foodprice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final String FARMER_JSON = "farmers.json";
    private static final String PRODUCT_JSON = "products.json";
    private static final String WAREHOUSE_JSON = "warehouse.json";
    private static final String SUPPLY_JSON = "supply_chain.json";
    private static final String SUPPLY_HISTORY_JSON = "supply_history.json";
    private static final String IMPORT_JSON = "import_records.json";
    private static final String ADMIN_JSON = "admins.json";
    private static final String USER_JSON = "users.json";

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveFarmers(List<Farmer> farmers) {
        try (Writer writer = new FileWriter(FARMER_JSON)) {
            gson.toJson(farmers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Farmer> loadFarmers() {
        File file = new File(FARMER_JSON);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Farmer>>() {}.getType();
            List<Farmer> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveProducts(List<Product> products) {
        try (Writer writer = new FileWriter(PRODUCT_JSON)) {
            gson.toJson(products, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Product> loadProducts() {
        File file = new File(PRODUCT_JSON);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Product>>() {}.getType();
            List<Product> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveWarehouses(List<Warehouse> list) {
        try (Writer writer = new FileWriter(WAREHOUSE_JSON)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Warehouse> loadWarehouses() {
        File file = new File(WAREHOUSE_JSON);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Warehouse>>() {}.getType();
            List<Warehouse> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveSupplyEntries(List<SupplyEntry> list) {
        try (Writer writer = new FileWriter(SUPPLY_JSON)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<SupplyEntry> loadSupplyEntries() {
        File file = new File(SUPPLY_JSON);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<SupplyEntry>>() {}.getType();
            List<SupplyEntry> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveSupplyHistory(List<SupplyEntry> list) {
        try (Writer writer = new FileWriter(SUPPLY_HISTORY_JSON)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<SupplyEntry> loadSupplyHistory() {
        File file = new File(SUPPLY_HISTORY_JSON);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<SupplyEntry>>() {}.getType();
            List<SupplyEntry> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveImportRecords(List<ImportRecord> list) {
        try (Writer writer = new FileWriter(IMPORT_JSON)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ImportRecord> loadImportRecords() {
        File file = new File(IMPORT_JSON);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<ImportRecord>>() {}.getType();
            List<ImportRecord> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveAdmins(List<AdminUser> admins) {
        try (Writer writer = new FileWriter(ADMIN_JSON)) {
            gson.toJson(admins, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<AdminUser> loadAdmins() {
        File file = new File(ADMIN_JSON);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<AdminUser>>() {}.getType();
            List<AdminUser> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveUsers(List<User> users) {
        try (Writer writer = new FileWriter(USER_JSON)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<User> loadUsers() {
        File file = new File(USER_JSON);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<User>>() {}.getType();
            List<User> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
