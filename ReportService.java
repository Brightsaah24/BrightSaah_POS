package service;

import model.CartItem;
import model.InventoryItem;
import model.Sale;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportService {
    private final SalesService salesService;
    private final InventoryService inventoryService;

    public ReportService(SalesService salesService, InventoryService inventoryService) {
        this.salesService = salesService;
        this.inventoryService = inventoryService;
    }

    public BigDecimal revenueToday() {
        LocalDate today = LocalDate.now();
        return revenueFor(sale -> sale.getSoldAt().toLocalDate().equals(today));
    }

    public BigDecimal revenueThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields fields = WeekFields.of(Locale.getDefault());
        int week = today.get(fields.weekOfWeekBasedYear());
        int year = today.get(fields.weekBasedYear());
        return revenueFor(sale -> sale.getSoldAt().get(fields.weekOfWeekBasedYear()) == week
                && sale.getSoldAt().get(fields.weekBasedYear()) == year);
    }

    public BigDecimal revenueThisMonth() {
        LocalDate today = LocalDate.now();
        return revenueFor(sale -> sale.getSoldAt().getMonth() == today.getMonth()
                && sale.getSoldAt().getYear() == today.getYear());
    }

    public List<ProductSales> productSalesRanked(boolean bestSelling) {
        Map<String, ProductSales> totals = new LinkedHashMap<>();
        for (Sale sale : salesService.getSales()) {
            for (CartItem item : sale.getItems()) {
                ProductSales current = totals.getOrDefault(item.getProduct().getSku(),
                        new ProductSales(item.getProduct().getSku(), item.getProduct().getName(), 0, BigDecimal.ZERO));
                totals.put(item.getProduct().getSku(), current.add(item.getQuantity(), item.getNetAmount()));
            }
        }
        Comparator<ProductSales> comparator = Comparator.comparingInt(ProductSales::quantitySold);
        if (bestSelling) {
            comparator = comparator.reversed();
        }
        return totals.values().stream().sorted(comparator).toList();
    }

    public List<InventoryItem> lowStockProducts() {
        return inventoryService.getLowStockItems();
    }

    public String summary() {
        return """
                Sales Report
                Daily Sales: %s
                Weekly Sales: %s
                Monthly Sales: %s
                Total Revenue: %s
                Transactions: %d
                Low Stock Products: %d
                Out of Stock Products: %d
                """.formatted(
                revenueToday(),
                revenueThisWeek(),
                revenueThisMonth(),
                salesService.getRevenue(),
                salesService.getTransactionsCount(),
                inventoryService.countLowStockItems(),
                inventoryService.countOutOfStockItems()
        );
    }

    private BigDecimal revenueFor(SalePredicate predicate) {
        return salesService.getSales().stream()
                .filter(predicate::matches)
                .map(Sale::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private interface SalePredicate {
        boolean matches(Sale sale);
    }

    public record ProductSales(String sku, String productName, int quantitySold, BigDecimal revenue) {
        public ProductSales add(int quantity, BigDecimal amount) {
            return new ProductSales(sku, productName, quantitySold + quantity, revenue.add(amount));
        }
    }
}
