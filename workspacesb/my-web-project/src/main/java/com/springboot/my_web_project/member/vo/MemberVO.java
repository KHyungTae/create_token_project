package com.springboot.my_web_project.member.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberVO {

	//화면데이터 받을때만 사용하는 VO
	private String userId;
	private String userName;
	private String password;
	private String email;
	private String tel;
}
