package org.example.service.shoppingcart;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.dto.cartitem.CartItemDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.CartItemMapper;
import org.example.mappers.ShoppingCartMapper;
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
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ShoppingCart shoppingCartRepositoryByEmail = shoppingCartRepository.findByUser(user);
        ShoppingCartDto dto = shoppingCartMapper.toDto(shoppingCartRepositoryByEmail);
        Set<CartItemDto> itemsSetDto = cartItemMapper.map(
                shoppingCartRepositoryByEmail.getCartItems());
        Set<CartItemDto> orderById = itemsSetDto.stream()
                .sorted(Comparator.comparing(CartItemDto::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        dto.setCartItems(orderById);
        return dto;

    }

}
