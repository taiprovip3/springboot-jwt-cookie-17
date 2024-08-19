package com.jwtcookie.jwttokencookie.dto;

import java.io.Serializable;
import java.util.Set;
public record UserDto (
        Long id,
        String username,
        String password,
        String role,
        Set<String> premissions
)implements Serializable { }