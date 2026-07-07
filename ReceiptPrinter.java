package service;

import model.CashPayment;
import model.CartItem;
import model.Sale;

import java.time.format.DateTimeFormatter;

public class ReceiptPrinter {
    public String print(Sale sale) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("NOVA MART POS\n");
        receipt.append("Receipt: ").append(sale.getReceiptNumber()).append("\n");
        receipt.append("Date: ").append(sale.getSoldAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        receipt.append("Cashier: ").append(sale.getCashier().getFullName()).append("\n");
        receipt.append("Customer: ").append(sale.getCustomer().getName()).append("\n");
        receipt.append("----------------------------------------\n");
        for (CartItem item : sale.getItems()) {
            receipt.append(String.format("%-22s x%-3d %8.2f%n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getNetAmount()));
        }
        receipt.append("----------------------------------------\n");
        receipt.append(String.format("%-28s %9.2f%n", "Subtotal", sale.getSubtotal()));
        receipt.append(String.format("%-28s %9.2f%n", "Discount", sale.getDiscount().negate()));
        receipt.append(String.format("%-28s %9.2f%n", "Tax", sale.getTax()));
        receipt.append(String.format("%-28s %9.2f%n", "TOTAL", sale.getTotal()));
        receipt.append("Payment: ").append(sale.getPayment().getMethodName()).append("\n");
        receipt.append("Amount Paid: ").append(String.format("%.2f", sale.getPayment().getAmount())).append("\n");
        receipt.append(sale.getPayment().getReference()).append("\n");
        if (sale.getPayment() instanceof CashPayment cashPayment) {
            receipt.append("Change: ").append(String.format("%.2f", cashPayment.getChange(sale.getTotal()))).append("\n");
        }
        receipt.append("Loyalty points: ").append(sale.getCustomer().getLoyaltyPoints()).append("\n");
        receipt.append("\nThank you for shopping with us.");
        return receipt.toString();
    }
}
