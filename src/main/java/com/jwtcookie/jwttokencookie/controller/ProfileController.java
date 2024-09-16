package com.jwtcookie.jwttokencookie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwtcookie.jwttokencookie.jwt.JwtTokenProvider;
import com.jwtcookie.jwttokencookie.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@GetMapping("/")
    public ResponseEntity<?> getUserProfile(
    		@CookieValue(name = "access_token", required = false) String accessToken,
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
		
		if(!jwtTokenProvider.validateToken(accessToken) || !jwtTokenProvider.validateToken(refreshToken)) {
			return ResponseEntity.badRequest().body("Unauthorized cookie. Token was not found!");
		}
		String username = jwtTokenProvider.getUsernameFromToken(accessToken);
        return ResponseEntity.ok(profileService.getUserProfile(username));
    }
}
