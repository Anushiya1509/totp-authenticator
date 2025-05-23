package com.anushiya.totp.controller;

import com.anushiya.totp.dto.LoginRequest;
import com.anushiya.totp.dto.RegisterRequest;
import com.anushiya.totp.security.JwtTokenProvider;
import com.anushiya.totp.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        logger.info("Registering user: {}", request.getUsername());
        try {
            authService.register(request);
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration failed for user {}: {}", request.getUsername(), e.getMessage(), e);
            response.put("error", "Registration failed");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        logger.info("Login attempt for user: {}", request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(request.getUsername());
            response.put("token", token);
            response.put("message", "Login successful");
            logger.info("Login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            logger.warn("Invalid credentials for user: {}", request.getUsername());
            response.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception ex) {
            logger.error("Login failed for user {}: {}", request.getUsername(), ex.getMessage(), ex);
            response.put("error", "Login failed");
            response.put("details", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        SecurityContextHolder.clearContext();
        logger.info("Logout successful");
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
}