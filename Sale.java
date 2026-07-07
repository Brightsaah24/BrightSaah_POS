package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Sale {
    private final String receiptNumber;
    private final LocalDateTime soldAt;
    private final Customer customer;
    private final User cashier;
    private final List<CartItem> items;
    private final BigDecimal subtotal;
    private final BigDecimal discount;
    private final BigDecimal tax;
    private final BigDecimal total;
    private final Payment payment;

    public Sale(String receiptNumber, LocalDateTime soldAt, Customer customer, List<CartItem> items,
                BigDecimal subtotal, BigDecimal discount, BigDecimal tax, BigDecimal total, Payment payment) {
        this(receiptNumber, soldAt, customer, new CashierUser("cashier", "Cashier", "cashier"), items,
                subtotal, discount, tax, total, payment);
    }

    public Sale(String receiptNumber, LocalDateTime soldAt, Customer customer, User cashier, List<CartItem> items,
                BigDecimal subtotal, BigDecimal discount, BigDecimal tax, BigDecimal total, Payment payment) {
        this.receiptNumber = receiptNumber;
        this.soldAt = soldAt;
        this.customer = customer;
        this.cashier = cashier;
        this.items = List.copyOf(items);
        this.subtotal = subtotal;
        this.discount = discount;
        this.tax = tax;
        this.total = total;
        this.payment = payment;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public LocalDateTime getSoldAt() {
        return soldAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public User getCashier() {
        return cashier;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Payment getPayment() {
        return payment;
    }
}
