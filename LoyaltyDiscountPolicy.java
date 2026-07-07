package service;

import model.CartItem;
import model.Customer;

import java.math.BigDecimal;

public class LoyaltyDiscountPolicy implements DiscountPolicy {
    @Override
    public BigDecimal discountFor(CartItem item, Customer customer) {
        if (customer != null && !customer.isWalkIn() && customer.getLoyaltyPoints() >= 50) {
            return new BigDecimal("0.03");
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String getName() {
        return "Loyalty member discount";
    }
}
