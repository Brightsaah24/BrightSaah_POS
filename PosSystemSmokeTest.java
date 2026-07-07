package tests;

import model.CashPayment;
import model.Customer;
import model.InventoryItem;
import model.Sale;
import service.CartService;
import service.InventoryService;
import service.ReceiptPrinter;
import service.SalesService;

import java.math.BigDecimal;

public class PosSystemSmokeTest {
    public static void main(String[] args) throws Exception {
        InventoryService inventoryService = new InventoryService();
        CartService cartService = new CartService();
        SalesService salesService = new SalesService(inventoryService);
        Customer customer = new Customer("Test Customer", "555-0100", 60);

        InventoryItem cable = inventoryService.findBySku("ELC-5001").orElseThrow();
        int startingStock = cable.getStockQuantity();
        cartService.addProduct(cable, 2, customer);

        BigDecimal totalDue = cartService.getTotal();
        Sale sale = salesService.checkout(cartService, customer, new CashPayment(totalDue.add(new BigDecimal("5.00"))));
        int endingStock = inventoryService.findBySku("ELC-5001").orElseThrow().getStockQuantity();

        if (endingStock != startingStock - 2) {
            throw new IllegalStateException("Stock was not reduced correctly.");
        }
        if (sale.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Sale total should be greater than zero.");
        }
        if (!new ReceiptPrinter().print(sale).contains(sale.getReceiptNumber())) {
            throw new IllegalStateException("Receipt was not generated correctly.");
        }

        System.out.println("POS smoke test passed: " + sale.getReceiptNumber());
    }
}
