package service;

import exceptions.ValidationException;
import model.CartItem;
import model.Customer;
import model.InventoryItem;
import model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartService {
    private final List<CartItem> items = new ArrayList<>();
    private final List<DiscountPolicy> discountPolicies = List.of(new BulkDiscountPolicy(), new LoyaltyDiscountPolicy());
    private BigDecimal taxRate = new BigDecimal("0.075");

    public void addProduct(InventoryItem inventoryItem, int quantity, Customer customer) throws ValidationException {
        if (inventoryItem == null) {
            throw new ValidationException("Select a product to add.");
        }
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero.");
        }
        int existingQuantity = items.stream()
                .filter(item -> item.getProduct().getSku().equals(inventoryItem.getProduct().getSku()))
                .mapToInt(CartItem::getQuantity)
                .sum();
        if (existingQuantity + quantity > inventoryItem.getStockQuantity()) {
            throw new ValidationException("Not enough stock available for " + inventoryItem.getProduct().getName() + ".");
        }

        Optional<CartItem> existing = items.stream()
                .filter(item -> item.getProduct().getSku().equals(inventoryItem.getProduct().getSku()))
                .findFirst();
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setDiscountRate(calculateBestDiscount(item, customer));
        } else {
            CartItem item = new CartItem(inventoryItem.getProduct(), quantity, BigDecimal.ZERO);
            item.setDiscountRate(calculateBestDiscount(item, customer));
            items.add(item);
        }
    }

    public void removeProduct(String sku) {
        items.removeIf(item -> item.getProduct().getSku().equals(sku));
    }

    public void clear() {
        items.clear();
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public BigDecimal getSubtotal() {
        return items.stream()
                .map(CartItem::getGrossAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDiscountTotal() {
        return items.stream()
                .map(CartItem::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTaxTotal() {
        return items.stream()
                .filter(item -> item.getProduct().isTaxable())
                .map(CartItem::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(taxRate)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        return getSubtotal().subtract(getDiscountTotal()).add(getTaxTotal()).setScale(2, RoundingMode.HALF_UP);
    }

    public void refreshDiscounts(Customer customer) {
        for (CartItem item : items) {
            item.setDiscountRate(calculateBestDiscount(item, customer));
        }
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) throws ValidationException {
        if (taxRate == null || taxRate.signum() < 0 || taxRate.compareTo(new BigDecimal("0.25")) > 0) {
            throw new ValidationException("Tax rate must be between 0% and 25%.");
        }
        this.taxRate = taxRate;
    }

    private BigDecimal calculateBestDiscount(CartItem item, Customer customer) {
        BigDecimal best = BigDecimal.ZERO;
        for (DiscountPolicy policy : discountPolicies) {
            BigDecimal candidate = policy.discountFor(item, customer);
            if (candidate.compareTo(best) > 0) {
                best = candidate;
            }
        }
        return best;
    }
}
