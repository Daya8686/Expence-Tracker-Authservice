package com.expencetracker.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expencetracker.authservice.entities.RefreshToken;
import com.expencetracker.authservice.entities.UserInfo;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
	
	Optional<RefreshToken> findByRefreshToken(String refreshToken);

	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.userInfo = :userInfo")
	int deleteByUserInfo(@Param("userInfo") UserInfo userInfo);

}
