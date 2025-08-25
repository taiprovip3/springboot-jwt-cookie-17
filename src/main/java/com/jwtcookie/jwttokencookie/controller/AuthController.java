package com.jwtcookie.jwttokencookie.controller;

import com.jwtcookie.jwttokencookie.dto.LoginRequest;
import com.jwtcookie.jwttokencookie.dto.LoginResponse;
import com.jwtcookie.jwttokencookie.dto.UserLoggedDto;
import com.jwtcookie.jwttokencookie.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken() {
        return authService.refresh();
    }
    
    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout() {
        return authService.logout();
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public ResponseEntity<UserLoggedDto> userLoggedInfo() {
        return ResponseEntity.ok(authService.getUserLoggedInfo());
    }
}
