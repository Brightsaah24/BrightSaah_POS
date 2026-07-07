package model;

import java.math.BigDecimal;

public class RetailProduct extends Product {
    private final String brand;

    public RetailProduct(String sku, String name, String category, BigDecimal price, boolean taxable, String brand) {
        super(sku, name, category, price, taxable);
        this.brand = brand == null || brand.isBlank() ? "House Brand" : brand.trim();
    }

    public RetailProduct(String sku, String name, String category, String barcode, BigDecimal costPrice,
                         BigDecimal price, boolean taxable, String brand) {
        super(sku, name, category, barcode, costPrice, price, taxable);
        this.brand = brand == null || brand.isBlank() ? "House Brand" : brand.trim();
    }

    public String getBrand() {
        return brand;
    }

    @Override
    public String getTypeLabel() {
        return brand + " Retail";
    }
}
