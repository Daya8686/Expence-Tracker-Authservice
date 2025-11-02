package com.expencetracker.authservice.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refresh_token_id")
	private Long refreshTokenId;
	
	@Column(name = "refresh_token", nullable = false)
	private String refreshToken;
	
	@Column(name="refresh_token_expiration_date")
	private Instant expiryDate;
	
	@OneToOne
	@JoinColumn(name="user_id", referencedColumnName = "user_id")
	private UserInfo userInfo;
	

}
