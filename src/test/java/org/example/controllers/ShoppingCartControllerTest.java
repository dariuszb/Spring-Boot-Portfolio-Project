package org.example.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Set;
import org.example.dto.cartitem.CartItemDto;
import org.example.dto.cartitem.CreateCartItemDto;
import org.example.dto.cartitem.UpdateCartItemDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.model.Book;
import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.example.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ShoppingCartControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Check, that authenticated user's shopping cart is returned")
    @WithMockUser(username = "user2@email2.com", roles = "USER")
    @Sql(scripts = "classpath:database/add-to-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear-all-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getShoppingCart_existAndAuthenticatedUser_returnProperShoppingCart() throws Exception {
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
        shoppingCart.setId(2L);
        shoppingCart.setCartItems(Set.of(cartItem));

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(1L);
        cartItemDto.setBookId(1L);
        cartItemDto.setBookTitle("First");
        cartItemDto.setQuantity(2);

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(2L);
        expected.setUserId(2L);
        expected.setCartItems(Set.of(cartItemDto));

        MvcResult result = mockMvc.perform(
                        get("/api/cart")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String httpResponse = result.getResponse().getContentAsString();

        org.example.dto.shoppingcart.ShoppingCartDto actual = objectMapper.readValue(
                httpResponse, new TypeReference<ShoppingCartDto>() {
                });

        org.assertj.core.api.Assertions.assertThat(expected)
                .isEqualTo(actual);

    }

    @Test
    @DisplayName("Check, that cart item is correct added to shopping cart")
    @WithMockUser(username = "user2@email2.com", roles = "USER")
    @Sql(scripts = "classpath:database/add-to-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear-all-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addCartItemToShoppingCart_correctInputValues_returnCartWithAddedItems() throws Exception {
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
        cartItemDto.setBookTitle("First");
        cartItemDto.setQuantity(6);

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(2L);
        expected.setUserId(2L);
        expected.setCartItems(Set.of(cartItemDto));

        CreateCartItemDto createCartItemDto = new CreateCartItemDto(
                1L, 4);

        String jsonRequest = objectMapper.writeValueAsString(createCartItemDto);

        MvcResult result = mockMvc.perform(
                        post("/api/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), ShoppingCartDto.class);

        org.assertj.core.api.Assertions.assertThat(expected)
                .isEqualTo(actual);

    }

    @Test
    @DisplayName("Verify correctness of updating cart item by "
            + "cart item id")
    @WithMockUser(username = "user2@email2.com", roles = "USER")
    @Sql(scripts = "classpath:database/add-to-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear-all-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCartItemById_correctInputArgument_returnCartWithUpdatedItem() throws Exception {
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
        cartItemDto.setBookTitle("First");
        cartItemDto.setQuantity(5);

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(2L);
        expected.setUserId(2L);
        expected.setCartItems(Set.of(cartItemDto));

        UpdateCartItemDto updateCartItemDto = new UpdateCartItemDto(5);

        String jsonRequest = objectMapper.writeValueAsString(updateCartItemDto);

        MvcResult result = mockMvc.perform(
                        put("/api/cart/cart-items/{cartItemId}",
                                cartItem.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), ShoppingCartDto.class);

        org.assertj.core.api.Assertions.assertThat(expected)
                .isEqualTo(actual);

    }

}
