package com.springboot.my_web_project.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BOOT_REFRESH_TOKEN")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "USER_ID", nullable = false, length = 50)
	private String userId;
	
	@Column(name = "TOKEN_VALUE", nullable = false, length = 500)
	private String tokenValue;
	
	@Column(name = "EXPIRY_DATE", nullable = false)
	private LocalDateTime expiryDate; //토큰이 만료되는 정확한 미래 시각
}
