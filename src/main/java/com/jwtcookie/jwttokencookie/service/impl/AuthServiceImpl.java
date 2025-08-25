package com.jwtcookie.jwttokencookie.service.impl;

import com.jwtcookie.jwttokencookie.dto.LoginRequest;
import com.jwtcookie.jwttokencookie.dto.LoginResponse;
import com.jwtcookie.jwttokencookie.dto.UserLoggedDto;
import com.jwtcookie.jwttokencookie.exception.AppException;
import com.jwtcookie.jwttokencookie.exception.ErrorDetails;
import com.jwtcookie.jwttokencookie.exception.ResourceNotFoundException;
import com.jwtcookie.jwttokencookie.mapper.UserMapper;
import com.jwtcookie.jwttokencookie.model.Token;
import com.jwtcookie.jwttokencookie.model.User;
import com.jwtcookie.jwttokencookie.repository.TokenRepository;
import com.jwtcookie.jwttokencookie.repository.UserRepository;
import com.jwtcookie.jwttokencookie.service.AuthService;
import com.jwtcookie.jwttokencookie.service.JwtTokenProvider;
import com.jwtcookie.jwttokencookie.util.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.jwtcookie.jwttokencookie.util.Constants.TOKEN_HEADER;
import static com.jwtcookie.jwttokencookie.util.Constants.TOKEN_PREFIX;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	
    @Value("${JWT_ACCESS_TOKEN_DURATION_MINUTE}")
    private long accessTokenDurationMinute;
    @Value("${JWT_ACCESS_TOKEN_DURATION_SECOND}")
    private long accessTokenDurationSecond;
    @Value("${JWT_REFRESH_TOKEN_DURATION_DAY}")
    private long refreshTokenDurationDay;
    @Value("${JWT_REFRESH_TOKEN_DURATION_SECOND}")
    private long refreshTokenDurationSecond;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final CookieUtil cookieUtil;
    private final AuthenticationManager authenticationManager;
    @Autowired
    private HttpServletRequest request; // request có chức năng lấy accessToken và getRequestUri để làm msg path return error
    
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
    	// 01. Validate by SpringSecurity system
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    loginRequest.username(), loginRequest.password()
            )
        );
        
        // 02. Find user
        String username = loginRequest.username();
        User user = userRepository.findByUsername(username).orElseThrow(
        	() -> new ResourceNotFoundException("User not found")
        );
        
        String ipHeader = request.getHeader("X-Forwarded-For");
        String ipResolved = (ipHeader == null || ipHeader.isEmpty()) 
                ? request.getRemoteAddr() 
                : ipHeader;
        String osDetected;
        String userAgent = request.getHeader("User-Agent");

        if (userAgent != null) {
            String ua = userAgent.toLowerCase();
            if (ua.contains("windows")) osDetected = "Windows";
            else if (ua.contains("mac")) osDetected = "MacOS";
            else if (ua.contains("x11")) osDetected = "Unix";
            else if (ua.contains("android")) osDetected = "Android";
            else if (ua.contains("iphone")) osDetected = "iOS";
            else osDetected = "Unknown";
        } else {
            osDetected = "Unknown";
        }
        System.out.printf("IP: %s%nUser-Agent: %s%nOS: %s%n", ipResolved, userAgent, osDetected);
        
        // 03. (skip).
        //
        // Validate location by ip-api.com (case hacker hacked username/password) -> Send OTP, email verification, MFA,...
        // Ko có refreshToken nào dưới DB ==  lần đầu đăng nhập -> bypass
        //
        //
        
        /*
         * Đăng nhập
         * -- Biết username, password
         * -- Login phải cấp được refresh token mới nếu refresh token cũ hết hạn
         * -- Login phải cấp được access token mới hoặc throw nếu phát hiện đăng nhập bất thường
         * 
         * TH1: Đăng nhập trên thiết bị khác:
         * 		- Lấy ra valid refresh tokens (nếu có) để kiểm tra
         * 		- So sánh oldOS_oldIP của token vs newOS_newIP của request
         * 		- Nếu khác -> { send verify email; từ chối đăng nhập }
         * 
         * TH2: Null accessToken, expired accessToken, invalid accessToken (Lần đầu đăng nhập)
         * 			-> Tạo ra accessToken mới
         * 		
         * TH3: Valid accessToken (có accessToken trong request)
         * 		- Kiểm tra user-agent từ valid refresh token
         * 		- TH: Trùng ip&os (spam) -> vẫn giữ accessToken cũ return
         * 
         * TH4: Kiểm tra user có refresh token nào valid không
         * 		- Nếu không có refresh token nào valid
         * 			-> Tạo & lưu refresh token mới hoặc update
         * 		- Nếu có refresh token valid (skip if)
         * 
         * 
         * 
         */
        
        Set<Token> refreshTokens = user.getTokens(); // Lấy ra User-Agent info của user
        if(!refreshTokens.isEmpty()) {// Nếu có refresh token còn hiệu lực (chưa expired & chưa disaled) -> 
        	refreshTokens.forEach(refreshToken -> {
        		if(refreshToken.getExpiryDate().isAfter(LocalDateTime.now()) && !refreshToken.isDisabled()) { // Chưa hết hạn và chưa bị disabled
        			String oldOS = refreshToken.getDevice();
        			String oldIP = refreshToken.getIp();
        	        if((oldOS != osDetected) && (oldIP != ipResolved)) { // Send email verificatio new location & alert
        	        	// Send email here
//        	        	throw new AppException(HttpStatus.BAD_REQUEST, "ALERT! We found you have abnormality login while another is in session with this account!  If you're true owner. Please check your email to verify!");
        	        	System.out.println("ALERT! We found you have abnormality login while another is in session with this account!  If you're true owner. Please check your email to verify!");
        	        }
        		}
            });	
        }
        
        // 03. Validate access token valid or not
        HttpHeaders responseHeaders = new HttpHeaders();
        String accessToken = getAccessTokenFromRequest();
        boolean accessTokenValid = tokenProvider.validateToken(accessToken);
        if(!accessTokenValid) { // Invalid access token
        	System.out.println("Null or invalid access token. Let's login normally!");
        	Token newAccessToken = tokenProvider.generateAccessToken(
                Map.of("role", user.getRole().getAuthority()),
                accessTokenDurationMinute,
                ChronoUnit.MINUTES,
                user
            );
        	addAccessTokenCookie(responseHeaders, newAccessToken);
        } else { // Valid access token
        	System.out.println("Warn! Valid token. Let's check is spam login!");
            if(!refreshTokens.isEmpty()) {// Nếu có refresh token còn hiệu lực (chưa expired & chưa disaled) -> 
            	refreshTokens.forEach(refreshToken -> {
            		if(refreshToken.getExpiryDate().isAfter(LocalDateTime.now()) && !refreshToken.isDisabled()) { // Chưa hết hạn và chưa bị disabled
            			String oldOS = refreshToken.getDevice();
            			String oldIP = refreshToken.getIp();
            	        if((oldOS == osDetected) && (oldIP == ipResolved)) { // Spam login, có thể cảnh báo hoặc ko làm gì cả vì access token vẫn còn sử dụng dc
            	        	System.out.println("Found user " + user.getUsername() +" is spaming login.");
            	        }
            		}
                });	
            }
        }
        
        // 04. Validate refresh token is expired
        boolean isHaveValidRefreshToken = refreshTokens.stream()
        	    .anyMatch(token -> token.getExpiryDate().isAfter(LocalDateTime.now()) && !token.isDisabled());

        if(!isHaveValidRefreshToken) { // Nếu ko có valid token nào
        	// Xóa token cũ, save token mới
        	tokenRepository.deleteAllByUserId(user.getId());
        	Token newRefreshToken = tokenProvider.generateRefreshToken (
                refreshTokenDurationDay,
                ChronoUnit.DAYS,
                user
            );
        	newRefreshToken.setUser(user);
        	newRefreshToken.setDevice(userAgent);
        	newRefreshToken.setIp(ipResolved);
        	tokenRepository.save(newRefreshToken);
        }
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginResponse loginResponse = new LoginResponse(true, user.getRole().getName());
        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
    }
    
    @Override
    public ResponseEntity<?> refresh() {
    	try {
    		String accessToken = getAccessTokenFromRequest();
			boolean accessTokenValid = tokenProvider.validateToken(accessToken);
			if(!accessTokenValid) {
				throw new AppException(HttpStatus.NOT_ACCEPTABLE, "Invalid access token!");
			}
			String username = tokenProvider.getUsernameFromToken(accessToken);
			User user = userRepository.findByUsername(username).orElse(null);
			Set<Token> refreshTokens = user.getTokens();
			if(!refreshTokens.isEmpty()) {// Nếu có refresh token còn hiệu lực (chưa expired & chưa disaled) ->
				for (Token refreshToken : refreshTokens) {
					if((refreshToken.getExpiryDate().isAfter(LocalDateTime.now())) && (!refreshToken.isDisabled())) { // Chưa hết hạn và chưa bị disabled
						Token newAccessToken = tokenProvider.generateAccessToken(Map.of("role", user.getRole().getAuthority()), accessTokenDurationMinute, ChronoUnit.MINUTES, user);
						HttpHeaders responseHeaders = new HttpHeaders();
				        addAccessTokenCookie(responseHeaders, newAccessToken);
				        LoginResponse loginResponse = new LoginResponse(true, user.getRole().getName());
				        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
					}
				}
			}
			ErrorDetails error = new ErrorDetails(
	                LocalDateTime.now(),
	                HttpStatus.BAD_REQUEST.value(),
	                "NOT FOUND RESOURCES",
	                "Not found any valid refresh token with " + username,
	                request.getRequestURI()
	        );
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
    }
    
    @Override
    public ResponseEntity<LoginResponse> logout() {
        SecurityContextHolder.clearContext();
        String accessToken = getAccessTokenFromRequest();
        String username = tokenProvider.getUsernameFromToken(accessToken);
        User user = userRepository.findByUsername(username).orElseThrow(
        	() -> new ResourceNotFoundException("User not found")
        );
        tokenRepository.deleteAllByUserId(user.getId());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.deleteAccessTokenCookie().toString());
        LoginResponse loginResponse = new LoginResponse(false, null);
        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
    }
    
    @Override
    public UserLoggedDto getUserLoggedInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof AnonymousAuthenticationToken)
            throw new AppException(HttpStatus.UNAUTHORIZED, "No user authenticated");
        String username = authentication.getName();
        System.out.println("username=" + username);
        User user = userRepository.findByUsername(username).orElseThrow(
        	() -> new ResourceNotFoundException("User not found")
        );
        return UserMapper.userToUserLoggedDto(user);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(token.getValue(), accessTokenDurationSecond).toString());
    }
    private String getAccessTokenFromRequest() {
    	if (request == null) {
            return null;
        }
        // 1. Check cookie trước
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String authHeader = request.getHeader(TOKEN_HEADER); // 2. Nếu không có trong cookie thì check header Authorization
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        // 3. Có thể thêm check query param nếu muốn (optional)
        String tokenParam = request.getParameter("access_token");
        if (tokenParam != null && !tokenParam.isBlank()) {
            return tokenParam;
        }
        return null;
    }
}
