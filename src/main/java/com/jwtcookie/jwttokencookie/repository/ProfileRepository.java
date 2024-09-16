package com.jwtcookie.jwttokencookie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jwtcookie.jwttokencookie.entity.Profile;
import com.jwtcookie.jwttokencookie.model.User;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
	public Profile findByUserId(long id);

	public Profile findByUser(User user);
}
