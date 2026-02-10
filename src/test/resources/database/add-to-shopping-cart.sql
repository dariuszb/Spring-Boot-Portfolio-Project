INSERT INTO users(id, first_name, last_name, email, password,is_deleted) VALUES(1,'userFirstName', 'userLastName','user1@email1.com', 'password',false);
INSERT INTO users(id, first_name, last_name, email, password,is_deleted) VALUES(2,'userSecondName', 'userLastName','user2@email2.com', 'password',false);
INSERT INTO users(id, first_name, last_name, email, password,is_deleted) VALUES(3,'userThirdName', 'userLastName','user3@email3.com', 'password',false);

INSERT INTO books(id, title, author, isbn, price, is_deleted) VALUES(1,'First', 'FirstAuthor', '123456701', 100, false);

INSERT INTO shopping_carts(id, user_id) VALUES(1,1);
INSERT INTO shopping_carts(id, user_id) VALUES(2,2);
INSERT INTO shopping_carts(id, user_id) VALUES(3,3);

INSERT INTO cart_items(id, shopping_cart_id, book_id, quantity) VALUES(1, 2,1,2);