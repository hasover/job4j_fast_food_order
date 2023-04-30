
create table orders (
                              id serial primary key ,
                              status varchar(150),
                              payment_id int,
                              customer_id int

);