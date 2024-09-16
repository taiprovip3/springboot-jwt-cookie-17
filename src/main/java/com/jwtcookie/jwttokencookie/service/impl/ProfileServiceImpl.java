package com.jwtcookie.jwttokencookie.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jwtcookie.jwttokencookie.entity.Profile;
import com.jwtcookie.jwttokencookie.model.User;
import com.jwtcookie.jwttokencookie.repository.ProfileRepository;
import com.jwtcookie.jwttokencookie.repository.UserRepository;
import com.jwtcookie.jwttokencookie.service.ProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
	
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Profile getUserProfile(String username) {
		User user = userRepository.findByUsername(username).orElse(null);
		return profileRepository.findByUser(user);
	}
	
}
