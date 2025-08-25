package com.jwtcookie.jwttokencookie.repository;

import com.jwtcookie.jwttokencookie.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TokenRepository extends JpaRepository<Token, Long> {
	@Modifying
	@Transactional
	@Query("DELETE FROM Token t WHERE t.user.id = :userId")
	public void deleteAllByUserId(@Param("userId") Long userId);
}