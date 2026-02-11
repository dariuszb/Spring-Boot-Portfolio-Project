package org.example.repository.shoppingcart;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.example.model.Book;
import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.example.model.User;
import org.example.repository.book.BookRepository;
import org.example.repository.cartitem.CartItemRepository;
import org.example.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/clear-all-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ShoppingCartRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Check, that user's shopping cart with user, who email input"
            + " is returned, when it exist")
    @Sql(scripts = "classpath:database/clear-all-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserEmail_existCartWithUserInputEmail_success() {
        org.example.model.User user = new User();
        user.setFirstName("userSecondName");
        user.setLastName("userLastName");
        user.setEmail("user2@email2.com");
        user.setPassword("password");
        userRepository.save(user);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);

        Book book = new Book();
        book.setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of())
                .setDescription("Description1")
                .setCoverImage("CoverImage1");
        bookRepository.save(book);

        CartItem cartItem1 = new CartItem();
        cartItem1.setShoppingCart(shoppingCart);
        cartItem1.setBook(book);
        cartItem1.setQuantity(2);
        cartItemRepository.save(cartItem1);

        shoppingCart.setCartItems(Set.of(cartItem1));

        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserEmail(user.getEmail());

        Assertions.assertThat(actual).isEqualTo(Optional.of(shoppingCart));

    }

    @Test
    @DisplayName("Check, that empty optional is returned, when shopping cart's"
            + " user with input user's email doesn't exist")
    void findByUserEmail_nonExistCartWithUserWhoEmailInput_shouldReturnOptionalEmpty() {

        Optional<ShoppingCart> nonExistUserEmail = shoppingCartRepository
                .findByUserEmail("nonExistUserEmail");

        Assertions.assertThat(nonExistUserEmail)
                .isEqualTo(Optional.empty());

    }

}
