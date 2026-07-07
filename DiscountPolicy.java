package service;

import model.CartItem;
import model.Customer;

import java.math.BigDecimal;

public interface DiscountPolicy {
    BigDecimal discountFor(CartItem item, Customer customer);

    String getName();
}
