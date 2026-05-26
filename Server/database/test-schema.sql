drop database if exists kanin_nyc_test;
create database kanin_nyc_test;
use kanin_nyc_test;

create table user (
    id int primary key auto_increment,
    email text ,
    password text,
    role enum(
    	'PENDING',
        'CASHIER',
        'CHEF',
        'ADMIN') not null default 'PENDING',
    approved boolean default false,
    created_at datetime default current_timestamp
);

create table menu_items (
	id int primary key auto_increment,
	name varchar(100) not null,
	price decimal(8,2) not null,
	description text
);

create table orders (
	id int primary key auto_increment,
	name varchar(100) not null,
	status enum(
		'RECEIVED',
		'IN_PROGRESS',
        'READY',
        'COMPLETED',
        'CANCELLED') not null default 'RECEIVED',
    payment_method enum(
    	'CASH',
        'VENMO',
        'ZELLE',
        'CARD') not null,
    total decimal(8, 2) not null,
    created_at datetime default current_timestamp,
    notes text
);

create table order_items (
	id int primary key auto_increment,
	order_id int,
	menu_item int,
	quantity int,
	constraint fk_order_items_order_id
		foreign key(order_id)
		references orders(id),
	constraint fk_order_items_menu_item
		foreign key(menu_item)
		references menu_items(id)
);

create table payments(
	id int primary key auto_increment,
    order_id int not null,
    status enum(
    	'PENDING',
    	'SUCCEEDED',
    	'FAILED',
    	'REFUNDED',
    	'CANCELLED') not null default 'PENDING',
    amount decimal(8,2) not null,
    stripe_checkout_session_id varchar(255),
    stripe_payment_intent_id varchar(255),
    stripe_charge_id varchar(255),
    created_at datetime not null default current_timestamp,
    constraint fk_payments_order
    	foreign key (order_id)
    	references orders(id)
);

create table inventory (
	id int primary key auto_increment,
	name varchar(100),
	quantity int,
	unit varchar(100),
	notes text
);

delimiter //
create procedure set_known_good_state()
begin
	delete from order_items;
	alter table order_items auto_increment = 1;
	delete from payments;
	alter table payments auto_increment = 1;
	delete from orders;
	alter table orders auto_increment = 1;
	delete from menu_items;
	alter table menu_items auto_increment = 1;
	delete from inventory;
	alter table inventory auto_increment = 1;
	delete from user;
	alter table user auto_increment = 1;

	insert into user (email, password, role, approved) values
        ('admin@kaninnyc.com', 'password', 'ADMIN', true),
        ('cashier@kaninnyc.com', 'password', 'CASHIER', true),
        ('chef@kaninnyc.com', 'password', 'CHEF', true),
        ('pending@kaninnyc.com', 'password', 'PENDING', false);

	insert into menu_items (name, price, description) values
		('Chicken Adobo', 6.00, 'Halal Boneless Chicken Thighs braised in Soy Sauce, Coconut Milk, Vinegar, Garlic'),
		('Lumpia', 6.00, 'Fried Pork Spring Rolls: Pork, Scallions, Carrots, Jicama, Mushrooms'),
		('Palabok', 6.00, 'Rice Noodles with Shrimp Bisque and garnished with Pork Chicharon, Shrimp, Egg, Fried Garlic, Scallions'),
		('Halo Halo', 6.00, 'Layered shaved ice dessert with sweet beans, jellies, leche flan, and ube.'),
		('Sago''t Gulaman', 6.00, 'Brown sugar arnibal drink with tapioca pearls and gulaman.'),
		('Ube Cooler', 6.00, 'Creamy chilled ube drink with milk and ice.');

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
end //
delimiter ;
