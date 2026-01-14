package org.example.service.shoppingcart;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.dto.cartitem.CartItemDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.mappers.CartItemMapper;
import org.example.mappers.ShoppingCartMapper;
import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.example.model.User;
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
    private final UserRepository userRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto get() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String userName = authentication.getName();
        Optional<User> user = userRepository.findByEmail(userName);
        ShoppingCart shoppingCartRepositoryByEmail = shoppingCartRepository.findByUser(user);
        ShoppingCartDto dto = shoppingCartMapper.toDto(shoppingCartRepositoryByEmail);
        dto.setCartItems(setToDto(shoppingCartRepositoryByEmail.getCartItems()));
        return dto;

    }

    private Set<CartItemDto> setToDto(Set<CartItem> shoppingCarts) {
        return shoppingCarts.stream()
                .map(cartItemMapper::toDto)
                .sorted(Comparator.comparing(CartItemDto::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
