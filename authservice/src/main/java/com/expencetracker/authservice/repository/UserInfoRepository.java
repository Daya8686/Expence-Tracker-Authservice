package com.expencetracker.authservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expencetracker.authservice.entities.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UUID>{

	public Optional<UserInfo> findByUsername(String username);
	
	public boolean existsByUsername(String username);
	
	public boolean existsByEmail(String email);
	
	public boolean existsByMobileNo(String mobileNo);

	public boolean existsByUsernameOrEmailOrMobileNo(String username, String email, String mobileNo);

	@Query("SELECT u FROM UserInfo u WHERE u.username = :input OR u.email = :input")
	public Optional<UserInfo> findByUsernameOrEmail(@Param("input") String input);

}
