package model;

public class InventoryItem {
    private final Product product;
    private int stockQuantity;
    private int reorderLevel;

    public InventoryItem(Product product, int stockQuantity, int reorderLevel) {
        if (stockQuantity < 0 || reorderLevel < 0) {
            throw new IllegalArgumentException("Stock values cannot be negative.");
        }
        this.product = product;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
    }

    public Product getProduct() {
        return product;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than zero.");
        }
        stockQuantity += quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (quantity > stockQuantity) {
            throw new IllegalArgumentException("Insufficient stock for " + product.getName() + ".");
        }
        stockQuantity -= quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        if (reorderLevel < 0) {
            throw new IllegalArgumentException("Reorder level cannot be negative.");
        }
        this.reorderLevel = reorderLevel;
    }

    public boolean isLowStock() {
        return stockQuantity <= reorderLevel;
    }
}
