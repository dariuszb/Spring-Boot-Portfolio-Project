package org.example.service.cartitem;

import java.util.Optional;
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

        CartItem itemEntity = new CartItem();
        ShoppingCart userShoppingCart = shoppingCartRepository
                .findByUserId(getUserIdByAuthentication());
        userShoppingCart.getCartItems().add(itemEntity);
        itemEntity.setShoppingCart(userShoppingCart);

        Optional<Book> chosenBook = bookRepository.findById(
                createCartItemDto.bookId());

        itemEntity.setBook(chosenBook.get());
        itemEntity.setQuantity(createCartItemDto.quantity());
        CartItem saved = cartItemRepository.save(itemEntity);

        return cartItemMapper.toDto(saved);
    }

    @Override
    public CartItemDto update(Long id, CartItemDto cartItemDto) {
        isCartItemExist(id);
        return updateCartItem(id, cartItemDto);
        //wywołanie karty usera czy automatyczne usunięcie przez usunięcie item
    }

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

    private CartItemDto updateCartItem(Long id, CartItemDto cartItemDto) {
        CartItem item = cartItemRepository.findById(id).get();
        item.setBook(bookRepository.findById(cartItemDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found")));
        item.setQuantity(cartItemDto.getQuantity());
        cartItemRepository.save(item);
        return cartItemMapper.toDto(item);
    }

    private Long getUserIdByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String userName = authentication.getName();
        Optional<User> user = userRepository.findByEmail(userName);
        return user.get().getId();
    }
}
