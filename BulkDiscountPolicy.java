package service;

import model.CartItem;
import model.Customer;

import java.math.BigDecimal;

public class BulkDiscountPolicy implements DiscountPolicy {
    @Override
    public BigDecimal discountFor(CartItem item, Customer customer) {
        if (item.getQuantity() >= 10) {
            return new BigDecimal("0.10");
        }
        if (item.getQuantity() >= 5) {
            return new BigDecimal("0.05");
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String getName() {
        return "Bulk quantity discount";
    }
}
