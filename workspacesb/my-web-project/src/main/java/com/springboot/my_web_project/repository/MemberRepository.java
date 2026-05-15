package com.springboot.my_web_project.repository; 

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.my_web_project.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {

	//ID와 비밀번호로 회원을 찾는 메서드(로그인용) 현재 암호화 사용으로 사용하지 않는다.
	//Optional<Member> findByUserIdAndPassword(String userId, String password);
	
}
