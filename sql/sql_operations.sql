USE FoodExpressDB;

-- =========================
-- SELECT
-- =========================

-- View all customers
SELECT * FROM customers;

-- View all vendors/restaurants
SELECT * FROM vendors;

-- View all menu items
SELECT * FROM menu_items;

-- View all orders
SELECT * FROM orders;

-- View all order items, including quantity
SELECT * FROM order_items;

-- View all deliveries
SELECT * FROM deliveries;


-- =========================
-- WHERE FILTERING
-- =========================

-- View completed orders
SELECT *
FROM orders
WHERE order_status = 'Completed';

-- View menu items over $10
SELECT *
FROM menu_items
WHERE price > 10;


-- =========================
-- ORDER BY
-- =========================

-- View menu items from highest to lowest price
SELECT *
FROM menu_items
ORDER BY price DESC;


-- =========================
-- JOIN QUERY #1
-- Customer Orders
-- =========================

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


-- =========================
-- JOIN QUERY #2
-- Deliveries and Drivers
-- =========================

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
-- JOIN QUERY #3
-- Order Items with Menu Details
-- =========================

SELECT
    oi.order_id,
    mi.item_name,
    oi.quantity,
    oi.unit_price,
    (oi.quantity * oi.unit_price) AS line_total
FROM order_items oi
JOIN menu_items mi
ON oi.menu_item_id = mi.menu_item_id;


-- =========================
-- AGGREGATE QUERY #1
-- COUNT
-- =========================

SELECT COUNT(*) AS total_orders
FROM orders;


-- =========================
-- AGGREGATE QUERY #2
-- SUM
-- =========================

SELECT SUM(total_amount) AS total_revenue
FROM orders;


-- =========================
-- GROUP BY QUERY #1
-- Orders Per Vendor
-- =========================

SELECT
    v.vendor_name,
    COUNT(o.order_id) AS total_orders
FROM vendors v
LEFT JOIN orders o
ON v.vendor_id = o.vendor_id
GROUP BY v.vendor_name;


-- =========================
-- GROUP BY QUERY #2
-- Revenue Per Vendor
-- =========================

SELECT
    v.vendor_name,
    SUM(o.total_amount) AS revenue
FROM vendors v
JOIN orders o
ON v.vendor_id = o.vendor_id
GROUP BY v.vendor_name;


-- =========================
-- INSERT EXAMPLE
-- Simulates placing a new order
-- =========================

INSERT INTO orders (customer_id, vendor_id, order_status, total_amount)
VALUES (1, 1, 'Placed', 25.98);

INSERT INTO order_items (order_id, menu_item_id, quantity, unit_price)
VALUES (LAST_INSERT_ID(), 4, 2, 12.99);


-- =========================
-- UPDATE EXAMPLE
-- Restaurant updates order status
-- =========================

UPDATE orders
SET order_status = 'Completed'
WHERE order_id = 1;


-- =========================
-- UPDATE EXAMPLE
-- Driver updates delivery status
-- =========================

UPDATE deliveries
SET delivery_status = 'Delivered',
    delivered_at = NOW()
WHERE delivery_id = 1;


-- =========================
-- DELETE EXAMPLE
-- Delete an order and related records
-- =========================

DELETE FROM deliveries
WHERE order_id = 1;

DELETE FROM order_items
WHERE order_id = 1;

DELETE FROM orders
WHERE order_id = 1;