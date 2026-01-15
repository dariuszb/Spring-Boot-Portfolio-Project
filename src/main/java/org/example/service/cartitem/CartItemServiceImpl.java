package org.example.service.cartitem;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.dto.cartitem.CartItemDto;
import org.example.dto.cartitem.CreateCartItemDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.CartItemMapper;
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
public class CartItemServiceImpl implements CartItemService {

    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;

    @Override
    public CartItemDto addBookToShoppingCart(CreateCartItemDto createCartItemDto) {

        ShoppingCart userShoppingCart = shoppingCartRepository
                .findByUserId(getUserIdByAuthentication())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found"));

        Set<CartItem> cartItems = userShoppingCart.getCartItems();
        for (CartItem cartItem : cartItems) {
            if (Objects.equals(cartItem.getBook().getId(), createCartItemDto.bookId())) {
                cartItem.setQuantity(cartItem.getQuantity() + createCartItemDto.quantity());
                CartItem overrideExistQuantity = cartItemRepository.save(cartItem);
                return cartItemMapper.toDto(overrideExistQuantity);
            }
        }

        CartItem saved = addNewCartItemToShoppingCart(
                cartItems, userShoppingCart, createCartItemDto);

        return cartItemMapper.toDto(saved);

    }

    @Transactional
    @Override
    public CartItemDto update(@Positive Long id, CreateCartItemDto createCartItemDto) {
        isCartItemExist(id);
        CartItem itemToUpdate = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Item not found"));
        Long userId = itemToUpdate.getShoppingCart().getUser().getId();
        ShoppingCart userShoppingCart = shoppingCartRepository
                .findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found"));
        Set<CartItem> cartItems = userShoppingCart.getCartItems();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getId().equals(id)) {
                cartItem.setQuantity(createCartItemDto.quantity());
                cartItemRepository.save(cartItem);
            }
        }
        CartItem updatedItem = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Item not found"));
        return cartItemMapper.toDto(updatedItem);
    }

    @Transactional
    @Override
    public void deleteItemById(Long cartItemId) {
        isCartItemExist(cartItemId);
        cartItemRepository.deleteById(cartItemId);
    }

    private void isCartItemExist(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new EntityNotFoundException("CartItem not found");
        }
    }

    private CartItem addNewCartItemToShoppingCart(
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

        return cartItemRepository.save(itemEntity);

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
}
