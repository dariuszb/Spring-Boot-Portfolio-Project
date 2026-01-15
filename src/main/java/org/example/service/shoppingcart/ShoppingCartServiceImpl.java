package org.example.service.shoppingcart;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.dto.cartitem.CartItemDto;
import org.example.dto.cartitem.CreateCartItemDto;
import org.example.dto.cartitem.UpdateCartItemDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.CartItemMapper;
import org.example.mappers.ShoppingCartMapper;
import org.example.model.Book;
import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.example.model.User;
import org.example.repository.book.BookRepository;
import org.example.repository.cartitem.CartItemRepository;
import org.example.repository.shoppingcartrepository.ShoppingCartRepository;
import org.example.repository.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto get() {

        ShoppingCart shoppingCartRepositoryByEmail =
                shoppingCartRepository.findByUser(getPrincipalsOfUser());

        return mapToDto(shoppingCartRepositoryByEmail);

    }

    @Override
    public ShoppingCartDto addBookToShoppingCart(CreateCartItemDto createCartItemDto) {

        ShoppingCart userShoppingCart = shoppingCartRepository
                .findByUserId(getUserIdByAuthentication())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found"));

        Set<CartItem> cartItems = userShoppingCart.getCartItems();
        for (CartItem cartItem : cartItems) {
            if (Objects.equals(cartItem.getBook().getId(), createCartItemDto.bookId())) {
                cartItem.setQuantity(cartItem.getQuantity() + createCartItemDto.quantity());
                cartItemRepository.save(cartItem);
                shoppingCartRepository.save(userShoppingCart);
                return get();
            }
        }

        ShoppingCart saved = addNewCartItemToShoppingCart(
                cartItems, userShoppingCart, createCartItemDto);
        shoppingCartRepository.save(saved);

        return get();

    }

    @Transactional
    @Override
    public ShoppingCartDto update(@Positive Long id, UpdateCartItemDto updateCartItemDto) {
        isCartItemExist(id);
        ShoppingCart userShoppingCart = getShoppingCartByItemId(id);
        Set<CartItem> cartItems = userShoppingCart.getCartItems();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getId().equals(id)) {
                cartItem.setQuantity(updateCartItemDto.quantity());
                cartItemRepository.save(cartItem);
                shoppingCartRepository.save(userShoppingCart);
            }
        }

        return get();
    }

    @Transactional
    @Override
    public ShoppingCartDto deleteItemById(Long cartItemId) {
        isCartItemExist(cartItemId);
        ShoppingCart shoppingCartByItemId = getShoppingCartByItemId(cartItemId);
        shoppingCartByItemId.getCartItems().remove(cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Item not found"
                )));
        shoppingCartRepository.save(shoppingCartByItemId);
        return get();
    }

    private void isCartItemExist(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new EntityNotFoundException("CartItem not found");
        }
    }

    private ShoppingCart getShoppingCartByItemId(Long id) {
        CartItem itemToUpdate = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Item not found"));
        Long userId = itemToUpdate.getShoppingCart().getUser().getId();
        return shoppingCartRepository
                .findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found"));
    }

    private ShoppingCart addNewCartItemToShoppingCart(
            Set<CartItem> cartItems,
            ShoppingCart shoppingCart, CreateCartItemDto createCartItemDto) {

        CartItem itemEntity = new CartItem();
        itemEntity.setShoppingCart(shoppingCart);
        Book chosenBook = bookRepository.findById(
                createCartItemDto.bookId()).orElseThrow(
                    () -> new NoSuchElementException("Book not found")
        );

        itemEntity.setBook(chosenBook);
        itemEntity.setQuantity(createCartItemDto.quantity());
        cartItems.add(itemEntity);
        return cartItemRepository.save(itemEntity).getShoppingCart();

    }

    private Long getUserIdByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found"
                ));
        return user.getId();
    }

    private User getPrincipalsOfUser() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String userName = authentication.getName();
        return userRepository.findByEmail(userName)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

    }

    private ShoppingCartDto mapToDto(ShoppingCart shoppingCart) {
        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toDto(shoppingCart);
        Set<CartItemDto> itemsSetDto = cartItemMapper.map(
                shoppingCart.getCartItems());
        Set<CartItemDto> orderById = itemsSetDto
                .stream()
                .sorted(Comparator.comparing(CartItemDto::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        shoppingCartDto.setCartItems(orderById);
        return shoppingCartDto;

    }

}
