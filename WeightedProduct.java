package model;

import java.math.BigDecimal;

public class WeightedProduct extends Product {
    private final String unit;

    public WeightedProduct(String sku, String name, String category, BigDecimal price, boolean taxable, String unit) {
        super(sku, name, category, price, taxable);
        this.unit = unit == null || unit.isBlank() ? "kg" : unit.trim();
    }

    public WeightedProduct(String sku, String name, String category, String barcode, BigDecimal costPrice,
                           BigDecimal price, boolean taxable, String unit) {
        super(sku, name, category, barcode, costPrice, price, taxable);
        this.unit = unit == null || unit.isBlank() ? "kg" : unit.trim();
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String getTypeLabel() {
        return "Sold per " + unit;
    }
}
