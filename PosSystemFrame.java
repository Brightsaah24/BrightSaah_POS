package ui;

import exceptions.PosException;
import exceptions.ValidationException;
import model.CardPayment;
import model.CartItem;
import model.CashPayment;
import model.Customer;
import model.InventoryItem;
import model.MobileMoneyPayment;
import model.Payment;
import model.Sale;
import service.CartService;
import service.InventoryService;
import service.ReceiptPrinter;
import service.SalesService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PosSystemFrame extends JFrame {
    private static final Color APP_BACKGROUND = new Color(244, 247, 250);
    private static final Color SURFACE = Color.WHITE;
    private static final Color SIDEBAR = new Color(24, 25, 30);
    private static final Color TEXT = new Color(33, 37, 41);
    private static final Color MUTED = new Color(101, 111, 124);
    private static final Color ACCENT = new Color(46, 125, 98);
    private static final Color WARNING = new Color(208, 140, 46);
    private static final Color DANGER = new Color(190, 72, 72);
    private static final Color BORDER = new Color(224, 230, 236);

    private final InventoryService inventoryService = new InventoryService();
    private final CartService cartService = new CartService();
    private final SalesService salesService = new SalesService(inventoryService);
    private final ReceiptPrinter receiptPrinter = new ReceiptPrinter();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private final DefaultTableModel productModel = readOnlyTable("SKU", "Product", "Category", "Price", "Stock", "Type");
    private final DefaultTableModel cartModel = readOnlyTable("SKU", "Product", "Qty", "Price", "Disc.", "Line Total");
    private final DefaultTableModel inventoryModel = readOnlyTable("SKU", "Product", "Category", "Price", "Stock", "Reorder", "Status");
    private final DefaultTableModel salesModel = readOnlyTable("Receipt", "Date", "Customer", "Items", "Payment", "Total");

    private JTable productTable;
    private JTable cartTable;
    private JTable inventoryTable;
    private JTable salesTable;

    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JSpinner quantitySpinner;
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JSpinner loyaltySpinner;
    private JTextField taxRateField;
    private JComboBox<String> paymentMethodCombo;
    private JTextField paymentAmountField;
    private JTextField paymentReferenceField;
    private JTextArea receiptArea;
    private JTextArea salesReceiptArea;
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel taxLabel;
    private JLabel totalLabel;
    private JLabel dashboardRevenueLabel;
    private JLabel dashboardTransactionsLabel;
    private JLabel dashboardProductsLabel;
    private JLabel dashboardLowStockLabel;

    public PosSystemFrame() {
        super("Nova Mart POS System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(APP_BACKGROUND);

        add(createSidebar(), BorderLayout.WEST);
        contentPanel.setBackground(APP_BACKGROUND);
        contentPanel.add(createRegisterPanel(), "Register");
        contentPanel.add(createDashboardPanel(), "Dashboard");
        contentPanel.add(createInventoryPanel(), "Inventory");
        contentPanel.add(createSalesPanel(), "Sales");
        add(contentPanel, BorderLayout.CENTER);

        refreshAll();
        cardLayout.show(contentPanel, "Register");
        setSize(1280, 820);
        setLocationRelativeTo(null);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 760));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(26, 18, 18, 18));

        JLabel brand = new JLabel("Nova Mart");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Point of Sale");
        subtitle.setForeground(new Color(175, 181, 190));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(brand);
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(34));
        sidebar.add(navButton("Register", "Register"));
        sidebar.add(navButton("Dashboard", "Dashboard"));
        sidebar.add(navButton("Inventory", "Inventory"));
        sidebar.add(navButton("Sales History", "Sales"));
        sidebar.add(Box.createVerticalGlue());

        JLabel hint = new JLabel("<html><b>Cashier:</b><br>Admin User</html>");
        hint.setForeground(new Color(206, 211, 218));
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(hint);
        return sidebar;
    }

    private JButton navButton(String text, String card) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(new Color(36, 38, 45));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.addActionListener(event -> {
            refreshAll();
            cardLayout.show(contentPanel, card);
        });
        return button;
    }

    private JPanel createRegisterPanel() {
        JPanel page = page("Register");
        JPanel split = new JPanel(new GridBagLayout());
        split.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 14);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.58;
        gbc.weighty = 1;
        split.add(createProductBrowser(), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 0.42;
        split.add(createCheckoutPanel(), gbc);

        page.add(split, BorderLayout.CENTER);
        return page;
    }

    private JPanel createProductBrowser() {
        JPanel panel = cardPanel(new BorderLayout(10, 10));
        panel.add(sectionHeader("Product Catalog", "Search, filter, and add items to the cart."), BorderLayout.NORTH);

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);
        searchField = field("Search SKU, name, or category");
        searchField.getDocument().addDocumentListener(simpleDocumentListener(this::refreshProducts));
        categoryCombo = new JComboBox<>();
        categoryCombo.addActionListener(event -> refreshProducts());
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JButton addButton = primaryButton("Add to Cart");
        addButton.addActionListener(event -> addSelectedProductToCart());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        controls.add(searchField, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        controls.add(categoryCombo, gbc);
        gbc.gridx = 2;
        controls.add(quantitySpinner, gbc);
        gbc.gridx = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        controls.add(addButton, gbc);

        productTable = table(productModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(controls, BorderLayout.NORTH);
        center.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCheckoutPanel() {
        JPanel panel = cardPanel(new BorderLayout(10, 10));
        panel.add(sectionHeader("Checkout", "Cart, customer, payment, and receipt."), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setOpaque(false);

        cartTable = table(cartModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JButton removeButton = secondaryButton("Remove Selected");
        removeButton.addActionListener(event -> removeSelectedCartItem());

        JPanel cartTools = new JPanel(new BorderLayout());
        cartTools.setOpaque(false);
        cartTools.add(removeButton, BorderLayout.EAST);

        JPanel cartSection = new JPanel(new BorderLayout(0, 8));
        cartSection.setOpaque(false);
        cartSection.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        cartSection.add(cartTools, BorderLayout.SOUTH);
        body.add(cartSection, BorderLayout.CENTER);
        body.add(createCheckoutControls(), BorderLayout.SOUTH);

        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCheckoutControls() {
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);
        controls.setBorder(new EmptyBorder(4, 0, 0, 0));

        customerNameField = field("Customer name");
        customerPhoneField = field("Phone");
        loyaltySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        taxRateField = field("7.5");
        taxRateField.setText("7.5");
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Card", "Mobile Money"});
        paymentMethodCombo.addActionListener(event -> updatePaymentDefaults());
        paymentAmountField = field("Amount");
        paymentReferenceField = field("Authorization / transaction ID");

        JButton applyTaxButton = secondaryButton("Apply Tax");
        applyTaxButton.addActionListener(event -> applyTaxRate());
        JButton checkoutButton = primaryButton("Process Sale");
        checkoutButton.addActionListener(event -> processSale());
        JButton clearButton = dangerButton("Clear Cart");
        clearButton.addActionListener(event -> {
            cartService.clear();
            refreshAll();
        });

        subtotalLabel = metricLabel("$0.00");
        discountLabel = metricLabel("$0.00");
        taxLabel = metricLabel("$0.00");
        totalLabel = new JLabel("$0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        totalLabel.setForeground(ACCENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        controls.add(label("Customer"), gbc);
        gbc.gridx = 1;
        controls.add(label("Phone"), gbc);
        gbc.gridx = 2;
        controls.add(label("Loyalty"), gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        controls.add(customerNameField, gbc);
        gbc.gridx = 1;
        controls.add(customerPhoneField, gbc);
        gbc.gridx = 2;
        controls.add(loyaltySpinner, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        controls.add(label("Payment"), gbc);
        gbc.gridx = 1;
        controls.add(label("Amount"), gbc);
        gbc.gridx = 2;
        controls.add(label("Reference"), gbc);
        gbc.gridy = 3;
        gbc.gridx = 0;
        controls.add(paymentMethodCombo, gbc);
        gbc.gridx = 1;
        controls.add(paymentAmountField, gbc);
        gbc.gridx = 2;
        controls.add(paymentReferenceField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        controls.add(label("Tax %"), gbc);
        gbc.gridx = 1;
        controls.add(taxRateField, gbc);
        gbc.gridx = 2;
        controls.add(applyTaxButton, gbc);

        JPanel totals = new JPanel(new GridLayout(4, 2, 8, 4));
        totals.setOpaque(false);
        totals.add(label("Subtotal"));
        totals.add(subtotalLabel);
        totals.add(label("Discount"));
        totals.add(discountLabel);
        totals.add(label("Tax"));
        totals.add(taxLabel);
        totals.add(label("Total Due"));
        totals.add(totalLabel);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(12, 0, 4, 0);
        controls.add(totals, gbc);

        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        actions.setOpaque(false);
        actions.add(clearButton);
        actions.add(checkoutButton);
        gbc.gridy = 6;
        controls.add(actions, gbc);

        receiptArea = new JTextArea(8, 34);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        receiptArea.setForeground(TEXT);
        receiptArea.setBackground(new Color(250, 252, 253));
        receiptArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        controls.add(new JScrollPane(receiptArea), gbc);

        return controls;
    }

    private JPanel createDashboardPanel() {
        JPanel page = page("Dashboard");
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);
        dashboardRevenueLabel = new JLabel();
        dashboardTransactionsLabel = new JLabel();
        dashboardProductsLabel = new JLabel();
        dashboardLowStockLabel = new JLabel();
        cards.add(statCard("Revenue", dashboardRevenueLabel, ACCENT));
        cards.add(statCard("Transactions", dashboardTransactionsLabel, WARNING));
        cards.add(statCard("Products", dashboardProductsLabel, new Color(83, 91, 242)));
        cards.add(statCard("Low Stock", dashboardLowStockLabel, DANGER));
        page.add(cards, BorderLayout.NORTH);

        JTextArea guidance = new JTextArea("""
                System capabilities:
                - Browse products by search text or category.
                - Build a cart with live subtotal, discount, tax, and total calculations.
                - Validate cash, card, and mobile money payments.
                - Reduce inventory automatically after successful checkout.
                - Restock inventory and monitor low-stock products.
                - Save transaction history and generate receipts.
                """);
        guidance.setEditable(false);
        guidance.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        guidance.setForeground(TEXT);
        guidance.setBackground(SURFACE);
        guidance.setBorder(new EmptyBorder(20, 20, 20, 20));
        page.add(wrapCard(guidance), BorderLayout.CENTER);
        return page;
    }

    private JPanel createInventoryPanel() {
        JPanel page = page("Inventory");
        JPanel panel = cardPanel(new BorderLayout(10, 10));
        panel.add(sectionHeader("Inventory Control", "Restock items and identify products that need attention."), BorderLayout.NORTH);
        inventoryTable = table(inventoryModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        JSpinner restockSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
        JButton restockButton = primaryButton("Restock Selected");
        restockButton.addActionListener(event -> restockSelectedProduct(restockSpinner));
        JPanel tools = new JPanel(new BorderLayout(8, 0));
        tools.setOpaque(false);
        tools.add(label("Quantity"), BorderLayout.WEST);
        tools.add(restockSpinner, BorderLayout.CENTER);
        tools.add(restockButton, BorderLayout.EAST);
        panel.add(tools, BorderLayout.SOUTH);
        page.add(panel, BorderLayout.CENTER);
        return page;
    }

    private JPanel createSalesPanel() {
        JPanel page = page("Sales History");
        JPanel panel = cardPanel(new BorderLayout(10, 10));
        panel.add(sectionHeader("Transactions", "Review completed sales and receipt details."), BorderLayout.NORTH);
        salesTable = table(salesModel);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.getSelectionModel().addListSelectionListener(event -> showSelectedSaleReceipt());
        salesReceiptArea = new JTextArea();
        salesReceiptArea.setEditable(false);
        salesReceiptArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        salesReceiptArea.setForeground(TEXT);
        salesReceiptArea.setBackground(new Color(250, 252, 253));
        salesReceiptArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel split = new JPanel(new GridLayout(1, 2, 12, 0));
        split.setOpaque(false);
        split.add(new JScrollPane(salesTable));
        split.add(new JScrollPane(salesReceiptArea));
        panel.add(split, BorderLayout.CENTER);
        page.add(panel, BorderLayout.CENTER);
        return page;
    }

    private void addSelectedProductToCart() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            showError("Select a product first.");
            return;
        }
        String sku = productTable.getValueAt(row, 0).toString();
        InventoryItem item = inventoryService.findBySku(sku).orElse(null);
        try {
            cartService.addProduct(item, (Integer) quantitySpinner.getValue(), currentCustomer());
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void removeSelectedCartItem() {
        int row = cartTable.getSelectedRow();
        if (row >= 0) {
            cartService.removeProduct(cartTable.getValueAt(row, 0).toString());
            refreshAll();
        }
    }

    private void processSale() {
        try {
            cartService.refreshDiscounts(currentCustomer());
            Payment payment = createPayment();
            Sale sale = salesService.checkout(cartService, currentCustomer(), payment);
            receiptArea.setText(receiptPrinter.print(sale));
            refreshAll();
            JOptionPane.showMessageDialog(this, "Sale completed: " + sale.getReceiptNumber(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (PosException | NumberFormatException ex) {
            showError(ex.getMessage());
        }
    }

    private void restockSelectedProduct(JSpinner restockSpinner) {
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            showError("Select an inventory item to restock.");
            return;
        }
        try {
            String sku = inventoryTable.getValueAt(row, 0).toString();
            inventoryService.restock(sku, (Integer) restockSpinner.getValue());
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void applyTaxRate() {
        try {
            BigDecimal percent = new BigDecimal(taxRateField.getText().trim());
            cartService.setTaxRate(percent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            refreshAll();
        } catch (ValidationException | NumberFormatException ex) {
            showError("Enter a valid tax percentage between 0 and 25.");
        }
    }

    private Payment createPayment() throws ValidationException {
        BigDecimal amount = parseMoney(paymentAmountField.getText());
        String method = paymentMethodCombo.getSelectedItem().toString();
        String reference = paymentReferenceField.getText();
        return switch (method) {
            case "Card" -> new CardPayment(amount, reference);
            case "Mobile Money" -> new MobileMoneyPayment(amount, reference);
            default -> new CashPayment(amount);
        };
    }

    private Customer currentCustomer() {
        return new Customer(
                customerNameField == null ? "" : customerNameField.getText(),
                customerPhoneField == null ? "" : customerPhoneField.getText(),
                loyaltySpinner == null ? 0 : (Integer) loyaltySpinner.getValue()
        );
    }

    private void refreshAll() {
        refreshCategories();
        refreshProducts();
        refreshCart();
        refreshInventory();
        refreshSales();
        refreshDashboard();
        updatePaymentDefaults();
    }

    private void refreshCategories() {
        if (categoryCombo == null) {
            return;
        }
        Object selected = categoryCombo.getSelectedItem();
        categoryCombo.setModel(new DefaultComboBoxModel<>(inventoryService.getCategories().toArray(String[]::new)));
        if (selected != null) {
            categoryCombo.setSelectedItem(selected);
        }
    }

    private void refreshProducts() {
        if (productModel == null || categoryCombo == null) {
            return;
        }
        productModel.setRowCount(0);
        String query = searchField == null ? "" : searchField.getText();
        String category = categoryCombo.getSelectedItem() == null ? "All" : categoryCombo.getSelectedItem().toString();
        for (InventoryItem item : inventoryService.search(query, category)) {
            productModel.addRow(new Object[]{
                    item.getProduct().getSku(),
                    item.getProduct().getName(),
                    item.getProduct().getCategory(),
                    money(item.getProduct().getPrice()),
                    item.getStockQuantity(),
                    item.getProduct().getTypeLabel()
            });
        }
    }

    private void refreshCart() {
        if (cartModel == null) {
            return;
        }
        cartService.refreshDiscounts(currentCustomer());
        cartModel.setRowCount(0);
        for (CartItem item : cartService.getItems()) {
            cartModel.addRow(new Object[]{
                    item.getProduct().getSku(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    money(item.getProduct().getPrice()),
                    item.getDiscountRate().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString() + "%",
                    money(item.getNetAmount())
            });
        }
        if (subtotalLabel != null) {
            subtotalLabel.setText(money(cartService.getSubtotal()));
            discountLabel.setText("-" + money(cartService.getDiscountTotal()));
            taxLabel.setText(money(cartService.getTaxTotal()));
            totalLabel.setText(money(cartService.getTotal()));
        }
    }

    private void refreshInventory() {
        if (inventoryModel == null) {
            return;
        }
        inventoryModel.setRowCount(0);
        for (InventoryItem item : inventoryService.getAllItems()) {
            inventoryModel.addRow(new Object[]{
                    item.getProduct().getSku(),
                    item.getProduct().getName(),
                    item.getProduct().getCategory(),
                    money(item.getProduct().getPrice()),
                    item.getStockQuantity(),
                    item.getReorderLevel(),
                    item.isLowStock() ? "Low stock" : "Healthy"
            });
        }
    }

    private void refreshSales() {
        if (salesModel == null) {
            return;
        }
        salesModel.setRowCount(0);
        for (Sale sale : salesService.getSales()) {
            salesModel.addRow(new Object[]{
                    sale.getReceiptNumber(),
                    sale.getSoldAt().toString().replace('T', ' '),
                    sale.getCustomer().getName(),
                    sale.getItems().size(),
                    sale.getPayment().getMethodName(),
                    money(sale.getTotal())
            });
        }
    }

    private void refreshDashboard() {
        if (dashboardRevenueLabel == null) {
            return;
        }
        dashboardRevenueLabel.setText(money(salesService.getRevenue()));
        dashboardTransactionsLabel.setText(String.valueOf(salesService.getTransactionsCount()));
        dashboardProductsLabel.setText(String.valueOf(inventoryService.countProducts()));
        dashboardLowStockLabel.setText(String.valueOf(inventoryService.countLowStockItems()));
    }

    private void updatePaymentDefaults() {
        if (paymentAmountField == null || paymentMethodCombo == null) {
            return;
        }
        if (!paymentAmountField.hasFocus()) {
            paymentAmountField.setText(cartService.getTotal().toPlainString());
        }
        String method = paymentMethodCombo.getSelectedItem() == null ? "Cash" : paymentMethodCombo.getSelectedItem().toString();
        if ("Cash".equals(method)) {
            paymentReferenceField.setText("");
            paymentReferenceField.setEnabled(false);
        } else {
            paymentReferenceField.setEnabled(true);
            if (paymentReferenceField.getText().isBlank()) {
                paymentReferenceField.setText("AUTH1234");
            }
        }
    }

    private void showSelectedSaleReceipt() {
        int row = salesTable.getSelectedRow();
        if (row < 0 || salesReceiptArea == null) {
            return;
        }
        String receiptNumber = salesTable.getValueAt(row, 0).toString();
        for (Sale sale : salesService.getSales()) {
            if (sale.getReceiptNumber().equals(receiptNumber)) {
                salesReceiptArea.setText(receiptPrinter.print(sale));
                break;
            }
        }
    }

    private JPanel page(String title) {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setBackground(APP_BACKGROUND);
        page.setBorder(new EmptyBorder(24, 24, 24, 24));
        JLabel header = new JLabel(title);
        header.setForeground(TEXT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 30));
        page.add(header, BorderLayout.NORTH);
        return page;
    }

    private JPanel cardPanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    private JPanel wrapCard(Component component) {
        JPanel panel = cardPanel(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JPanel sectionHeader(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT);
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(MUTED);
        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        return header;
    }

    private JPanel statCard(String title, JLabel valueLabel, Color accent) {
        JPanel panel = cardPanel(new BorderLayout());
        JLabel titleLabel = label(title);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accent);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setGridColor(new Color(234, 238, 242));
        table.setSelectionBackground(new Color(221, 239, 231));
        table.setSelectionForeground(TEXT);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(235, 239, 244));
        header.setForeground(TEXT);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        if (table.getColumnCount() > 3) {
            table.getColumnModel().getColumn(3).setCellRenderer(right);
        }
        return table;
    }

    private JTextField field(String placeholder) {
        JTextField field = new JTextField();
        field.setToolTipText(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(MUTED);
        return label;
    }

    private JLabel metricLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TEXT);
        return label;
    }

    private JButton primaryButton(String text) {
        return styledButton(text, ACCENT, Color.WHITE);
    }

    private JButton secondaryButton(String text) {
        return styledButton(text, new Color(235, 239, 244), TEXT);
    }

    private JButton dangerButton(String text) {
        return styledButton(text, DANGER, Color.WHITE);
    }

    private JButton styledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(9, 14, 9, 14));
        return button;
    }

    private DefaultTableModel readOnlyTable(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DocumentListener simpleDocumentListener(Runnable runnable) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                runnable.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                runnable.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                runnable.run();
            }
        };
    }

    private BigDecimal parseMoney(String text) throws ValidationException {
        try {
            BigDecimal amount = new BigDecimal(text.trim());
            if (amount.signum() < 0) {
                throw new ValidationException("Payment amount cannot be negative.");
            }
            return amount.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Enter a valid payment amount.");
        }
    }

    private String money(BigDecimal value) {
        return "$" + value.setScale(2, RoundingMode.HALF_UP);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "POS Validation", JOptionPane.WARNING_MESSAGE);
    }
}
