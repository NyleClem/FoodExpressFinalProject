USE FoodExpressDB;

-- Customers
INSERT INTO customers (first_name, last_name, email, phone, address) VALUES
('Nyle', 'Clements', 'nyle.clements@gmail.com', '410-111-2222', '100 Main St'),
('Myles', 'Burrows', 'myles.burrows@gmail.com', '410-222-3333', '200 Oak Ave'),
('Justice', 'Moody', 'justice.moody@gmail.com', '410-333-4444', '300 Pine Rd'),
('Nand', 'Chana', 'nand.chana@gmail.com', '410-444-5555', '400 Hill Rd'),
('Franklin', 'Atika', 'franklin.atika@gmail.com', '410-555-6666', '500 Lake Ave');

-- Vendors / Restaurants
INSERT INTO vendors (vendor_name, category, phone, address, is_active) VALUES
('Burger Bros', 'Burgers', '410-555-1000', '10 Food St', TRUE),
('Taco Teleport', 'Mexican', '410-555-2000', '20 Market Ave', TRUE),
('Pizza People', 'Pizza', '410-555-3000', '30 City Rd', TRUE);

-- Menu Items
INSERT INTO menu_items (vendor_id, item_name, description, price, is_available) VALUES
(1, 'Classic Burger', 'Beef burger with lettuce and tomato', 9.99, TRUE),
(1, 'Chicken Sandwich', 'Grilled chicken sandwich', 8.99, TRUE),
(1, 'Fries', 'Seasoned fries', 3.49, TRUE),
(1, 'Double Burger', 'Two beef patties with cheese', 12.99, TRUE),
(1, 'Loaded Fries', 'Fries with cheese and toppings', 5.99, TRUE),

(2, 'Chicken Tacos', 'Three chicken tacos', 10.99, TRUE),
(2, 'Steak Burrito', 'Large steak burrito', 12.49, TRUE),
(2, 'Quesadilla', 'Chicken quesadilla with salsa', 9.49, TRUE),
(2, 'Nacho Bowl', 'Rice, beans, cheese, and chicken', 11.49, TRUE),

(3, 'Cheese Pizza', 'Medium cheese pizza', 11.99, TRUE),
(3, 'Pepperoni Pizza', 'Medium pepperoni pizza', 13.99, TRUE),
(3, 'Veggie Pizza', 'Vegetable pizza with mushrooms and peppers', 14.99, TRUE),
(3, 'Garlic Knots', 'Garlic bread knots with sauce', 4.99, TRUE);

-- Drivers
INSERT INTO drivers (first_name, last_name, phone, vehicle_type, is_available) VALUES
('Dante', 'Miller', '410-444-1111', 'Car', TRUE),
('Alex', 'Smith', '410-444-2222', 'Bike', TRUE),
('Jordan', 'Lee', '410-444-3333', 'Car', TRUE);

-- Orders
INSERT INTO orders (customer_id, vendor_id, order_status, total_amount) VALUES
(1, 1, 'Placed', 13.48),
(2, 2, 'Preparing', 10.99),
(3, 3, 'Ready', 13.99),
(4, 1, 'Placed', 18.98),
(5, 2, 'Completed', 20.98),
(1, 3, 'Completed', 19.98),
(2, 1, 'Preparing', 12.99),
(3, 2, 'Placed', 11.49);

-- Order Items
INSERT INTO order_items (order_id, menu_item_id, quantity, unit_price) VALUES
(1, 1, 1, 9.99),
(1, 3, 1, 3.49),

(2, 6, 1, 10.99),

(3, 11, 1, 13.99),

(4, 4, 1, 12.99),
(4, 5, 1, 5.99),

(5, 8, 1, 9.49),
(5, 9, 1, 11.49),

(6, 12, 1, 14.99),
(6, 13, 1, 4.99),

(7, 4, 1, 12.99),

(8, 9, 1, 11.49);

-- Deliveries
INSERT INTO deliveries (order_id, driver_id, delivery_status, delivered_at) VALUES
(1, 1, 'Assigned', NULL),
(2, 2, 'Picked Up', NULL),
(3, 3, 'Assigned', NULL),
(4, 1, 'Assigned', NULL),
(5, 2, 'Delivered', NOW()),
(6, 3, 'Delivered', NOW()),
(7, 1, 'Picked Up', NULL),
(8, 2, 'Assigned', NULL);