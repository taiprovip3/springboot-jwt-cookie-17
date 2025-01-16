package com.jwtcookie.jwttokencookie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
	@NotBlank(message = "Username không được để trống!")
	@Size(min = 3, max = 20, message = "Username phải từ 3 đến 20 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username chỉ được chứa chữ cái, số, hoặc dấu gạch dưới")
	private String username;
	@NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
	@NotBlank(message = "Password không được để trống")
    @Size(min = 8, max = 50, message = "Password phải từ 8 đến 50 ký tự")
    private String password;
}
