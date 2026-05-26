drop database if exists kanin_nyc;
create database kanin_nyc;
use kanin_nyc;

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
    status enum('PENDING', 'SUCCEEDED', 'FAILED', 'REFUNDED', 'CANCELLED') not null default 'PENDING',
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
