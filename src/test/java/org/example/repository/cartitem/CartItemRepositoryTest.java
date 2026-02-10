package org.example.repository.cartitem;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import org.example.model.Book;
import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.example.model.User;
import org.example.repository.book.BookRepository;
import org.example.repository.shoppingcart.ShoppingCartRepository;
import org.example.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/clear-all-tables.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Check, that certain book id is assign to shopping cart"
            + " id by return proper cart item")
    @Sql(scripts = "classpath:database/clear-all-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findItemByProperBookIdAndShoppingCartId() {

        User user = new User();
        user.setFirstName("userFirstName");
        user.setLastName("userLastName");
        user.setEmail("user2@email2.com");
        user.setPassword("password1");
        userRepository.save(user);

        Book book = new Book()
                .setTitle("First")
                .setAuthor("FirstAuthor")
                .setIsbn("123456701")
                .setPrice(BigDecimal.valueOf(100));
        bookRepository.save(book);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);

        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        cartItem.setShoppingCart(shoppingCart);
        cartItemRepository.save(cartItem);

        shoppingCart.setCartItems(Set.of(cartItem));

        User user2 = new User();
        user2.setFirstName("userFirstName");
        user2.setLastName("userLastName");
        user2.setEmail("user3@email3.com");
        user2.setPassword("password2");
        userRepository.save(user2);

        Book book2 = new Book()
                .setTitle("First")
                .setAuthor("FirstAuthor")
                .setIsbn("123456702")
                .setPrice(BigDecimal.valueOf(100));
        bookRepository.save(book2);

        ShoppingCart shoppingCart2 = new ShoppingCart();
        shoppingCart2.setUser(user2);
        shoppingCartRepository.save(shoppingCart2);

        CartItem cartItem2 = new CartItem();
        cartItem2.setBook(book2);
        cartItem2.setQuantity(4);
        cartItem2.setShoppingCart(shoppingCart2);
        cartItemRepository.save(cartItem2);

        shoppingCart2.setCartItems(Set.of(cartItem2));

        cartItem2.setShoppingCart(shoppingCart2);
        cartItemRepository.save(cartItem2);

        cartItem2.setShoppingCart(shoppingCart2);

        Optional<CartItem> itemByProperBookIdAndShoppingCartId = cartItemRepository
                .findItemByProperBookIdAndShoppingCartId(2L, shoppingCart2.getId());

        Optional<CartItem> itemByProperBookIdAndShoppingCartId2 = cartItemRepository
                .findItemByProperBookIdAndShoppingCartId(1L, shoppingCart.getId());

        Assertions.assertEquals(itemByProperBookIdAndShoppingCartId.get(),
                cartItem2);

        Assertions.assertEquals(itemByProperBookIdAndShoppingCartId2.get(),
                cartItem);
    }
}
