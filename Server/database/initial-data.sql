use kanin_nyc;

insert into user (email, password, role, approved) values
    ('admin@kaninnyc.com', 'password', 'ADMIN', true),
    ('cashier@kaninnyc.com', 'password', 'CASHIER', true),
    ('chef@kaninnyc.com', 'password', 'CHEF', true),
    ('pending@kaninnyc.com', 'password', 'PENDING', false);

insert into menu_items (name, price, description) values
    ('Chicken Adobo', 6.00, 'Halal Boneless Chicken Thighs Braised in Soy Sauce, Coconut Milk, Vinegar, Garlic'),
    ('Lumpia', 6.00, 'Fried Pork Spring Rolls: Pork, Scallions, Carrots, Jicama, Mushrooms with Sweet Chilli Dipping Sauce'),
    ('Palabok', 6.00, 'Rice Noodles with Shrimp Bisque and Garnished with Pork Chicharon, Shrimp, Egg, Fried Garlic, Scallions'),
    ('Halo Halo', 6.00, 'Layered Shaved Ice Dessert with Sweet Red Beans, Jellies, Toasted Rice Pinipig, and Ube Halaya'),
    ('Sago''t Gulaman', 6.00, 'Brown Sugar Sahved Ice Drink with Tapioca Pearls and Agar Jelly'),
    ('Ube Cooler', 6.00, 'Creamy Purple Yam Cooler Ice Drink with Tapioca Pearls');

insert into inventory (name, quantity, unit, notes) values
    ('Chicken thighs', 40, 'lb', 'For chicken adobo.'),
    ('Garlic rice', 60, 'servings', 'Prepared rice portions.'),
    ('Lumpia wrappers', 200, 'pieces', 'Frozen backup in walk-in.'),
    ('Ground pork filling', 25, 'lb', 'For lumpia.'),
    ('Rice noodles', 30, 'lb', 'For palabok.'),
    ('Shrimp sauce', 20, 'qt', 'Palabok sauce base.'),
    ('Shaved ice mix-ins', 80, 'servings', 'Halo halo toppings.'),
    ('Ube halaya', 18, 'qt', 'For halo halo and ube cooler.'),
    ('Sago pearls', 35, 'servings', 'For sago''t gulaman.'),
    ('Gulaman', 35, 'servings', 'For sago''t gulaman.');

insert into orders (name, status, payment_method, total, notes) values
    ('Maya Santos', 'RECEIVED', 'CARD', 18.00, 'Extra napkins.'),
    ('Luis Reyes', 'IN_PROGRESS', 'CASH', 12.00, 'No scallions on palabok.'),
    ('Tara Cruz', 'READY', 'VENMO', 12.00, null),
    ('Nico Garcia', 'COMPLETED', 'ZELLE', 12.00, 'Picked up at counter.');

insert into order_items (order_id, menu_item, quantity) values
    (1, 1, 1),
    (1, 2, 1),
    (1, 6, 1),
    (2, 3, 1),
    (2, 2, 1),
    (3, 4, 1),
    (3, 5, 1),
    (4, 1, 1),
    (4, 6, 1);

insert into payments (order_id, status, amount, stripe_checkout_session_id, stripe_payment_intent_id, stripe_charge_id) values
    (1, 'SUCCEEDED', 18.00, 'cs_test_kanin_001', 'pi_test_kanin_001', 'ch_test_kanin_001');
