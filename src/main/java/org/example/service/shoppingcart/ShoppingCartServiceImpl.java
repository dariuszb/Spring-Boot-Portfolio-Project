package org.example.service.shoppingcart;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.dto.cartitem.CreateCartItemDto;
import org.example.dto.cartitem.UpdateCartItemDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.ShoppingCartMapper;
import org.example.model.Book;
import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.example.repository.book.BookRepository;
import org.example.repository.cartitem.CartItemRepository;
import org.example.repository.shoppingcart.ShoppingCartRepository;
import org.springframework.security.access.AccessDeniedException;
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

    @Override
    public ShoppingCartDto get() {

        ShoppingCart userShoppingCart =
                shoppingCartRepository.findByUserEmail(getUserNameByAuthentication())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "ShoppingCart not found"
                        ));

        return shoppingCartMapper.toDto(userShoppingCart);

    }

    @Override
    public ShoppingCartDto addBookToShoppingCart(CreateCartItemDto createCartItemDto) {

        String userNameByAuthentication = getUserNameByAuthentication();
        ShoppingCart userShoppingCart = shoppingCartRepository
                .findByUserEmail(userNameByAuthentication)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found"));

        Optional<CartItem> cartItem = cartItemRepository
                .findItemByProperBookIdAndShoppingCartId(
                createCartItemDto.bookId(), userShoppingCart.getId());
        if (cartItem.isPresent()) {
            CartItem itemToSetQuantity = cartItem.get();
            itemToSetQuantity.setQuantity(cartItem.get()
                    .getQuantity() + createCartItemDto.quantity());
            cartItemRepository.save(itemToSetQuantity);
            return shoppingCartMapper.toDto(userShoppingCart);
        }

        addNewCartItem(userShoppingCart, createCartItemDto);
        return shoppingCartMapper.toDto(userShoppingCart);
    }

    @Transactional
    @Override
    public ShoppingCartDto update(@Positive Long id, UpdateCartItemDto updateCartItemDto) {
        ShoppingCart userShoppingCart = getShoppingCartByItemId(id);

        if (userShoppingCart.getUser().getEmail().equals(getUserNameByAuthentication())) {
            CartItem cartItem = cartItemRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException(
                            "CartItem not found"));

            cartItem.setQuantity(updateCartItemDto.quantity());
            cartItemRepository.save(cartItem);
        } else {
            throw new AccessDeniedException("Access denied");
        }

        return shoppingCartMapper.toDto(userShoppingCart);
    }

    @Transactional
    @Override
    public void deleteItemById(Long cartItemId) {
        ShoppingCart shoppingCartByItemId = getShoppingCartByItemId(cartItemId);
        if (shoppingCartByItemId.getUser().getEmail().equals(getUserNameByAuthentication())) {
            CartItem item = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Item not found"
                    ));
            shoppingCartByItemId.getCartItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            throw new AccessDeniedException("Access denied");

        }
    }

    private ShoppingCart getShoppingCartByItemId(Long id) {
        CartItem itemToUpdate = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Item not found"));
        return itemToUpdate.getShoppingCart();
    }

    private void addNewCartItem(
            ShoppingCart shoppingCart, CreateCartItemDto createCartItemDto) {

        Book chosenBook = bookRepository.findById(
                createCartItemDto.bookId()).orElseThrow(
                    () -> new EntityNotFoundException("Book not found")
        );

        CartItem itemEntity = new CartItem();
        itemEntity.setShoppingCart(shoppingCart);
        itemEntity.setBook(chosenBook);
        itemEntity.setQuantity(createCartItemDto.quantity());

        shoppingCart.getCartItems().add(itemEntity);
        cartItemRepository.save(itemEntity);

    }

    private String getUserNameByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        return authentication.getName();
    }

}
