USE FoodExpressDB;

-- =========================
-- BASIC SELECT QUERIES
-- =========================

-- View all customers
SELECT * FROM customers;

-- View all vendors
SELECT * FROM vendors;

-- View all menu items
SELECT * FROM menu_items;


-- =========================
-- WHERE FILTERING
-- =========================

-- View only available menu items
SELECT *
FROM menu_items
WHERE is_available = TRUE;

-- View orders with status 'Placed'
SELECT *
FROM orders
WHERE order_status = 'Ready';


-- =========================
-- ORDER BY
-- =========================

-- View menu items ordered by price
SELECT *
FROM menu_items
ORDER BY price DESC;


-- =========================
-- JOIN QUERIES
-- =========================

-- View customer orders
SELECT
    o.order_id,
    c.first_name,
    c.last_name,
    v.vendor_name,
    o.total_amount,
    o.order_status
FROM orders o
JOIN customers c
ON o.customer_id = c.customer_id
JOIN vendors v
ON o.vendor_id = v.vendor_id;

-- View deliveries with driver information
SELECT
    d.delivery_id,
    o.order_id,
    dr.first_name,
    dr.last_name,
    d.delivery_status
FROM deliveries d
JOIN orders o
ON d.order_id = o.order_id
JOIN drivers dr
ON d.driver_id = dr.driver_id;


-- =========================
-- AGGREGATE QUERIES
-- =========================

-- Count total orders
SELECT COUNT(*) AS total_orders
FROM orders;

-- Calculate total revenue
SELECT SUM(total_amount) AS total_revenue
FROM orders;


-- =========================
-- GROUP BY QUERIES
-- =========================

-- Total orders per vendor
SELECT
    v.vendor_name,
    COUNT(o.order_id) AS total_orders
FROM vendors v
LEFT JOIN orders o
ON v.vendor_id = o.vendor_id
GROUP BY v.vendor_name;

-- Revenue by vendor
SELECT
    v.vendor_name,
    SUM(o.total_amount) AS revenue
FROM vendors v
JOIN orders o
ON v.vendor_id = o.vendor_id
GROUP BY v.vendor_name;


-- =========================
-- UPDATE QUERIES
-- =========================

-- Update order status
UPDATE orders
SET order_status = 'Completed'
WHERE order_id = 1;

-- Update delivery status
UPDATE deliveries
SET delivery_status = 'Delivered'
WHERE delivery_id = 1;


-- =========================
-- DELETE QUERIES
-- =========================

-- Delete a delivery
DELETE FROM deliveries
WHERE delivery_id = 3;

-- Delete an order item
DELETE FROM order_items
WHERE order_id = 3
AND menu_item_id = 7;