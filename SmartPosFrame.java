package ui;

import exceptions.PosException;
import exceptions.ValidationException;
import model.*;
import service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class SmartPosFrame extends JFrame {
    private final InventoryService inventoryService = new InventoryService();
    private final CustomerService customerService = new CustomerService();
    private final UserService userService = new UserService();
    private final CartService cartService = new CartService();
    private final SalesService salesService = new SalesService(inventoryService);
    private final ReportService reportService = new ReportService(salesService, inventoryService);
    private final ReceiptPrinter receiptPrinter = new ReceiptPrinter();
    private final ReceiptExporter receiptExporter = new ReceiptExporter();

    private final CardLayout rootLayout = new CardLayout();
    private final JPanel root = new JPanel(rootLayout);
    private final CardLayout appLayout = new CardLayout();
    private final JPanel content = new JPanel(appLayout);
    private final JPanel nav = new JPanel();

    private Color background = new Color(244, 247, 250);
    private Color surface = Color.WHITE;
    private Color sidebar = new Color(24, 25, 30);
    private Color text = new Color(33, 37, 41);
    private Color muted = new Color(103, 112, 124);
    private Color accent = new Color(42, 127, 98);
    private Color danger = new Color(188, 70, 70);
    private Color border = new Color(224, 230, 236);
    private boolean darkTheme;

    private User currentUser;
    private Sale lastSale;

    private final DefaultTableModel salesProductModel = tableModel("ID", "Name", "Barcode", "Category", "Price", "Stock");
    private final DefaultTableModel cartModel = tableModel("ID", "Product", "Qty", "Unit", "Discount", "Total");
    private final DefaultTableModel productAdminModel = tableModel("ID", "Name", "Category", "Barcode", "Cost", "Price", "Qty", "Reorder", "Status");
    private final DefaultTableModel customerModel = tableModel("ID", "Name", "Phone", "Email", "Points");
    private final DefaultTableModel userModel = tableModel("Username", "Full Name", "Role", "Active");
    private final DefaultTableModel inventoryModel = tableModel("ID", "Product", "Stock", "Reorder", "Status");
    private final DefaultTableModel stockHistoryModel = tableModel("Time", "ID", "Product", "Change", "After", "Reason", "User");
    private final DefaultTableModel salesHistoryModel = tableModel("Receipt", "Date", "Customer", "Cashier", "Payment", "Total");
    private final DefaultTableModel reportBestModel = tableModel("ID", "Product", "Qty Sold", "Revenue");
    private final DefaultTableModel reportLeastModel = tableModel("ID", "Product", "Qty Sold", "Revenue");
    private final DefaultTableModel reportLowStockModel = tableModel("ID", "Product", "Stock", "Reorder");

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel userBadge;
    private JLabel dashProducts;
    private JLabel dashCustomers;
    private JLabel dashSalesToday;
    private JLabel dashRevenue;
    private JLabel dashLowStock;
    private JLabel dashTransactions;
    private JTextField saleSearchField;
    private JTable salesProductTable;
    private JTable cartTable;
    private JComboBox<Customer> saleCustomerCombo;
    private JSpinner saleQuantitySpinner;
    private JComboBox<String> paymentMethodCombo;
    private JTextField amountPaidField;
    private JTextField paymentReferenceField;
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel vatLabel;
    private JLabel totalLabel;
    private JTextArea receiptArea;
    private JTable productAdminTable;
    private JTextField productSearchField;
    private JTextField productIdField;
    private JTextField productNameField;
    private JTextField productCategoryField;
    private JTextField productBarcodeField;
    private JTextField productCostField;
    private JTextField productPriceField;
    private JSpinner productQuantitySpinner;
    private JSpinner productReorderSpinner;
    private JComboBox<String> productTypeCombo;
    private JCheckBox productTaxableCheck;
    private JTable customerTable;
    private JTextField customerSearchField;
    private JTextField customerIdField;
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JTextField customerEmailField;
    private JSpinner customerPointsSpinner;
    private JTable userTable;
    private JTextField userSearchField;
    private JTextField managedUsernameField;
    private JTextField managedFullNameField;
    private JTextField managedPasswordField;
    private JComboBox<UserRole> managedRoleCombo;
    private JCheckBox managedActiveCheck;
    private JTable inventoryTable;
    private JTable stockHistoryTable;
    private JTable salesHistoryTable;
    private JTextField salesHistorySearchField;
    private JTextArea salesHistoryReceiptArea;
    private JTextArea reportSummaryArea;
    private JTable reportBestTable;
    private JTable reportLeastTable;
    private JTable reportLowStockTable;

    public SmartPosFrame() {
        super("Bright Saah POS Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 820));
        setSize(1380, 880);
        setLocationRelativeTo(null);
        root.add(createLoginScreen(), "login");
        add(root);
        rootLayout.show(root, "login");
    }

    private JPanel createLoginScreen() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(new Color(26, 29, 36));
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(430, 380));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(28, 28, 28, 28));

        JLabel title = new JLabel("Bright Saah POS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(new Color(36, 39, 46));
        JLabel subtitle = new JLabel("Login to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 108, 118));

        usernameField = field("Username");
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(inputBorder());
        JButton loginButton = primaryButton("Login");
        loginButton.addActionListener(event -> login());
        passwordField.addActionListener(event -> login());

        // JTextArea hint = new JTextArea("Demo accounts:\nadmin / admin123\ncashier / cash123");
        // hint.setEditable(false);
        // hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // hint.setForeground(new Color(100, 108, 118));
        // hint.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(title, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        card.add(subtitle, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(label("Username"), gbc);
        gbc.gridy = 3;
        card.add(usernameField, gbc);
        gbc.gridy = 4;
        card.add(label("Password"), gbc);
        gbc.gridy = 5;
        card.add(passwordField, gbc);
        gbc.gridy = 6;
        gbc.insets = new Insets(16, 0, 16, 0);
        card.add(loginButton, gbc);
        gbc.gridy = 7;
//        card.add(hint, gbc);

        page.add(card);
        return page;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        userService.authenticate(username, password).ifPresentOrElse(user -> {
            currentUser = user;
            buildApp();
            refreshAll();
            rootLayout.show(root, "app");
        }, () -> showError("Invalid username, password, or inactive account."));
    }

    private void buildApp() {
        root.removeAll();
        JPanel app = new JPanel(new BorderLayout());
        app.setBackground(background);
        app.add(createNavigation(), BorderLayout.WEST);
        content.removeAll();
        content.setBackground(background);
        content.add(createDashboardScreen(), "Dashboard");
        content.add(createSalesScreen(), "Sales");
        content.add(createProductScreen(), "Products");
        content.add(createCustomerScreen(), "Customers");
        content.add(createInventoryScreen(), "Inventory");
        content.add(createSalesHistoryScreen(), "Sales History");
        content.add(createReportsScreen(), "Reports");
        content.add(createUserScreen(), "Users");
        content.add(createAboutScreen(), "About");
        app.add(content, BorderLayout.CENTER);
        root.add(app, "app");
        appLayout.show(content, "Dashboard");
        revalidate();
        repaint();
    }

    private JPanel createNavigation() {
        nav.removeAll();
        nav.setPreferredSize(new Dimension(230, 820));
        nav.setBackground(sidebar);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(24, 18, 18, 18));

        JLabel brand = new JLabel("Bright Saah POS");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 25));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Retail Management");
        subtitle.setForeground(new Color(190, 196, 204));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        userBadge = new JLabel();
        userBadge.setForeground(new Color(221, 226, 232));
        userBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        nav.add(brand);
        nav.add(subtitle);
        nav.add(Box.createVerticalStrut(14));
        nav.add(userBadge);
        nav.add(Box.createVerticalStrut(24));
        nav.add(navButton("Dashboard", "Dashboard"));
        nav.add(navButton("Sales", "Sales"));
        if (isAdmin()) {
            nav.add(navButton("Products", "Products"));
            nav.add(navButton("Customers", "Customers"));
        }
        nav.add(navButton("Inventory", "Inventory"));
        nav.add(navButton("Sales History", "Sales History"));
        if (isAdmin()) {
            nav.add(navButton("Reports", "Reports"));
            nav.add(navButton("Users", "Users"));
        }
        nav.add(navButton("About", "About"));
        nav.add(Box.createVerticalGlue());
        JButton themeButton = navActionButton("Toggle Theme");
        themeButton.addActionListener(event -> toggleTheme());
        JButton logoutButton = navActionButton("Logout");
        logoutButton.addActionListener(event -> logout());
        nav.add(themeButton);
        nav.add(Box.createVerticalStrut(8));
        nav.add(logoutButton);
        return nav;
    }

    private JButton navButton(String textValue, String card) {
        JButton button = navActionButton(textValue);
        button.addActionListener(event -> {
            refreshAll();
            appLayout.show(content, card);
        });
        return button;
    }

    private JButton navActionButton(String textValue) {
        JButton button = new JButton(textValue);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(new Color(39, 42, 50));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return button;
    }

    private JPanel createDashboardScreen() {
        JPanel page = page("Dashboard");
        JPanel cards = new JPanel(new GridLayout(2, 3, 14, 14));
        cards.setOpaque(false);
        dashProducts = valueLabel();
        dashCustomers = valueLabel();
        dashSalesToday = valueLabel();
        dashRevenue = valueLabel();
        dashLowStock = valueLabel();
        dashTransactions = valueLabel();
        cards.add(statCard("Products", dashProducts));
        cards.add(statCard("Customers", dashCustomers));
        cards.add(statCard("Sales Today", dashSalesToday));
        cards.add(statCard("Revenue", dashRevenue));
        cards.add(statCard("Low Stock", dashLowStock));
        cards.add(statCard("Transactions", dashTransactions));
        page.add(cards, BorderLayout.NORTH);

        JTextArea welcome = new JTextArea();
        welcome.setEditable(false);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcome.setBackground(surface);
        welcome.setForeground(text);
        welcome.setBorder(new EmptyBorder(18, 18, 18, 18));
        welcome.setText("""
                Bright Saah POS Management System
                
                Use the navigation menu to manage products, customers, users, inventory, sales, receipts, and reports.
                Role-based access is active: administrators can manage the system, while cashiers focus on selling and viewing their own sales.
                """);
        page.add(wrap(welcome), BorderLayout.CENTER);
        return page;
    }

    private JPanel createSalesScreen() {
        JPanel page = page("Sales");
        JPanel split = new JPanel(new GridBagLayout());
        split.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 12);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.55;
        gbc.weighty = 1;
        split.add(createSalesProductPanel(), gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 0.45;
        split.add(createCartPanel(), gbc);
        page.add(split, BorderLayout.CENTER);
        return page;
    }

    private JPanel createSalesProductPanel() {
        JPanel panel = panel(new BorderLayout(10, 10));
        panel.add(sectionHeader("Product Search", "Search as you type by ID, barcode, name, or category."), BorderLayout.NORTH);
        JPanel tools = new JPanel(new GridBagLayout());
        tools.setOpaque(false);
        saleSearchField = field("Search products");
        saleSearchField.getDocument().addDocumentListener(doc(this::refreshSalesProductTable));
        saleQuantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JButton add = primaryButton("Add");
        add.addActionListener(event -> addToCart());
        saleSearchField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "addToCart");
        saleSearchField.getActionMap().put("addToCart", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                addToCart();
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 8);
        tools.add(saleSearchField, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        tools.add(saleQuantitySpinner, gbc);
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        tools.add(add, gbc);
        salesProductTable = table(salesProductModel);
        salesProductTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(tools, BorderLayout.SOUTH);
        panel.add(new JScrollPane(salesProductTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = panel(new BorderLayout(10, 10));
        panel.add(sectionHeader("Cart and Payment", "Complete sale, preview receipt, and export receipt."), BorderLayout.NORTH);
        cartTable = table(cartModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridBagLayout());
        bottom.setOpaque(false);
        saleCustomerCombo = new JComboBox<>();
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Card", "Mobile Money"});
        paymentMethodCombo.addActionListener(event -> updatePaymentDefaults());
        amountPaidField = field("Amount paid");
        paymentReferenceField = field("Payment reference");
        subtotalLabel = valueLabel();
        discountLabel = valueLabel();
        vatLabel = valueLabel();
        totalLabel = new JLabel("$0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        totalLabel.setForeground(accent);
        JButton remove = secondaryButton("Remove");
        remove.addActionListener(event -> removeCartItem());
        JButton updateQty = secondaryButton("Update Qty");
        updateQty.addActionListener(event -> updateCartQuantity());
        JButton clear = dangerButton("Cancel Sale");
        clear.addActionListener(event -> {
            cartService.clear();
            refreshAll();
        });
        JButton checkout = primaryButton("Checkout");
        checkout.addActionListener(event -> checkout());
        JButton print = secondaryButton("Print");
        print.addActionListener(event -> printReceipt());
        JButton exportTxt = secondaryButton("Export TXT");
        exportTxt.addActionListener(event -> exportReceipt(false));
        JButton exportPdf = secondaryButton("Export PDF");
        exportPdf.addActionListener(event -> exportReceipt(true));
        receiptArea = new JTextArea(9, 30);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        receiptArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 0, 3, 8);
        gbc.weightx = 1;
        bottom.add(label("Customer"), gbc);
        gbc.gridx = 1;
        bottom.add(saleCustomerCombo, gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        bottom.add(label("Payment"), gbc);
        gbc.gridx = 1;
        bottom.add(paymentMethodCombo, gbc);
        gbc.gridy = 2;
        gbc.gridx = 0;
        bottom.add(label("Amount Paid"), gbc);
        gbc.gridx = 1;
        bottom.add(amountPaidField, gbc);
        gbc.gridy = 3;
        gbc.gridx = 0;
        bottom.add(label("Reference"), gbc);
        gbc.gridx = 1;
        bottom.add(paymentReferenceField, gbc);
        gbc.gridy = 4;
        gbc.gridx = 0;
        bottom.add(label("Subtotal"), gbc);
        gbc.gridx = 1;
        bottom.add(subtotalLabel, gbc);
        gbc.gridy = 5;
        gbc.gridx = 0;
        bottom.add(label("Discount"), gbc);
        gbc.gridx = 1;
        bottom.add(discountLabel, gbc);
        gbc.gridy = 6;
        gbc.gridx = 0;
        bottom.add(label("VAT"), gbc);
        gbc.gridx = 1;
        bottom.add(vatLabel, gbc);
        gbc.gridy = 7;
        gbc.gridx = 0;
        bottom.add(label("Total"), gbc);
        gbc.gridx = 1;
        bottom.add(totalLabel, gbc);

        JPanel cartActions = new JPanel(new GridLayout(2, 4, 8, 8));
        cartActions.setOpaque(false);
        cartActions.add(remove);
        cartActions.add(updateQty);
        cartActions.add(clear);
        cartActions.add(checkout);
        cartActions.add(print);
        cartActions.add(exportTxt);
        cartActions.add(exportPdf);
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        bottom.add(cartActions, gbc);
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        bottom.add(new JScrollPane(receiptArea), gbc);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createProductScreen() {
        JPanel page = page("Product Management");
        JPanel panel = panel(new BorderLayout(10, 10));
        productSearchField = field("Search products");
        productSearchField.getDocument().addDocumentListener(doc(this::refreshProductAdminTable));
        productAdminTable = table(productAdminModel);
        productAdminTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productAdminTable.getSelectionModel().addListSelectionListener(event -> loadSelectedProduct());
        panel.add(productSearchField, BorderLayout.NORTH);
        panel.add(new JScrollPane(productAdminTable), BorderLayout.CENTER);
        panel.add(productForm(), BorderLayout.SOUTH);
        page.add(panel, BorderLayout.CENTER);
        return page;
    }

    private JPanel productForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        productIdField = field("Product ID");
        productNameField = field("Name");
        productCategoryField = field("Category");
        productBarcodeField = field("Barcode");
        productCostField = field("Cost");
        productPriceField = field("Selling");
        productQuantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));
        productReorderSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 100000, 1));
        productTypeCombo = new JComboBox<>(new String[]{"Retail", "Weighted"});
        productTaxableCheck = new JCheckBox("Taxable");
        productTaxableCheck.setOpaque(false);
        JButton add = primaryButton("Add Product");
        add.addActionListener(event -> addProduct());
        JButton update = secondaryButton("Edit Product");
        update.addActionListener(event -> updateProduct());
        JButton delete = dangerButton("Delete Product");
        delete.addActionListener(event -> deleteProduct());
        JButton clear = secondaryButton("Clear");
        clear.addActionListener(event -> clearProductForm());
        Component[] fields = {productIdField, productNameField, productCategoryField, productBarcodeField,
                productCostField, productPriceField, productQuantitySpinner, productReorderSpinner,
                productTypeCombo, productTaxableCheck, add, update, delete, clear};
        for (int i = 0; i < fields.length; i++) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = i % 4;
            gbc.gridy = i / 4;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 0, 4, 8);
            form.add(fields[i], gbc);
        }
        return form;
    }

    private JPanel createCustomerScreen() {
        JPanel page = page("Customer Management");
        JPanel panel = panel(new BorderLayout(10, 10));
        customerSearchField = field("Search customers");
        customerSearchField.getDocument().addDocumentListener(doc(this::refreshCustomers));
        customerTable = table(customerModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.getSelectionModel().addListSelectionListener(event -> loadSelectedCustomer());
        panel.add(customerSearchField, BorderLayout.NORTH);
        panel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        panel.add(customerForm(), BorderLayout.SOUTH);
        page.add(panel, BorderLayout.CENTER);
        return page;
    }

    private JPanel customerForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        customerIdField = field("Customer ID");
        customerNameField = field("Name");
        customerPhoneField = field("Phone");
        customerEmailField = field("Email");
        customerPointsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));
        JButton add = primaryButton("Add");
        add.addActionListener(event -> addCustomer());
        JButton update = secondaryButton("Edit");
        update.addActionListener(event -> updateCustomer());
        JButton delete = dangerButton("Delete");
        delete.addActionListener(event -> deleteCustomer());
        Component[] fields = {customerIdField, customerNameField, customerPhoneField, customerEmailField,
                customerPointsSpinner, add, update, delete};
        for (int i = 0; i < fields.length; i++) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = i % 4;
            gbc.gridy = i / 4;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 0, 4, 8);
            form.add(fields[i], gbc);
        }
        return form;
    }

    private JPanel createInventoryScreen() {
        JPanel page = page("Inventory Management");
        JTabbedPane tabs = new JTabbedPane();
        JPanel stockPanel = panel(new BorderLayout(10, 10));
        inventoryTable = table(inventoryModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stockPanel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
        JButton increase = primaryButton("Increase Stock");
        increase.addActionListener(event -> adjustStock(stockSpinner, true));
        JButton reduce = dangerButton("Reduce Stock");
        reduce.addActionListener(event -> adjustStock(stockSpinner, false));
        JPanel tools = new JPanel(new GridLayout(1, 3, 8, 0));
        tools.setOpaque(false);
        tools.add(stockSpinner);
        tools.add(increase);
        tools.add(reduce);
        stockPanel.add(tools, BorderLayout.SOUTH);
        stockHistoryTable = table(stockHistoryModel);
        tabs.addTab("Current Stock", stockPanel);
        tabs.addTab("Stock History", new JScrollPane(stockHistoryTable));
        page.add(tabs, BorderLayout.CENTER);
        return page;
    }

    private JPanel createSalesHistoryScreen() {
        JPanel page = page("Sales History");
        JPanel panel = panel(new BorderLayout(10, 10));
        salesHistorySearchField = field("Search by receipt, customer, cashier, or date");
        salesHistorySearchField.getDocument().addDocumentListener(doc(this::refreshSalesHistory));
        salesHistoryTable = table(salesHistoryModel);
        salesHistoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesHistoryTable.getSelectionModel().addListSelectionListener(event -> loadSelectedSaleReceipt());
        salesHistoryReceiptArea = new JTextArea();
        salesHistoryReceiptArea.setEditable(false);
        salesHistoryReceiptArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JPanel split = new JPanel(new GridLayout(1, 2, 12, 0));
        split.setOpaque(false);
        split.add(new JScrollPane(salesHistoryTable));
        split.add(new JScrollPane(salesHistoryReceiptArea));
        panel.add(salesHistorySearchField, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        page.add(panel, BorderLayout.CENTER);
        return page;
    }

    private JPanel createReportsScreen() {
        JPanel page = page("Reports");
        JPanel panel = panel(new BorderLayout(10, 10));
        reportSummaryArea = new JTextArea();
        reportSummaryArea.setEditable(false);
        reportSummaryArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        reportBestTable = table(reportBestModel);
        reportLeastTable = table(reportLeastModel);
        reportLowStockTable = table(reportLowStockModel);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Summary", new JScrollPane(reportSummaryArea));
        tabs.addTab("Best Selling", new JScrollPane(reportBestTable));
        tabs.addTab("Least Selling", new JScrollPane(reportLeastTable));
        tabs.addTab("Low Stock", new JScrollPane(reportLowStockTable));
        JButton export = primaryButton("Export Report CSV");
        export.addActionListener(event -> exportReportCsv());
        panel.add(tabs, BorderLayout.CENTER);
        panel.add(export, BorderLayout.SOUTH);
        page.add(panel, BorderLayout.CENTER);
        return page;
    }

    private JPanel createUserScreen() {
        JPanel page = page("User Management");
        JPanel panel = panel(new BorderLayout(10, 10));
        userSearchField = field("Search users");
        userSearchField.getDocument().addDocumentListener(doc(this::refreshUsers));
        userTable = table(userModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(event -> loadSelectedUser());
        panel.add(userSearchField, BorderLayout.NORTH);
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        panel.add(userForm(), BorderLayout.SOUTH);
        page.add(panel, BorderLayout.CENTER);
        return page;
    }

    private JPanel userForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        managedUsernameField = field("Username");
        managedFullNameField = field("Full Name");
        managedPasswordField = field("Password");
        managedRoleCombo = new JComboBox<>(UserRole.values());
        managedActiveCheck = new JCheckBox("Active", true);
        managedActiveCheck.setOpaque(false);
        JButton add = primaryButton("Add User");
        add.addActionListener(event -> addUser());
        JButton update = secondaryButton("Edit User");
        update.addActionListener(event -> updateUser());
        JButton delete = dangerButton("Delete User");
        delete.addActionListener(event -> deleteUser());
        Component[] fields = {managedUsernameField, managedFullNameField, managedPasswordField, managedRoleCombo,
                managedActiveCheck, add, update, delete};
        for (int i = 0; i < fields.length; i++) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = i % 4;
            gbc.gridy = i / 4;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 0, 4, 8);
            form.add(fields[i], gbc);
        }
        return form;
    }

    private JPanel createAboutScreen() {
        JPanel page = page("About");
        JTextArea about = new JTextArea("""
                Bright Saah Point of Sale (POS) Management System
                Version: 1.0
                
                Developer: Bright Saah
                
                Features:
                Login and logout, role-based admin/cashier access, product CRUD, customer CRUD, user management,
                inventory updates, stock history, low-stock alerts, sales cart, receipt generation, receipt export,
                sales history, searchable records, dashboard statistics, reports, and dark/light theme toggle.
                """);
        about.setEditable(false);
        about.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        about.setForeground(text);
        about.setBackground(surface);
        about.setBorder(new EmptyBorder(20, 20, 20, 20));
        page.add(wrap(about), BorderLayout.CENTER);
        return page;
    }

    private void addToCart() {
        int row = salesProductTable.getSelectedRow();
        if (row < 0) {
            if (salesProductTable.getRowCount() == 1) {
                row = 0;
            } else {
                showError("Select a product to add.");
                return;
            }
        }
        InventoryItem item = inventoryService.findBySku(salesProductTable.getValueAt(row, 0).toString()).orElse(null);
        try {
            cartService.addProduct(item, (Integer) saleQuantitySpinner.getValue(), selectedCustomer());
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void removeCartItem() {
        int row = cartTable.getSelectedRow();
        if (row >= 0) {
            cartService.removeProduct(cartTable.getValueAt(row, 0).toString());
            refreshAll();
        }
    }

    private void updateCartQuantity() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            showError("Select a cart item first.");
            return;
        }
        String sku = cartTable.getValueAt(row, 0).toString();
        cartService.removeProduct(sku);
        InventoryItem item = inventoryService.findBySku(sku).orElse(null);
        try {
            cartService.addProduct(item, (Integer) saleQuantitySpinner.getValue(), selectedCustomer());
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void checkout() {
        try {
            Payment payment = payment();
            Sale sale = salesService.checkout(cartService, selectedCustomer(), currentUser, payment);
            lastSale = sale;
            receiptArea.setText(receiptPrinter.print(sale));
            refreshAll();
            JOptionPane.showMessageDialog(this, "Sale completed: " + sale.getReceiptNumber());
        } catch (PosException ex) {
            showError(ex.getMessage());
        }
    }

    private void exportReceipt(boolean pdf) {
        if (lastSale == null || receiptArea.getText().isBlank()) {
            showError("Complete a sale before exporting a receipt.");
            return;
        }
        try {
            Path dir = Path.of("receipts");
            Path file = pdf
                    ? receiptExporter.exportSimplePdf(receiptArea.getText(), dir, lastSale.getReceiptNumber())
                    : receiptExporter.exportTextReceipt(receiptArea.getText(), dir, lastSale.getReceiptNumber());
            JOptionPane.showMessageDialog(this, "Receipt exported to " + file.toAbsolutePath());
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void printReceipt() {
        if (receiptArea.getText().isBlank()) {
            showError("Complete a sale before printing a receipt.");
            return;
        }
        try {
            receiptArea.print();
        } catch (java.awt.print.PrinterException ex) {
            showError("Printing failed: " + ex.getMessage());
        }
    }

    private void addProduct() {
        try {
            Product product = productFromForm();
            inventoryService.addProduct(product, (Integer) productQuantitySpinner.getValue(), (Integer) productReorderSpinner.getValue());
            clearProductForm();
            refreshAll();
        } catch (ValidationException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateProduct() {
        try {
            inventoryService.updateProduct(productIdField.getText().trim(), productNameField.getText(), productCategoryField.getText(),
                    productBarcodeField.getText(), moneyValue(productCostField), moneyValue(productPriceField),
                    productTaxableCheck.isSelected(), (Integer) productQuantitySpinner.getValue(), (Integer) productReorderSpinner.getValue());
            refreshAll();
        } catch (ValidationException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteProduct() {
        if (JOptionPane.showConfirmDialog(this, "Delete selected product?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            inventoryService.deleteProduct(productIdField.getText().trim());
            clearProductForm();
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private Product productFromForm() throws ValidationException {
        String sku = productIdField.getText().trim();
        String type = productTypeCombo.getSelectedItem().toString();
        if (sku.isBlank()) {
            throw new ValidationException("Product ID is required.");
        }
        if ("Weighted".equals(type)) {
            return new WeightedProduct(sku, productNameField.getText(), productCategoryField.getText(), productBarcodeField.getText(),
                    moneyValue(productCostField), moneyValue(productPriceField), productTaxableCheck.isSelected(), "kg");
        }
        return new RetailProduct(sku, productNameField.getText(), productCategoryField.getText(), productBarcodeField.getText(),
                moneyValue(productCostField), moneyValue(productPriceField), productTaxableCheck.isSelected(), "Store Brand");
    }

    private void addCustomer() {
        try {
            customerService.addCustomer(customerFromForm());
            clearCustomerForm();
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateCustomer() {
        try {
            customerService.updateCustomer(customerFromForm());
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteCustomer() {
        if (JOptionPane.showConfirmDialog(this, "Delete selected customer?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            customerService.deleteCustomer(customerIdField.getText().trim());
            clearCustomerForm();
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private Customer customerFromForm() {
        return new Customer(customerIdField.getText(), customerNameField.getText(), customerPhoneField.getText(),
                customerEmailField.getText(), (Integer) customerPointsSpinner.getValue());
    }

    private void adjustStock(JSpinner spinner, boolean increase) {
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            showError("Select an inventory product first.");
            return;
        }
        String sku = inventoryTable.getValueAt(row, 0).toString();
        try {
            if (increase) {
                inventoryService.restock(sku, (Integer) spinner.getValue(), currentUser);
            } else {
                inventoryService.manualReduceStock(sku, (Integer) spinner.getValue(), currentUser);
            }
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void addUser() {
        try {
            userService.addUser(managedUsernameField.getText().trim(), managedFullNameField.getText(),
                    managedPasswordField.getText(), (UserRole) managedRoleCombo.getSelectedItem(), managedActiveCheck.isSelected());
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateUser() {
        try {
            userService.updateUser(managedUsernameField.getText().trim(), managedFullNameField.getText(),
                    managedPasswordField.getText(), (UserRole) managedRoleCombo.getSelectedItem(), managedActiveCheck.isSelected());
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteUser() {
        if (JOptionPane.showConfirmDialog(this, "Delete selected user?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            userService.deleteUser(managedUsernameField.getText().trim());
            refreshAll();
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void exportReportCsv() {
        try {
            Path dir = Path.of("reports");
            java.nio.file.Files.createDirectories(dir);
            Path file = dir.resolve("sales-report-" + LocalDate.now() + ".csv");
            StringBuilder csv = new StringBuilder("Metric,Value\n");
            csv.append("Daily Sales,").append(reportService.revenueToday()).append("\n");
            csv.append("Weekly Sales,").append(reportService.revenueThisWeek()).append("\n");
            csv.append("Monthly Sales,").append(reportService.revenueThisMonth()).append("\n");
            csv.append("Revenue,").append(salesService.getRevenue()).append("\n");
            csv.append("Transactions,").append(salesService.getTransactionsCount()).append("\n");
            java.nio.file.Files.writeString(file, csv);
            JOptionPane.showMessageDialog(this, "Report exported to " + file.toAbsolutePath());
        } catch (Exception ex) {
            showError("Report export failed: " + ex.getMessage());
        }
    }

    private void refreshAll() {
        if (currentUser == null) {
            return;
        }
        if (userBadge != null) {
            userBadge.setText(currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        }
        refreshDashboard();
        refreshSalesProductTable();
        refreshCart();
        refreshProductAdminTable();
        refreshCustomers();
        refreshUsers();
        refreshInventory();
        refreshSalesHistory();
        refreshReports();
        updatePaymentDefaults();
    }

    private void refreshDashboard() {
        if (dashProducts == null) {
            return;
        }
        dashProducts.setText(String.valueOf(inventoryService.countProducts()));
        dashCustomers.setText(String.valueOf(customerService.countCustomers()));
        dashSalesToday.setText(money(reportService.revenueToday()));
        dashRevenue.setText(money(salesService.getRevenue()));
        dashLowStock.setText(String.valueOf(inventoryService.countLowStockItems()));
        dashTransactions.setText(String.valueOf(salesService.getTransactionsCount()));
    }

    private void refreshSalesProductTable() {
        if (salesProductModel == null) {
            return;
        }
        salesProductModel.setRowCount(0);
        String query = saleSearchField == null ? "" : saleSearchField.getText();
        for (InventoryItem item : inventoryService.search(query)) {
            salesProductModel.addRow(new Object[]{item.getProduct().getSku(), item.getProduct().getName(),
                    item.getProduct().getBarcode(), item.getProduct().getCategory(), money(item.getProduct().getPrice()), item.getStockQuantity()});
        }
    }

    private void refreshCart() {
        if (cartModel == null) {
            return;
        }
        cartService.refreshDiscounts(selectedCustomer());
        cartModel.setRowCount(0);
        for (CartItem item : cartService.getItems()) {
            cartModel.addRow(new Object[]{item.getProduct().getSku(), item.getProduct().getName(), item.getQuantity(),
                    money(item.getProduct().getPrice()), item.getDiscountRate().multiply(new BigDecimal("100")) + "%", money(item.getNetAmount())});
        }
        if (subtotalLabel != null) {
            subtotalLabel.setText(money(cartService.getSubtotal()));
            discountLabel.setText("-" + money(cartService.getDiscountTotal()));
            vatLabel.setText(money(cartService.getTaxTotal()));
            totalLabel.setText(money(cartService.getTotal()));
        }
    }

    private void refreshProductAdminTable() {
        if (productAdminModel == null) {
            return;
        }
        productAdminModel.setRowCount(0);
        String query = productSearchField == null ? "" : productSearchField.getText();
        for (InventoryItem item : inventoryService.search(query)) {
            productAdminModel.addRow(new Object[]{item.getProduct().getSku(), item.getProduct().getName(), item.getProduct().getCategory(),
                    item.getProduct().getBarcode(), money(item.getProduct().getCostPrice()), money(item.getProduct().getPrice()),
                    item.getStockQuantity(), item.getReorderLevel(), item.isLowStock() ? "Low" : "Healthy"});
        }
    }

    private void refreshCustomers() {
        if (customerModel == null) {
            return;
        }
        String query = customerSearchField == null ? "" : customerSearchField.getText();
        customerModel.setRowCount(0);
        List<Customer> customers = customerService.search(query);
        for (Customer customer : customers) {
            customerModel.addRow(new Object[]{customer.getCustomerId(), customer.getName(), customer.getPhone(), customer.getEmail(), customer.getLoyaltyPoints()});
        }
        if (saleCustomerCombo != null) {
            Customer selected = (Customer) saleCustomerCombo.getSelectedItem();
            saleCustomerCombo.setModel(new DefaultComboBoxModel<>(customers.toArray(Customer[]::new)));
            if (selected != null) {
                saleCustomerCombo.setSelectedItem(selected);
            }
        }
    }

    private void refreshUsers() {
        if (userModel == null) {
            return;
        }
        userModel.setRowCount(0);
        String query = userSearchField == null ? "" : userSearchField.getText();
        for (User user : userService.search(query)) {
            userModel.addRow(new Object[]{user.getUsername(), user.getFullName(), user.getRole(), user.isActive() ? "Yes" : "No"});
        }
    }

    private void refreshInventory() {
        if (inventoryModel == null) {
            return;
        }
        inventoryModel.setRowCount(0);
        for (InventoryItem item : inventoryService.getAllItems()) {
            String status = item.getStockQuantity() == 0 ? "Out of stock" : item.isLowStock() ? "Low stock" : "Healthy";
            inventoryModel.addRow(new Object[]{item.getProduct().getSku(), item.getProduct().getName(), item.getStockQuantity(), item.getReorderLevel(), status});
        }
        stockHistoryModel.setRowCount(0);
        for (StockHistoryEntry entry : inventoryService.getStockHistory()) {
            stockHistoryModel.addRow(new Object[]{entry.getChangedAt().toString().replace('T', ' '), entry.getSku(), entry.getProductName(),
                    entry.getQuantityChange(), entry.getStockAfter(), entry.getReason(), entry.getUsername()});
        }
    }

    private void refreshSalesHistory() {
        if (salesHistoryModel == null) {
            return;
        }
        salesHistoryModel.setRowCount(0);
        String query = salesHistorySearchField == null ? "" : salesHistorySearchField.getText().toLowerCase();
        for (Sale sale : salesService.getSales()) {
            if (!isAdmin() && !sale.getCashier().getUsername().equals(currentUser.getUsername())) {
                continue;
            }
            String haystack = (sale.getReceiptNumber() + sale.getSoldAt() + sale.getCustomer().getName() + sale.getCashier().getFullName()).toLowerCase();
            if (query.isBlank() || haystack.contains(query)) {
                salesHistoryModel.addRow(new Object[]{sale.getReceiptNumber(), sale.getSoldAt().toString().replace('T', ' '),
                        sale.getCustomer().getName(), sale.getCashier().getFullName(), sale.getPayment().getMethodName(), money(sale.getTotal())});
            }
        }
    }

    private void refreshReports() {
        if (reportSummaryArea == null) {
            return;
        }
        reportSummaryArea.setText(reportService.summary());
        reportBestModel.setRowCount(0);
        for (ReportService.ProductSales productSales : reportService.productSalesRanked(true)) {
            reportBestModel.addRow(new Object[]{productSales.sku(), productSales.productName(), productSales.quantitySold(), money(productSales.revenue())});
        }
        reportLeastModel.setRowCount(0);
        for (ReportService.ProductSales productSales : reportService.productSalesRanked(false)) {
            reportLeastModel.addRow(new Object[]{productSales.sku(), productSales.productName(), productSales.quantitySold(), money(productSales.revenue())});
        }
        reportLowStockModel.setRowCount(0);
        for (InventoryItem item : reportService.lowStockProducts()) {
            reportLowStockModel.addRow(new Object[]{item.getProduct().getSku(), item.getProduct().getName(), item.getStockQuantity(), item.getReorderLevel()});
        }
    }

    private void loadSelectedProduct() {
        if (productAdminTable == null || productAdminTable.getSelectedRow() < 0) {
            return;
        }
        String sku = productAdminTable.getValueAt(productAdminTable.getSelectedRow(), 0).toString();
        inventoryService.findBySku(sku).ifPresent(item -> {
            productIdField.setText(item.getProduct().getSku());
            productNameField.setText(item.getProduct().getName());
            productCategoryField.setText(item.getProduct().getCategory());
            productBarcodeField.setText(item.getProduct().getBarcode());
            productCostField.setText(item.getProduct().getCostPrice().toPlainString());
            productPriceField.setText(item.getProduct().getPrice().toPlainString());
            productQuantitySpinner.setValue(item.getStockQuantity());
            productReorderSpinner.setValue(item.getReorderLevel());
            productTaxableCheck.setSelected(item.getProduct().isTaxable());
            productTypeCombo.setSelectedItem(item.getProduct() instanceof WeightedProduct ? "Weighted" : "Retail");
        });
    }

    private void loadSelectedCustomer() {
        if (customerTable == null || customerTable.getSelectedRow() < 0) {
            return;
        }
        customerService.findById(customerTable.getValueAt(customerTable.getSelectedRow(), 0).toString()).ifPresent(customer -> {
            customerIdField.setText(customer.getCustomerId());
            customerNameField.setText(customer.getName());
            customerPhoneField.setText(customer.getPhone());
            customerEmailField.setText(customer.getEmail());
            customerPointsSpinner.setValue(customer.getLoyaltyPoints());
        });
    }

    private void loadSelectedUser() {
        if (userTable == null || userTable.getSelectedRow() < 0) {
            return;
        }
        managedUsernameField.setText(userTable.getValueAt(userTable.getSelectedRow(), 0).toString());
        managedFullNameField.setText(userTable.getValueAt(userTable.getSelectedRow(), 1).toString());
        managedRoleCombo.setSelectedItem(UserRole.valueOf(userTable.getValueAt(userTable.getSelectedRow(), 2).toString()));
        managedActiveCheck.setSelected("Yes".equals(userTable.getValueAt(userTable.getSelectedRow(), 3).toString()));
        managedPasswordField.setText("pass1234");
    }

    private void loadSelectedSaleReceipt() {
        if (salesHistoryTable == null || salesHistoryTable.getSelectedRow() < 0) {
            return;
        }
        String receipt = salesHistoryTable.getValueAt(salesHistoryTable.getSelectedRow(), 0).toString();
        for (Sale sale : salesService.getSales()) {
            if (sale.getReceiptNumber().equals(receipt)) {
                salesHistoryReceiptArea.setText(receiptPrinter.print(sale));
                return;
            }
        }
    }

    private Payment payment() throws ValidationException {
        BigDecimal amount = moneyValue(amountPaidField);
        String method = paymentMethodCombo.getSelectedItem().toString();
        String reference = paymentReferenceField.getText();
        return switch (method) {
            case "Card" -> new CardPayment(amount, reference);
            case "Mobile Money" -> new MobileMoneyPayment(amount, reference);
            default -> new CashPayment(amount);
        };
    }

    private Customer selectedCustomer() {
        if (saleCustomerCombo == null || saleCustomerCombo.getSelectedItem() == null) {
            return new Customer("Walk-in Customer", "", 0);
        }
        return (Customer) saleCustomerCombo.getSelectedItem();
    }

    private void updatePaymentDefaults() {
        if (amountPaidField == null) {
            return;
        }
        if (!amountPaidField.hasFocus()) {
            amountPaidField.setText(cartService.getTotal().toPlainString());
        }
        boolean cash = paymentMethodCombo == null || "Cash".equals(paymentMethodCombo.getSelectedItem());
        paymentReferenceField.setEnabled(!cash);
        if (cash) {
            paymentReferenceField.setText("");
        } else if (paymentReferenceField.getText().isBlank()) {
            paymentReferenceField.setText("AUTH1234");
        }
    }

    private void clearProductForm() {
        productIdField.setText("");
        productNameField.setText("");
        productCategoryField.setText("");
        productBarcodeField.setText("");
        productCostField.setText("");
        productPriceField.setText("");
        productQuantitySpinner.setValue(0);
        productReorderSpinner.setValue(5);
        productTaxableCheck.setSelected(false);
    }

    private void clearCustomerForm() {
        customerIdField.setText("");
        customerNameField.setText("");
        customerPhoneField.setText("");
        customerEmailField.setText("");
        customerPointsSpinner.setValue(0);
    }

    private void toggleTheme() {
        darkTheme = !darkTheme;
        if (darkTheme) {
            background = new Color(31, 34, 40);
            surface = new Color(42, 46, 54);
            text = new Color(236, 239, 244);
            muted = new Color(181, 188, 198);
            border = new Color(67, 73, 82);
        } else {
            background = new Color(244, 247, 250);
            surface = Color.WHITE;
            text = new Color(33, 37, 41);
            muted = new Color(103, 112, 124);
            border = new Color(224, 230, 236);
        }
        buildApp();
        refreshAll();
    }

    private void logout() {
        currentUser = null;
        cartService.clear();
        root.removeAll();
        root.add(createLoginScreen(), "login");
        rootLayout.show(root, "login");
        revalidate();
        repaint();
    }

    private boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == UserRole.ADMINISTRATOR;
    }

    private JPanel page(String title) {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setBackground(background);
        page.setBorder(new EmptyBorder(22, 22, 22, 22));
        JLabel heading = new JLabel(title);
        heading.setForeground(text);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 30));
        page.add(heading, BorderLayout.NORTH);
        return page;
    }

    private JPanel panel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(surface);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(border), new EmptyBorder(14, 14, 14, 14)));
        return panel;
    }

    private JPanel wrap(Component component) {
        JPanel panel = panel(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JPanel sectionHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(text);
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(muted);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel statCard(String title, JLabel value) {
        JPanel panel = panel(new BorderLayout());
        JLabel label = label(title);
        value.setForeground(accent);
        panel.add(label, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }

    private JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(text);
        table.setSelectionBackground(new Color(218, 239, 230));
        table.setGridColor(border);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        if (table.getColumnCount() > 3) {
            table.getColumnModel().getColumn(3).setCellRenderer(right);
        }
        return table;
    }

    private DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTextField field(String placeholder) {
        JTextField field = new JTextField();
        field.setToolTipText(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(inputBorder());
        return field;
    }

    private javax.swing.border.Border inputBorder() {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(border), new EmptyBorder(8, 10, 8, 10));
    }

    private JLabel label(String value) {
        JLabel label = new JLabel(value);
        label.setForeground(muted);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    private JLabel valueLabel() {
        JLabel label = new JLabel("0", SwingConstants.RIGHT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 23));
        label.setForeground(text);
        return label;
    }

    private JButton primaryButton(String value) {
        return button(value, accent, Color.WHITE);
    }

    private JButton secondaryButton(String value) {
        return button(value, new Color(232, 237, 242), new Color(32, 36, 42));
    }

    private JButton dangerButton(String value) {
        return button(value, danger, Color.WHITE);
    }

    private JButton button(String value, Color bg, Color fg) {
        JButton button = new JButton(value);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        return button;
    }

    private DocumentListener doc(Runnable runnable) {
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

    private BigDecimal moneyValue(JTextField field) throws ValidationException {
        try {
            BigDecimal value = new BigDecimal(field.getText().trim()).setScale(2, RoundingMode.HALF_UP);
            if (value.signum() < 0) {
                throw new ValidationException("Money values cannot be negative.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new ValidationException("Enter a valid amount.");
        }
    }

    private String money(BigDecimal value) {
        return "$" + value.setScale(2, RoundingMode.HALF_UP);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Bright Saah POS", JOptionPane.WARNING_MESSAGE);
    }
}
