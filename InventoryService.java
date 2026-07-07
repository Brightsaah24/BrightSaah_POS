package service;

import exceptions.StockException;
import exceptions.ValidationException;
import model.InventoryItem;
import model.Product;
import model.RetailProduct;
import model.StockHistoryEntry;
import model.User;
import model.WeightedProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

public class InventoryService {
    private final Map<String, InventoryItem> inventory = new LinkedHashMap<>();
    private final List<StockHistoryEntry> stockHistory = new ArrayList<>();

    public InventoryService() {
        seedDemoInventory();
    }

    public void addItem(InventoryItem item) {
        inventory.put(item.getProduct().getSku(), item);
        recordStock(item, item.getStockQuantity(), "Initial stock", "system");
    }

    public void addProduct(Product product, int quantity, int reorderLevel) throws ValidationException {
        validateProduct(product, quantity, reorderLevel, true);
        addItem(new InventoryItem(product, quantity, reorderLevel));
    }

    public void updateProduct(String sku, String name, String category, String barcode, BigDecimal costPrice,
                              BigDecimal sellingPrice, boolean taxable, int quantity, int reorderLevel) throws ValidationException {
        InventoryItem item = inventory.get(sku);
        if (item == null) {
            throw new ValidationException("Select a valid product to update.");
        }
        validateProductValues(name, category, costPrice, sellingPrice, quantity, reorderLevel);
        Product product = item.getProduct();
        product.setName(name);
        product.setCategory(category);
        product.setBarcode(barcode);
        product.setCostPrice(costPrice);
        product.setPrice(sellingPrice);
        product.setTaxable(taxable);
        int difference = quantity - item.getStockQuantity();
        if (difference > 0) {
            item.increaseStock(difference);
            recordStock(item, difference, "Manual product edit", "admin");
        } else if (difference < 0) {
            item.decreaseStock(Math.abs(difference));
            recordStock(item, difference, "Manual product edit", "admin");
        }
        item.setReorderLevel(reorderLevel);
    }

    public void deleteProduct(String sku) throws ValidationException {
        if (!inventory.containsKey(sku)) {
            throw new ValidationException("Select a valid product to delete.");
        }
        inventory.remove(sku);
    }

    public List<InventoryItem> getAllItems() {
        return inventory.values().stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getName()))
                .toList();
    }

    public Optional<InventoryItem> findBySku(String sku) {
        return Optional.ofNullable(inventory.get(sku));
    }

    public List<InventoryItem> search(String query, String category) {
        String normalizedQuery = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        String normalizedCategory = category == null ? "All" : category;
        return inventory.values().stream()
                .filter(item -> matchesQuery(item, normalizedQuery))
                .filter(item -> "All".equals(normalizedCategory) || item.getProduct().getCategory().equals(normalizedCategory))
                .sorted(Comparator.comparing(item -> item.getProduct().getName()))
                .toList();
    }

    public List<InventoryItem> search(String query) {
        return search(query, "All");
    }

    public List<String> getCategories() {
        TreeSet<String> categories = new TreeSet<>();
        categories.add("All");
        for (InventoryItem item : inventory.values()) {
            categories.add(item.getProduct().getCategory());
        }
        return new ArrayList<>(categories);
    }

    public void restock(String sku, int quantity) throws ValidationException {
        restock(sku, quantity, null);
    }

    public void restock(String sku, int quantity, User user) throws ValidationException {
        InventoryItem item = inventory.get(sku);
        if (item == null) {
            throw new ValidationException("Select a valid product before restocking.");
        }
        try {
            item.increaseStock(quantity);
            recordStock(item, quantity, "Stock increase", username(user));
        } catch (IllegalArgumentException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    public void reduceStock(String sku, int quantity) throws StockException {
        reduceStock(sku, quantity, null, "Sale");
    }

    public void reduceStock(String sku, int quantity, User user, String reason) throws StockException {
        InventoryItem item = inventory.get(sku);
        if (item == null) {
            throw new StockException("Product not found in inventory.");
        }
        try {
            item.decreaseStock(quantity);
            recordStock(item, -quantity, reason, username(user));
        } catch (IllegalArgumentException ex) {
            throw new StockException(ex.getMessage());
        }
    }

    public void manualReduceStock(String sku, int quantity, User user) throws ValidationException {
        try {
            reduceStock(sku, quantity, user, "Manual stock reduction");
        } catch (StockException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    public int countLowStockItems() {
        int count = 0;
        for (InventoryItem item : inventory.values()) {
            if (item.isLowStock()) {
                count++;
            }
        }
        return count;
    }

    public int countProducts() {
        return inventory.size();
    }

    public int countOutOfStockItems() {
        int count = 0;
        for (InventoryItem item : inventory.values()) {
            if (item.getStockQuantity() == 0) {
                count++;
            }
        }
        return count;
    }

    public List<InventoryItem> getLowStockItems() {
        return inventory.values().stream()
                .filter(InventoryItem::isLowStock)
                .sorted(Comparator.comparing(item -> item.getProduct().getName()))
                .toList();
    }

    public List<StockHistoryEntry> getStockHistory() {
        return List.copyOf(stockHistory);
    }

    private boolean matchesQuery(InventoryItem item, String normalizedQuery) {
        if (normalizedQuery.isBlank()) {
            return true;
        }
        Product product = item.getProduct();
        return product.getSku().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || product.getBarcode().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || product.getName().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || product.getCategory().toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }

    private void validateProduct(Product product, int quantity, int reorderLevel, boolean requireUniqueSku) throws ValidationException {
        if (product == null) {
            throw new ValidationException("Product details are required.");
        }
        if (requireUniqueSku && inventory.containsKey(product.getSku())) {
            throw new ValidationException("Product ID already exists.");
        }
        validateProductValues(product.getName(), product.getCategory(), product.getCostPrice(), product.getPrice(), quantity, reorderLevel);
    }

    private void validateProductValues(String name, String category, BigDecimal costPrice, BigDecimal sellingPrice,
                                       int quantity, int reorderLevel) throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Product name is required.");
        }
        if (category == null || category.isBlank()) {
            throw new ValidationException("Category is required.");
        }
        if (costPrice == null || costPrice.signum() < 0) {
            throw new ValidationException("Cost price must be zero or higher.");
        }
        if (sellingPrice == null || sellingPrice.signum() <= 0) {
            throw new ValidationException("Selling price must be greater than zero.");
        }
        if (quantity < 0) {
            throw new ValidationException("Quantity cannot be negative.");
        }
        if (reorderLevel < 0) {
            throw new ValidationException("Reorder level cannot be negative.");
        }
    }

    private void recordStock(InventoryItem item, int quantityChange, String reason, String username) {
        stockHistory.add(0, new StockHistoryEntry(
                LocalDateTime.now(),
                item.getProduct().getSku(),
                item.getProduct().getName(),
                quantityChange,
                item.getStockQuantity(),
                reason,
                username
        ));
    }

    private String username(User user) {
        return user == null ? "system" : user.getUsername();
    }

    private void seedDemoInventory() {
        addItem(new InventoryItem(new RetailProduct("BVG-1001", "Sparkling Water 500ml", "Beverages", "8901001001", bd("0.70"), bd("1.20"), true, "AquaPure"), 80, 15));
        addItem(new InventoryItem(new RetailProduct("BVG-1002", "Orange Juice 1L", "Beverages", "8901001002", bd("2.10"), bd("3.40"), true, "SunDrop"), 34, 10));
        addItem(new InventoryItem(new WeightedProduct("FRU-2001", "Bananas", "Fresh Food", "8902001001", bd("1.05"), bd("1.65"), false, "kg"), 55, 12));
        addItem(new InventoryItem(new WeightedProduct("FRU-2002", "Apples", "Fresh Food", "8902001002", bd("1.40"), bd("2.10"), false, "kg"), 49, 12));
        addItem(new InventoryItem(new RetailProduct("GRY-3001", "Premium Rice 5kg", "Grocery", "8903001001", bd("9.25"), bd("12.75"), false, "Golden Field"), 26, 8));
        addItem(new InventoryItem(new RetailProduct("GRY-3002", "Pasta 500g", "Grocery", "8903001002", bd("1.35"), bd("2.30"), false, "Casa Mia"), 7, 10));
        addItem(new InventoryItem(new RetailProduct("HOM-4001", "Laundry Detergent", "Household", "8904001001", bd("5.90"), bd("8.95"), true, "CleanMax"), 18, 5));
        addItem(new InventoryItem(new RetailProduct("HOM-4002", "Kitchen Towels", "Household", "8904001002", bd("2.60"), bd("4.20"), true, "SoftCare"), 41, 9));
        addItem(new InventoryItem(new RetailProduct("ELC-5001", "USB-C Cable", "Electronics", "8905001001", bd("3.20"), bd("6.50"), true, "Voltix"), 22, 6));
        addItem(new InventoryItem(new RetailProduct("ELC-5002", "Wireless Mouse", "Electronics", "8905001002", bd("11.50"), bd("18.99"), true, "Voltix"), 11, 4));
        addItem(new InventoryItem(new RetailProduct("HLT-6001", "Hand Sanitizer", "Health", "8906001001", bd("1.85"), bd("3.10"), true, "CarePlus"), 36, 8));
        addItem(new InventoryItem(new RetailProduct("HLT-6002", "Vitamin C Tablets", "Health", "8906001002", bd("4.60"), bd("7.80"), true, "CarePlus"), 14, 5));
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
