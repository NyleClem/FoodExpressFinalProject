package foodexpress;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

// Main GUI class for the Food Express delivery application
// Provides dashboards for customers, restaurants, drivers, and admins
public class FoodExpressGUI extends JFrame {

    // Customer dashboard components
    private JTable vendorTable;
    private DefaultTableModel vendorModel;

    private JTable menuTable;
    private DefaultTableModel menuModel;
    private JComboBox<String> customerDropdown;
    private JSpinner quantitySpinner;

    // Color scheme for the application
    private final Color navy = new Color(25, 35, 55);
    private final Color orange = new Color(230, 126, 34);
    private final Color lightBg = new Color(245, 247, 250);

    // Constructor - initializes the GUI with all four dashboards
    public FoodExpressGUI() {

        // Set up the main window
        setTitle("Food Express Delivery Platform");
        setSize(1050, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabbed interface for different user roles
        JTabbedPane tabs = new JTabbedPane();

        JPanel customerPanel = buildCustomerPanel();
        JPanel restaurantPanel = buildRestaurantPanel();
        JPanel driverPanel = buildDriverPanel();
        JPanel adminPanel = buildAdminPanel();

        tabs.addTab("Customer", customerPanel);
        tabs.addTab("Restaurant", restaurantPanel);
        tabs.addTab("Driver", driverPanel);
        tabs.addTab("Admin", adminPanel);

        add(tabs);
        setVisible(true);
    }

    // Helper method to create styled title labels
    private JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(navy);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }

    // Helper method to create styled buttons with consistent appearance
    private JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(orange);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        return button;
    }

    // Build the Customer dashboard with restaurants and menu browsing
    private JPanel buildCustomerPanel() {
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBackground(lightBg);

        // Top section with title and controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightBg);

        JPanel customerButtonPanel = new JPanel();
        customerButtonPanel.setBackground(lightBg);
        JLabel customerLabel = new JLabel("Customer:");
customerLabel.setFont(new Font("Arial", Font.BOLD, 13));

customerDropdown = new JComboBox<>();
loadCustomersIntoDropdown();

JLabel quantityLabel = new JLabel("Quantity:");
quantityLabel.setFont(new Font("Arial", Font.BOLD, 13));

quantitySpinner = new JSpinner(
        new SpinnerNumberModel(1, 1, 20, 1)
);

customerButtonPanel.add(customerLabel);
customerButtonPanel.add(customerDropdown);

customerButtonPanel.add(quantityLabel);
customerButtonPanel.add(quantitySpinner);

        JButton loadVendorsButton = styledButton("Browse Restaurants");
        JButton loadMenuButton = styledButton("View Selected Menu");
        JButton placeOrderButton = styledButton("Place Order");

        customerButtonPanel.add(placeOrderButton);
        customerButtonPanel.add(loadMenuButton);
        customerButtonPanel.add(loadVendorsButton);

        topPanel.add(titleLabel("Customer Dashboard"), BorderLayout.WEST);
        topPanel.add(customerButtonPanel, BorderLayout.EAST);

        vendorModel = new DefaultTableModel();
        vendorModel.addColumn("Vendor ID");
        vendorModel.addColumn("Restaurant Name");
        vendorModel.addColumn("Category");

        vendorTable = new JTable(vendorModel);

        menuModel = new DefaultTableModel();
        menuModel.addColumn("Menu Item ID");
        menuModel.addColumn("Item Name");
        menuModel.addColumn("Description");
        menuModel.addColumn("Price");

        menuTable = new JTable(menuModel);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(vendorTable),
                new JScrollPane(menuTable)
        );

        splitPane.setDividerLocation(260);

        loadVendorsButton.addActionListener(e -> loadVendors());
        loadMenuButton.addActionListener(e -> loadMenuItems());
        placeOrderButton.addActionListener(e -> placeOrder());

        customerPanel.add(topPanel, BorderLayout.NORTH);
        customerPanel.add(splitPane, BorderLayout.CENTER);

        return customerPanel;
    }

    // Build the Restaurant dashboard for order management
    private JPanel buildRestaurantPanel() {
        JPanel restaurantPanel = new JPanel(new BorderLayout());
        restaurantPanel.setBackground(lightBg);

        // Top section with title and controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightBg);

        JPanel restaurantButtonPanel = new JPanel();
        restaurantButtonPanel.setBackground(lightBg);

        // Buttons to update order status
        JButton completedButton = styledButton("Mark Completed");
        JButton preparingButton = styledButton("Mark Preparing");
        JButton loadOrdersButton = styledButton("View Incoming Orders");

        restaurantButtonPanel.add(completedButton);
        restaurantButtonPanel.add(preparingButton);
        restaurantButtonPanel.add(loadOrdersButton);

        topPanel.add(titleLabel("Restaurant Dashboard"), BorderLayout.WEST);
        topPanel.add(restaurantButtonPanel, BorderLayout.EAST);

        DefaultTableModel orderModel = new DefaultTableModel();
        orderModel.addColumn("Order ID");
        orderModel.addColumn("Customer ID");
        orderModel.addColumn("Vendor ID");
        orderModel.addColumn("Status");
        orderModel.addColumn("Total");

        JTable orderTable = new JTable(orderModel);

        restaurantPanel.add(topPanel, BorderLayout.NORTH);
        restaurantPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        loadOrdersButton.addActionListener(e -> {
            try {
                orderModel.setRowCount(0);

                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT * FROM orders");

                while (rs.next()) {
                    orderModel.addRow(new Object[]{
                            rs.getInt("order_id"),
                            rs.getInt("customer_id"),
                            rs.getInt("vendor_id"),
                            rs.getString("order_status"),
                            rs.getDouble("total_amount")
                    });
                }

                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        preparingButton.addActionListener(e -> updateOrderStatus(orderTable, orderModel, "Preparing"));
        completedButton.addActionListener(e -> updateOrderStatus(orderTable, orderModel, "Completed"));

        return restaurantPanel;
    }

    // Build the Driver dashboard for delivery management
    private JPanel buildDriverPanel() {
        JPanel driverPanel = new JPanel(new BorderLayout());
        driverPanel.setBackground(lightBg);

        // Top section with title and controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightBg);

        JPanel driverButtonPanel = new JPanel();
        driverButtonPanel.setBackground(lightBg);

        // Buttons to update delivery status
        JButton deliveredButton = styledButton("Mark Delivered");
        JButton pickedUpButton = styledButton("Mark Picked Up");
        JButton loadDeliveriesButton = styledButton("View Assigned Deliveries");

        driverButtonPanel.add(deliveredButton);
        driverButtonPanel.add(pickedUpButton);
        driverButtonPanel.add(loadDeliveriesButton);

        topPanel.add(titleLabel("Driver Dashboard"), BorderLayout.WEST);
        topPanel.add(driverButtonPanel, BorderLayout.EAST);

        DefaultTableModel deliveryModel = new DefaultTableModel();
        deliveryModel.addColumn("Delivery ID");
        deliveryModel.addColumn("Order ID");
        deliveryModel.addColumn("Driver ID");
        deliveryModel.addColumn("Status");
        deliveryModel.addColumn("Assigned At");
        deliveryModel.addColumn("Delivered At");

        JTable deliveryTable = new JTable(deliveryModel);

        driverPanel.add(topPanel, BorderLayout.NORTH);
        driverPanel.add(new JScrollPane(deliveryTable), BorderLayout.CENTER);

        loadDeliveriesButton.addActionListener(e -> {
            try {
                deliveryModel.setRowCount(0);

                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT * FROM deliveries");

                while (rs.next()) {
                    deliveryModel.addRow(new Object[]{
                            rs.getInt("delivery_id"),
                            rs.getInt("order_id"),
                            rs.getInt("driver_id"),
                            rs.getString("delivery_status"),
                            rs.getString("assigned_at"),
                            rs.getString("delivered_at")
                    });
                }

                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        pickedUpButton.addActionListener(e -> updateDeliveryStatus(deliveryTable, deliveryModel, "Picked Up"));
        deliveredButton.addActionListener(e -> updateDeliveryStatus(deliveryTable, deliveryModel, "Delivered"));

        return driverPanel;
    }

    // Build the Admin dashboard for system-wide data management
    private JPanel buildAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.setBackground(lightBg);

        // Top section with title and controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightBg);

        JPanel adminButtonPanel = new JPanel();
        adminButtonPanel.setBackground(lightBg);

        // Admin management buttons
        JButton deleteOrderButton = styledButton("Delete Selected Order");
        JButton loadOrdersAdminButton = styledButton("View Orders");
        JButton loadVendorsAdminButton = styledButton("View Vendors");
        JButton loadCustomersButton = styledButton("View Customers");

        adminButtonPanel.add(deleteOrderButton);
        adminButtonPanel.add(loadOrdersAdminButton);
        adminButtonPanel.add(loadVendorsAdminButton);
        adminButtonPanel.add(loadCustomersButton);

        topPanel.add(titleLabel("Admin Dashboard"), BorderLayout.WEST);
        topPanel.add(adminButtonPanel, BorderLayout.EAST);

        // Table to display various admin data
        DefaultTableModel adminModel = new DefaultTableModel();
        JTable adminTable = new JTable(adminModel);

        adminPanel.add(topPanel, BorderLayout.NORTH);
        adminPanel.add(new JScrollPane(adminTable), BorderLayout.CENTER);

        // Load customers into the table
        loadCustomersButton.addActionListener(e -> {
            try {
                adminModel.setRowCount(0);
                adminModel.setColumnCount(0);

                adminModel.addColumn("Customer ID");
                adminModel.addColumn("First Name");
                adminModel.addColumn("Last Name");
                adminModel.addColumn("Email");

                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT * FROM customers");

                while (rs.next()) {
                    adminModel.addRow(new Object[]{
                            rs.getInt("customer_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    });
                }

                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadVendorsAdminButton.addActionListener(e -> {
            try {
                adminModel.setRowCount(0);
                adminModel.setColumnCount(0);

                adminModel.addColumn("Vendor ID");
                adminModel.addColumn("Restaurant Name");
                adminModel.addColumn("Category");

                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT * FROM vendors");

                while (rs.next()) {
                    adminModel.addRow(new Object[]{
                            rs.getInt("vendor_id"),
                            rs.getString("vendor_name"),
                            rs.getString("category")
                    });
                }

                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadOrdersAdminButton.addActionListener(e -> {
            try {
                adminModel.setRowCount(0);
                adminModel.setColumnCount(0);

                adminModel.addColumn("Order ID");
                adminModel.addColumn("Customer ID");
                adminModel.addColumn("Vendor ID");
                adminModel.addColumn("Status");
                adminModel.addColumn("Total");

                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT * FROM orders");

                while (rs.next()) {
                    adminModel.addRow(new Object[]{
                            rs.getInt("order_id"),
                            rs.getInt("customer_id"),
                            rs.getInt("vendor_id"),
                            rs.getString("order_status"),
                            rs.getDouble("total_amount")
                    });
                }

                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        deleteOrderButton.addActionListener(e -> {
            int row = adminTable.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an order first.");
                return;
            }

            try {
                int orderId = (int) adminModel.getValueAt(row, 0);

                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                stmt.executeUpdate("DELETE FROM deliveries WHERE order_id = " + orderId);
                stmt.executeUpdate("DELETE FROM order_items WHERE order_id = " + orderId);
                stmt.executeUpdate("DELETE FROM orders WHERE order_id = " + orderId);

                conn.close();

                JOptionPane.showMessageDialog(this, "Order deleted successfully!");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return adminPanel;
    }

    // Fetch all vendors from database and populate the vendor table
    private void loadVendors() {
        try {
            // Clear existing rows
            vendorModel.setRowCount(0);

            // Query database for vendors
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(
                    "SELECT vendor_id, vendor_name, category FROM vendors"
            );

            while (rs.next()) {
                vendorModel.addRow(new Object[]{
                        rs.getInt("vendor_id"),
                        rs.getString("vendor_name"),
                        rs.getString("category")
                });
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading restaurants.");
        }
    }
    // Populate the customer dropdown with all customers from database
    private void loadCustomersIntoDropdown() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Fetch all customers
            ResultSet rs = stmt.executeQuery(
                    "SELECT customer_id, first_name, last_name FROM customers"
            );

        while (rs.next()) {

            String customer = rs.getInt("customer_id") +
                    " - " +
                    rs.getString("first_name") +
                    " " +
                    rs.getString("last_name");

            customerDropdown.addItem(customer);
        }

        conn.close();

    } catch (Exception e) {

        e.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                "Error loading customers."
        );
    }
}

    // Load menu items for the selected vendor
    private void loadMenuItems() {
        // Get the selected restaurant
        int selectedRow = vendorTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant first.");
            return;
        }

        // Extract vendor ID from selected row
        int vendorId = (int) vendorModel.getValueAt(selectedRow, 0);

        try {
            menuModel.setRowCount(0);

            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(
                    "SELECT menu_item_id, item_name, description, price " +
                            "FROM menu_items " +
                            "WHERE vendor_id = " + vendorId + " AND is_available = TRUE"
            );

            while (rs.next()) {
                menuModel.addRow(new Object[]{
                        rs.getInt("menu_item_id"),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getDouble("price")
                });
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading menu items.");
        }
    }

    // Create a new order with selected menu item
    private void placeOrder() {
        // Validate selections
        int vendorRow = vendorTable.getSelectedRow();
        int menuRow = menuTable.getSelectedRow();

        if (vendorRow == -1 || menuRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant and menu item.");
            return;
        }

        // Default driver ID for new orders
        int driverId = 1;

        // Extract order details from UI selections
        int vendorId = (int) vendorModel.getValueAt(vendorRow, 0);
        int menuItemId = (int) menuModel.getValueAt(menuRow, 0);
        String selectedCustomer = (String) customerDropdown.getSelectedItem();

        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer.");
            return;
        }

        // Parse customer ID from dropdown display text
        int customerId = Integer.parseInt(selectedCustomer.split(" - ")[0]);

        // Get quantity and calculate total price
        int quantity = (int) quantitySpinner.getValue();
        double unitPrice = (double) menuModel.getValueAt(menuRow, 3);
        double totalPrice = unitPrice * quantity;

        try {
            // Create database connection and insert order
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Insert the order
            stmt.executeUpdate(
                    "INSERT INTO orders " +
                            "(customer_id, vendor_id, order_status, total_amount) " +
                            "VALUES (" + customerId + ", " + vendorId + ", 'Placed', " + totalPrice + ")"
            );

            // Get the newly created order ID
            ResultSet rs = stmt.executeQuery("SELECT MAX(order_id) AS last_id FROM orders");

            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt("last_id");
            }

            // Add order items
            stmt.executeUpdate(
                    "INSERT INTO order_items " +
                            "(order_id, menu_item_id, quantity, unit_price) " +
                            "VALUES (" + orderId + ", " + menuItemId + ", " + quantity + ", " + unitPrice + ")"
            );

            // Create delivery record for the order
            stmt.executeUpdate(
                    "INSERT INTO deliveries " +
                            "(order_id, driver_id, delivery_status) " +
                            "VALUES (" + orderId + ", " + driverId + ", 'Assigned')"
            );

            conn.close();

            JOptionPane.showMessageDialog(this, "Order placed successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error placing order.");
        }
    }

    // Update the status of a selected order
    private void updateOrderStatus(JTable orderTable, DefaultTableModel orderModel, String status) {
        // Get selected order
        int row = orderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        // Extract order ID and update status in database
        int orderId = (int) orderModel.getValueAt(row, 0);

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(
                    "UPDATE orders SET order_status = '" + status + "' WHERE order_id = " + orderId
            );

            conn.close();

            JOptionPane.showMessageDialog(this, "Order updated to " + status + ".");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Update the status of a selected delivery
    private void updateDeliveryStatus(JTable deliveryTable, DefaultTableModel deliveryModel, String status) {
        // Get selected delivery
        int row = deliveryTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a delivery first.");
            return;
        }

        // Extract delivery ID
        int deliveryId = (int) deliveryModel.getValueAt(row, 0);

        try {
            // Update delivery status in database
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            // If marked as delivered, also record the timestamp
            if (status.equals("Delivered")) {
                stmt.executeUpdate(
                        "UPDATE deliveries SET delivery_status = 'Delivered', delivered_at = NOW() " +
                                "WHERE delivery_id = " + deliveryId
                );
            } else {
                // For other statuses, just update the status
                stmt.executeUpdate(
                        "UPDATE deliveries SET delivery_status = '" + status + "' " +
                                "WHERE delivery_id = " + deliveryId
                );
            }

            conn.close();

            JOptionPane.showMessageDialog(this, "Delivery updated to " + status + ".");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}