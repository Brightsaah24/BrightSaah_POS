package model;

import java.time.LocalDateTime;

public class StockHistoryEntry {
    private final LocalDateTime changedAt;
    private final String sku;
    private final String productName;
    private final int quantityChange;
    private final int stockAfter;
    private final String reason;
    private final String username;

    public StockHistoryEntry(LocalDateTime changedAt, String sku, String productName, int quantityChange,
                             int stockAfter, String reason, String username) {
        this.changedAt = changedAt;
        this.sku = sku;
        this.productName = productName;
        this.quantityChange = quantityChange;
        this.stockAfter = stockAfter;
        this.reason = reason;
        this.username = username;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public int getStockAfter() {
        return stockAfter;
    }

    public String getReason() {
        return reason;
    }

    public String getUsername() {
        return username;
    }
}
