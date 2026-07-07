package model;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Product {
    private final String sku;
    private String barcode;
    private String name;
    private String category;
    private BigDecimal costPrice;
    private BigDecimal price;
    private boolean taxable;

    protected Product(String sku, String name, String category, BigDecimal price, boolean taxable) {
        this(sku, name, category, sku, BigDecimal.ZERO, price, taxable);
    }

    protected Product(String sku, String name, String category, String barcode, BigDecimal costPrice, BigDecimal price, boolean taxable) {
        this.sku = Objects.requireNonNull(sku);
        setBarcode(barcode);
        setName(name);
        setCategory(category);
        setCostPrice(costPrice);
        setPrice(price);
        this.taxable = taxable;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode == null || barcode.isBlank() ? sku : barcode.trim();
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        this.name = name.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category is required.");
        }
        this.category = category.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        if (costPrice == null || costPrice.signum() < 0) {
            throw new IllegalArgumentException("Cost price must be zero or higher.");
        }
        this.costPrice = costPrice;
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Price must be zero or higher.");
        }
        this.price = price;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    public abstract String getTypeLabel();

    public String getDescription() {
        return name + " (" + getTypeLabel() + ")";
    }
}
