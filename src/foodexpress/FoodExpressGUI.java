package foodexpress;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class FoodExpressGUI extends JFrame {

    private JTable vendorTable;
    private DefaultTableModel vendorModel;

    private JTable menuTable;
    private DefaultTableModel menuModel;

    private final Color navy = new Color(25, 35, 55);
    private final Color orange = new Color(230, 126, 34);
    private final Color lightBg = new Color(245, 247, 250);

    public FoodExpressGUI() {

        setTitle("Food Express Delivery Platform");
        setSize(1050, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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

    private JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(navy);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }

    private JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(orange);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        return button;
    }

    private JPanel buildCustomerPanel() {
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBackground(lightBg);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightBg);

        JPanel customerButtonPanel = new JPanel();
        customerButtonPanel.setBackground(lightBg);

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

    private JPanel buildRestaurantPanel() {
        JPanel restaurantPanel = new JPanel(new BorderLayout());
        restaurantPanel.setBackground(lightBg);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightBg);

        JPanel restaurantButtonPanel = new JPanel();
        restaurantButtonPanel.setBackground(lightBg);

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

    private JPanel buildDriverPanel() {
        JPanel driverPanel = new JPanel(new BorderLayout());
        driverPanel.setBackground(lightBg);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightBg);

        JPanel driverButtonPanel = new JPanel();
        driverButtonPanel.setBackground(lightBg);

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

    private JPanel buildAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.setBackground(lightBg);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightBg);

        JPanel adminButtonPanel = new JPanel();
        adminButtonPanel.setBackground(lightBg);

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

        DefaultTableModel adminModel = new DefaultTableModel();
        JTable adminTable = new JTable(adminModel);

        adminPanel.add(topPanel, BorderLayout.NORTH);
        adminPanel.add(new JScrollPane(adminTable), BorderLayout.CENTER);

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

    private void loadVendors() {
        try {
            vendorModel.setRowCount(0);

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

    private void loadMenuItems() {
        int selectedRow = vendorTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant first.");
            return;
        }

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

    private void placeOrder() {
        int vendorRow = vendorTable.getSelectedRow();
        int menuRow = menuTable.getSelectedRow();

        if (vendorRow == -1 || menuRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant and menu item.");
            return;
        }

        int customerId = 1;
        int driverId = 1;

        int vendorId = (int) vendorModel.getValueAt(vendorRow, 0);
        int menuItemId = (int) menuModel.getValueAt(menuRow, 0);
        double price = (double) menuModel.getValueAt(menuRow, 3);

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(
                    "INSERT INTO orders " +
                            "(customer_id, vendor_id, order_status, total_amount) " +
                            "VALUES (" + customerId + ", " + vendorId + ", 'Placed', " + price + ")"
            );

            ResultSet rs = stmt.executeQuery("SELECT MAX(order_id) AS last_id FROM orders");

            int orderId = 0;

            if (rs.next()) {
                orderId = rs.getInt("last_id");
            }

            stmt.executeUpdate(
                    "INSERT INTO order_items " +
                            "(order_id, menu_item_id, quantity, unit_price) " +
                            "VALUES (" + orderId + ", " + menuItemId + ", 1, " + price + ")"
            );

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

    private void updateOrderStatus(JTable orderTable, DefaultTableModel orderModel, String status) {
        int row = orderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

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

    private void updateDeliveryStatus(JTable deliveryTable, DefaultTableModel deliveryModel, String status) {
        int row = deliveryTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a delivery first.");
            return;
        }

        int deliveryId = (int) deliveryModel.getValueAt(row, 0);

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            if (status.equals("Delivered")) {
                stmt.executeUpdate(
                        "UPDATE deliveries SET delivery_status = 'Delivered', delivered_at = NOW() " +
                                "WHERE delivery_id = " + deliveryId
                );
            } else {
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