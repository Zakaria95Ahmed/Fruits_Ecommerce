package com.fruits.ecommerce.services.implementations;

import com.fruits.ecommerce.configuration.SecurityConfig.SecurityCore.UserData;
import com.fruits.ecommerce.exceptions.exceptionsDomain.users.UserNotFoundException;
import com.fruits.ecommerce.exceptions.exceptionsDomain.products.CartNotFoundException;
import com.fruits.ecommerce.exceptions.global.ResourceNotFoundException;
import com.fruits.ecommerce.models.dtos.AddressDTO;
import com.fruits.ecommerce.models.dtos.CartDTO;
import com.fruits.ecommerce.models.entities.*;
import com.fruits.ecommerce.models.enums.RoleType;
import com.fruits.ecommerce.models.mappers.AddressMapper;
import com.fruits.ecommerce.models.mappers.CartMapper;
import com.fruits.ecommerce.repository.*;
import com.fruits.ecommerce.services.Interfaces.ICartService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service@Slf4j
@RequiredArgsConstructor
public class CartService implements ICartService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;
    private final RoleRepository roleRepository;
    private final CartMapper cartMapper;
    private final AddressMapper addressMapper;


    @Value("${app.shipping.cost}")
    private BigDecimal shippingCost;

    @Value("${app.discount.percentage}")
    private BigDecimal discountPercentage;
    @PostConstruct
    public void init() {
        log.info("Shipping Cost: {}", shippingCost);
        log.info("Discount Percentage: {}", discountPercentage);
    }


    @Override
    @Transactional
    public void addProductToCart(Authentication authentication, Long productId, int quantity) {
        User user = getUserFromAuthentication(authentication);
        log.info("User found: {}", user.getUsername());

        // Ensure user has the Customer role
        ensureCustomerRole(user);

        Customer customer = getOrCreateCustomer(user);
        log.info("Customer found/created with ID: {}", customer.getId());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        log.info("Product found with ID: {}", product.getId());

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> createNewCart(customer));
        log.info("Cart found/created with ID: {}", cart.getId());

        addOrUpdateCartItem(cart, product, quantity);
        cartRepository.save(cart);
        log.info("Cart saved successfully");
    }


    @Override
    public CartDTO getCartDetails(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Customer customer = getOrCreateCustomer(user);

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart newCart = createNewCart(customer);
                    log.info("Created new cart for customer: {}", customer.getId());
                    return newCart;
                });

        log.info("Retrieved cart: {}", cart);
        CartDTO cartDTO = cartMapper.toDTO(cart);
        log.debug("Mapped to DTO: {}", cartDTO);
        return cartDTO;
    }

    public CartDTO getCartForUser(String username) {
        Cart cart = cartRepository.findByCustomerUsername(username)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + username));

        return cartMapper.toDTO(cart);
    }
    private void ensureCustomerRole(User user) {
        Role customerRole = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Customer role not found"));

        if (!user.getRoles().contains(customerRole)) {
            user.getRoles().add(customerRole);
            userRepository.save(user);
            log.info("Assigned Customer role to user: {}", user.getUsername());
        }
    }


    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserData) {
            UserData userData = (UserData) authentication.getPrincipal();
            return userRepository.findById(userData.getId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userData.getId()));
        } else if (authentication.getPrincipal() instanceof String) {
            String usernameOrEmail = (String) authentication.getPrincipal();
            return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username or email: " + usernameOrEmail));
        } else {
            throw new IllegalStateException("Unexpected principal type: " + authentication.getPrincipal().getClass());
        }
    }
    private Customer getOrCreateCustomer(User user) {
        return customerRepository.findByUser(user)
                .orElseGet(() -> {
                    try {
                        Customer newCustomer = new Customer();
                        newCustomer.setUser(user);
                        // if necessary the other values
                        //  newCustomer.setId(user.getId());
                        Customer savedCustomer = customerRepository.save(newCustomer);
                        log.info("New customer created with ID: {}", savedCustomer.getId());
                        return savedCustomer;
                    } catch (Exception e) {
                        log.error("Error creating new customer for user: {}", user.getUsername(), e);
                        throw new RuntimeException("Unable to create new customer", e);
                    }
                });
    }

    private Cart createNewCart(Customer customer) {
        if (customer.getId() == null) {
            log.error("Cannot create cart: Customer ID is null for customer: {}", customer);
            throw new IllegalStateException("Customer must have a valid ID before creating a cart");
        }
        Cart newCart = new Cart();
        newCart.setCustomer(customer);
        newCart.setShippingCost(shippingCost);
        newCart.setDiscount(discountPercentage);
        newCart = cartRepository.save(newCart);
        log.info("New cart created with ID: {} for customer ID: {}", newCart.getId(), customer.getId());
        return newCart;
    }
    private void addOrUpdateCartItem(Cart cart, Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        CartItemKey key = new CartItemKey(cart.getId(), product.getId());

        try {
            CartItem cartItem = cartItemRepository.findById(key)
                    .orElseGet(() -> {
                        CartItem newItem = new CartItem();
                        newItem.setCart(cart);
                        newItem.setProduct(product);
                        newItem.setId(key);
                        newItem.setQuantity(0); // Initialize quantity to 0
                        return newItem;
                    });

            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);

            log.debug("Cart item updated for product ID: {}, New quantity: {}", product.getId(), cartItem.getQuantity());
        } catch (Exception e) {
            log.error("Error updating cart item for product ID: {}", product.getId(), e);
            throw new RuntimeException("Failed to update cart item", e);
        }
    }

    @Override
    @Transactional
    public void updateCustomerAddresses(Authentication authentication, AddressDTO billingAddress, AddressDTO shippingAddress) {
        User user = getUserFromAuthentication(authentication);
        Customer customer = getOrCreateCustomer(user);

        if (billingAddress != null) {
            Address billingAddressEntity = addressMapper.toEntity(billingAddress);
            customer.setBillingAddress(billingAddressEntity);
        }

        if (shippingAddress != null) {
            Address shippingAddressEntity = addressMapper.toEntity(shippingAddress);
            customer.setShippingAddress(shippingAddressEntity);
        }

        customerRepository.save(customer);
        log.info("Updated addresses for customer ID: {}", customer.getId());
    }


}



