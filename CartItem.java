package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CartItem {
    private final Product product;
    private int quantity;
    private BigDecimal discountRate;

    public CartItem(Product product, int quantity, BigDecimal discountRate) {
        this.product = product;
        setQuantity(quantity);
        setDiscountRate(discountRate);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.quantity = quantity;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        if (discountRate == null || discountRate.signum() < 0 || discountRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Discount must be between 0% and 100%.");
        }
        this.discountRate = discountRate;
    }

    public BigDecimal getGrossAmount() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDiscountAmount() {
        return getGrossAmount().multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getNetAmount() {
        return getGrossAmount().subtract(getDiscountAmount()).setScale(2, RoundingMode.HALF_UP);
    }
}
