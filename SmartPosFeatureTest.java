package tests;

import exceptions.ValidationException;
import model.CashPayment;
import model.Customer;
import model.InventoryItem;
import model.RetailProduct;
import model.Sale;
import model.User;
import model.UserRole;
import service.CartService;
import service.CustomerService;
import service.InventoryService;
import service.ReceiptExporter;
import service.ReceiptPrinter;
import service.ReportService;
import service.SalesService;
import service.UserService;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

public class SmartPosFeatureTest {
    public static void main(String[] args) throws Exception {
        UserService userService = new UserService();
        User admin = userService.authenticate("admin", "admin123").orElseThrow();
        User cashier = userService.authenticate("cashier", "cash123").orElseThrow();
        if (!admin.canManageUsers() || cashier.canManageUsers()) {
            throw new IllegalStateException("Role permissions are incorrect.");
        }

        userService.addUser("shift2", "Second Shift Cashier", "shift123", UserRole.CASHIER, true);
        if (userService.authenticate("shift2", "shift123").isEmpty()) {
            throw new IllegalStateException("Created user cannot log in.");
        }

        CustomerService customerService = new CustomerService();
        customerService.addCustomer(new Customer("CUS-9000", "Feature Tester", "555-9000", "tester@example.com", 10));
        if (customerService.findById("CUS-9000").isEmpty()) {
            throw new IllegalStateException("Customer CRUD failed.");
        }

        InventoryService inventoryService = new InventoryService();
        inventoryService.addProduct(new RetailProduct("TST-9000", "Feature Test Item", "Testing", "9000000000",
                new BigDecimal("2.00"), new BigDecimal("5.00"), true, "Test Brand"), 12, 3);
        inventoryService.updateProduct("TST-9000", "Feature Test Item Updated", "Testing", "9000000001",
                new BigDecimal("2.50"), new BigDecimal("6.00"), true, 15, 4);
        InventoryItem testItem = inventoryService.findBySku("TST-9000").orElseThrow();
        if (testItem.getStockQuantity() != 15) {
            throw new IllegalStateException("Product update failed.");
        }

        CartService cartService = new CartService();
        Customer customer = customerService.findById("CUS-9000").orElseThrow();
        cartService.addProduct(testItem, 2, customer);
        SalesService salesService = new SalesService(inventoryService);
        Sale sale = salesService.checkout(cartService, customer, cashier, new CashPayment(cartService.getTotal().add(new BigDecimal("10.00"))));
        ReportService reportService = new ReportService(salesService, inventoryService);
        if (reportService.revenueToday().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Daily report revenue failed.");
        }

        String receipt = new ReceiptPrinter().print(sale);
        Path output = Files.createTempDirectory("smart-pos-test");
        Path txt = new ReceiptExporter().exportTextReceipt(receipt, output, sale.getReceiptNumber());
        Path pdf = new ReceiptExporter().exportSimplePdf(receipt, output, sale.getReceiptNumber());
        if (!Files.exists(txt) || !Files.exists(pdf)) {
            throw new IllegalStateException("Receipt export failed.");
        }

        System.out.println("Bright Saah POS feature test passed: " + sale.getReceiptNumber());
    }
}
