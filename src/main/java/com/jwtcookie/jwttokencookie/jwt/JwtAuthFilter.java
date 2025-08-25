package com.jwtcookie.jwttokencookie.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jwtcookie.jwttokencookie.service.JwtTokenProvider;

import java.io.IOException;

import static com.jwtcookie.jwttokencookie.util.Constants.TOKEN_HEADER;
import static com.jwtcookie.jwttokencookie.util.Constants.TOKEN_PREFIX;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	
    @Value("${JWT_ACCESS_COOKIE_NAME}")
    private String accessTokenCookieName;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getAccessTokenFromRequest(request);
        
        if(accessToken == null || !tokenProvider.validateToken(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        // get username from token
        String username = tokenProvider.getUsernameFromToken(accessToken);
        if(username == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
    
    private String getAccessTokenFromRequest(HttpServletRequest request) {
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
        // 2. Get token từ request Authorization Bearer <token>
        String authHeader = request.getHeader(TOKEN_HEADER); // 2. Nếu không có trong cookie thì check header Authorization
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        // 3. Có thể thêm check query param nếu muốn (optional)
        String tokenParam = request.getParameter(accessTokenCookieName);
        if (tokenParam != null && !tokenParam.isBlank()) {
            return tokenParam;
        }
        return null;
    }
}
