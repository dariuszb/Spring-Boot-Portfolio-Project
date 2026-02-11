package org.example.service.shoppingcart;

import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.example.dto.cartitem.CartItemDto;
import org.example.dto.cartitem.CreateCartItemDto;
import org.example.dto.cartitem.UpdateCartItemDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.ShoppingCartMapper;
import org.example.model.Book;
import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.example.model.User;
import org.example.repository.book.BookRepository;
import org.example.repository.cartitem.CartItemRepository;
import org.example.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartServiceImpl;

    @Test
    @DisplayName("Check, that authenticated user's shopping cart is returned")
    void get_existShoppingCart_returnShoppingCart() {

        org.example.model.User user = new User();
        user.setId(2L);
        user.setFirstName("userSecondName");
        user.setLastName("userLastName");
        user.setEmail("user2@email2.com");
        user.setPassword("password");

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);

        Book book = new Book();
        book.setId(1L)
                .setTitle("Title1").setAuthor("Author1")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of())
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setShoppingCart(shoppingCart);
        cartItem1.setBook(book);
        cartItem1.setQuantity(2);

        CartItemDto cartItemDto1 = new CartItemDto();
        cartItemDto1.setId(1L);
        cartItemDto1.setBookId(1L);
        cartItem1.setQuantity(2);

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(1L);
        shoppingCartDto.setUserId(1L);
        shoppingCartDto.setCartItems(Set.of(cartItemDto1));

        shoppingCart.setCartItems(Set.of(cartItem1));

        Mockito.when(shoppingCartRepository.findByUserEmail(user.getUsername()))
                .thenReturn(Optional.of(shoppingCart));

        Mockito.when(shoppingCartMapper.toDto(shoppingCart))
                        .thenReturn(shoppingCartDto);

        ShoppingCartDto dto = shoppingCartServiceImpl.get("user2@email2.com");

        Mockito.verify(shoppingCartRepository, Mockito.times(1))
                .findByUserEmail(user.getUsername());

        Mockito.verify(shoppingCartMapper).toDto(any(ShoppingCart.class));

        Assertions.assertThat(dto).isEqualTo(shoppingCartDto);

    }

    @Test
    @DisplayName("Verify, that new cart item is added to shopping cart")
    void addBookToShoppingCart_newCartItem_returnShoppingCartWithNewCartItem() {

        User user = new User();
        user.setId(2L);
        user.setFirstName("userFirstName");
        user.setLastName("userLastName");
        user.setEmail("user2@email2.com");

        Book book = new Book()
                .setId(1L)
                .setTitle("First")
                .setAuthor("FirstAuthor")
                .setIsbn("123456701")
                .setPrice(BigDecimal.valueOf(100));

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(2);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setId(1L);
        shoppingCart.setCartItems(Set.of(cartItem));

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(1L);
        cartItemDto.setBookId(1L);
        cartItemDto.setBookTitle("Title1");
        cartItemDto.setQuantity(2);

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(1L);
        shoppingCartDto.setUserId(1L);
        shoppingCartDto.setCartItems(Set.of(cartItemDto));

        CreateCartItemDto createCartItemDto = new CreateCartItemDto(
                1L, 2);

        Mockito.when(shoppingCartRepository.findByUserEmail(user.getEmail()))
                .thenReturn(Optional.of(shoppingCart));

        Mockito.when(bookRepository.findById(1L))
                        .thenReturn(Optional.of(book));

        Mockito.when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartDto);

        ShoppingCartDto dto = shoppingCartServiceImpl.addBookToShoppingCart(
                "user2@email2.com", createCartItemDto);

        Mockito.verify(shoppingCartRepository).findByUserEmail(user.getEmail());

        Mockito.verify(cartItemRepository)
                .findItemByProperBookIdAndShoppingCartId(any(), any());

        Mockito.verify(bookRepository).findById(any(Long.class));

        Mockito.verify(cartItemRepository).save(any(CartItem.class));

        Assertions.assertThat(dto).isEqualTo(shoppingCartDto);

    }

    @Test
    @DisplayName("Check, that quantity of exist cart item growth, when add"
            + " item with the same book id")
    void addBookToShoppingCart_existCartItem_returnShoppingCartWithCartItemWithAddedQuantity() {
        User user = new User();
        user.setId(2L);
        user.setFirstName("userFirstName");
        user.setLastName("userLastName");
        user.setEmail("user2@email2.com");

        Book book = new Book()
                .setId(1L)
                .setTitle("First")
                .setAuthor("FirstAuthor")
                .setIsbn("123456701")
                .setPrice(BigDecimal.valueOf(100));

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(2);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setId(1L);
        shoppingCart.setCartItems(Set.of(cartItem));

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(1L);
        cartItemDto.setBookId(1L);
        cartItemDto.setBookTitle("Title1");
        cartItemDto.setQuantity(7);

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        expected.setUserId(1L);
        expected.setCartItems(Set.of(cartItemDto));

        CreateCartItemDto createCartItemDto = new CreateCartItemDto(
                1L, 5);

        Mockito.when(shoppingCartRepository.findByUserEmail(user.getEmail()))
                .thenReturn(Optional.of(shoppingCart));

        Mockito.when(cartItemRepository.findItemByProperBookIdAndShoppingCartId(
                createCartItemDto.bookId(), expected.getId()))
                .thenReturn(Optional.of(cartItem));

        Mockito.when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(expected);

        ShoppingCartDto actual = shoppingCartServiceImpl.addBookToShoppingCart(
                "user2@email2.com", createCartItemDto);

        Mockito.verify(shoppingCartRepository).findByUserEmail(user.getEmail());

        Mockito.verify(cartItemRepository)
                .findItemByProperBookIdAndShoppingCartId(any(), any());

        Mockito.verify(cartItemRepository).save(any(CartItem.class));

        Mockito.verify(shoppingCartMapper).toDto(any(ShoppingCart.class));

        Assertions.assertThat(expected).isEqualTo(actual);

    }

    @Test
    @DisplayName("Check, that exception is throwing when add"
            + " book to non exist shopping cart")
    void addBookToShoppingCart_nonExistShoppingCart_thrownException() {

        EntityNotFoundException entityNotFoundException =
                org.junit.jupiter.api.Assertions.assertThrows(
                        EntityNotFoundException.class,
                        () -> shoppingCartServiceImpl.addBookToShoppingCart(
                                "nonExistEmail", new CreateCartItemDto(
                                        1L, 1)));

        String exceptionMessage = "Shopping cart not found";

        Assertions.assertThat(entityNotFoundException.getMessage())
                .isEqualTo(exceptionMessage);

    }

    @Test
    @DisplayName("Check, that exception is throwing when update"
            + " item of non exist shopping cart")
    void update_nonExistShoppingCartId_thrownException() {

        UpdateCartItemDto updateCartItemDto = new UpdateCartItemDto(
                5);
        EntityNotFoundException entityNotFoundException =
                org.junit.jupiter.api.Assertions.assertThrows(
                        EntityNotFoundException.class,
                        () -> shoppingCartServiceImpl.update(
                                "user2@email2.com", 5L,
                                updateCartItemDto));

        String exceptionMessage = "Item not found";

        Assertions.assertThat(entityNotFoundException.getMessage())
                .isEqualTo(exceptionMessage);

    }

}
