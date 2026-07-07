package service;

import exceptions.PosException;
import exceptions.ValidationException;
import model.CartItem;
import model.Customer;
import model.Payment;
import model.Sale;
import model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SalesService {
    private final InventoryService inventoryService;
    private final List<Sale> sales = new ArrayList<>();
    private int receiptSequence = 1000;

    public SalesService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public Sale checkout(CartService cartService, Customer customer, Payment payment) throws PosException {
        return checkout(cartService, customer, null, payment);
    }

    public Sale checkout(CartService cartService, Customer customer, User cashier, Payment payment) throws PosException {
        if (cartService.isEmpty()) {
            throw new ValidationException("Add at least one product before checkout.");
        }
        BigDecimal total = cartService.getTotal();
        payment.validate(total);

        for (CartItem item : cartService.getItems()) {
            inventoryService.reduceStock(item.getProduct().getSku(), item.getQuantity(), cashier, "Sale " + nextPreviewReceiptNumber());
        }

        int points = total.divide(new BigDecimal("5"), 0, java.math.RoundingMode.DOWN).intValue();
        customer.addLoyaltyPoints(points);

        Sale sale = new Sale(
                nextReceiptNumber(),
                LocalDateTime.now(),
                customer,
                cashier == null ? new model.CashierUser("cashier", "Cashier", "cashier") : cashier,
                cartService.getItems(),
                cartService.getSubtotal(),
                cartService.getDiscountTotal(),
                cartService.getTaxTotal(),
                total,
                payment
        );
        sales.add(0, sale);
        cartService.clear();
        return sale;
    }

    public List<Sale> getSales() {
        return List.copyOf(sales);
    }

    public BigDecimal getRevenue() {
        return sales.stream()
                .map(Sale::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTransactionsCount() {
        return sales.size();
    }

    private String nextReceiptNumber() {
        receiptSequence++;
        return "POS-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + receiptSequence;
    }

    private String nextPreviewReceiptNumber() {
        return "POS-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + (receiptSequence + 1);
    }
}
