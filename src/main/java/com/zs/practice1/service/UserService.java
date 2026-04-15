package com.zs.practice1.service;

import com.zs.practice1.authentication.JwtService;
import com.zs.practice1.dao.UserJpaRepository;
import com.zs.practice1.model.AuthRequest;
import com.zs.practice1.model.User;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * The type User service.
 */
@Service
@CacheConfig(cacheNames = "users")
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Instantiates a new User service.
     *
     * @param userJpaRepository the user jpa repository
     * @param passwordEncoder   the password encoder
     * @param jwtService        the jwt service
     */
    public UserService(UserJpaRepository userJpaRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Add user user.
     *
     * @param user the user
     * @return the user
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Observed(name = "user.service", contextualName = "Register User")
    public User registerUser(User user) {

        if (userJpaRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userJpaRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation while saving user: {}", ex.getMessage());
            throw new IllegalStateException("Unable to save user due to data integrity constraints", ex);
        }
        return user;
    }

    /**
     * Login user string.
     *
     * @param request the request
     * @return the string
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Observed(name = "user.service", contextualName = "Login User")
    public String loginUser(AuthRequest request) {
        log.info("Login request received for username: {}", request.username());

        User user = userJpaRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        String jwtToken;
        try {
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities(user.getRole())
                    .build();

            jwtToken = jwtService.generateToken(userDetails);
            log.info("User Login successful");
        } catch (RuntimeException ex) {
            log.error("JWT token generation failed for username: {}", request.username(), ex);
            throw new IllegalStateException("Failed to create JWT token", ex);
        }
        return jwtToken;
    }

    /**
     * Get all users list.
     *
     * @return the list
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Cacheable(key = "'all'", unless = "#result == null")
    @Observed(name = "user.service", contextualName = "Fetch All Users")
    public List<User> getAllUsers() {
        List<User> users;
        try {
            users = userJpaRepository.findAll();
        } catch (Exception ex) {
            log.error("Error retrieving all users: {}", ex.getMessage());
            throw new RuntimeException("Failed to fetch all users.", ex);
        }
        return users;
    }

    /**
     * Gets user by username.
     *
     * @param username the username
     * @return the user by username
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Cacheable(key = "#username", unless = "#result == null")
    @Observed(name = "user.service", contextualName = "Fetch User By Username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
    }
}
