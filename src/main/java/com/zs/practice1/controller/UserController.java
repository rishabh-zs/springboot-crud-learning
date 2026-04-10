package com.zs.practice1.controller;

import com.zs.practice1.model.AuthRequest;
import com.zs.practice1.model.User;
import com.zs.practice1.service.UserService;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Authentication API that issues JWT tokens.
 */
@RestController
@RequestMapping("/auth/user")
@Observed(name = "auth.user.controller", contextualName = "Auth User Controller")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    /**
     * Instantiates a new Auth controller.
     *
     * @param userService the user service
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handle add user response entity.
     *
     * @param user the user
     * @return the response entity
     */
    @PutMapping("/register")
    public ResponseEntity<Map<String, String>> handleRegister(@RequestBody User user) {
        log.debug("/auth/user/register endpoint was called with user: {}", user.getUsername());
        User newUser = userService.registerUser(user);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "User added successfully with username: " + user.getUsername());
        response.put("User", newUser.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> handleLogin(@Valid @RequestBody AuthRequest request) {
        log.debug("/auth/user/login endpoint was called for username: {}", request.username());
        String jwtToken = userService.loginUser(request);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "Authentication successful");
        response.put("token:", jwtToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Handle get user response entity.
     *
     * @return the response entity
     */
    @GetMapping("/getUsers")
    public ResponseEntity<Map<String, Object>> handleGetUser() {
        log.debug("/auth/user/getUsers endpoint was called");
        long start = System.currentTimeMillis();
        List<User> allUsers = userService.getAllUsers();
        long endTime = System.currentTimeMillis();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "All user fetched successfully");
        response.put("time", (endTime - start) + "ms");
        response.put("users:", allUsers);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

