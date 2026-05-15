package com.springboot.my_web_project.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BOOT_MEMBER")
@Getter
@Setter
@NoArgsConstructor
public class Member extends BaseTimeEntity {

	@Id
	@Column(name = "USER_ID", length = 50)
	private String userId;
	
	@Column(nullable = false, length = 100)
	private String password;
	
	@Column(name = "USER_NAME", nullable = false, length = 50)
	private String userName;
	
	@Column(name = "TEL", nullable = false, length = 20)
    private String tel;
	
	@Column(name = "EMAIL", nullable = false, length = 100)
    private String email;
	
	/*
	시간 컬럼 공통으로 생성시/수정시 자동으로 입력되게 뺴둠.
	@Column(nullable = false)
	private LocalDateTime createdDt = LocalDateTime.now();
	
	private LocalDateTime updateDt;
	
	@PreUpdate
	public void onPreUpdate() {
		this.updateDt = LocalDateTime.now();
	}*/
}
