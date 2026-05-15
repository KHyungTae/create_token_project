package com.springboot.my_web_project.main.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.my_web_project.member.service.MemberService;

@RestController
public class MainAPIController {

	/* 
	 * 필드주입(DI): 객체생성 후에 누군가 실수로 값을 바꿀 수 있는 열린 구조가 될 수 있다.
	 * 필드에 final을 붙일 수 없다
	 * @Autowired private JwtUtil jwtUtil;
	 */
	
	//private final JwtUtil jwtUtil;
	private final MemberService memberService;
	/*
	 * 생성자주입(CI): 객체가 한 번 생성되면 의존성이 절대 바뀔 수 없음을 보장
	 * 필드에 final을 붙일 수 있다 객체의 불변성
	 */
	public MainAPIController(MemberService memberService) {
		//this.jwtUtil = jwtUtil;
		this.memberService = memberService;
	}
	
	
	
	/*
	 * 이미 로그인된 사람만 들어오는 곳 성공과 실패가 아니므로 Map사용
	 * 빠르고 간편하게 JSON 결과물을 만들기 위해
	 * 결론 단순 데이터 전달용
	 */
	@PostMapping("/api/main")
	public Map<String, Object> mainApi(HttpServletRequest req) {
		
		//인터셉터가 담아준 "userId"을 꺼냅니다 (Object로 나오므로 형변환 필요)
		String userId = (String) req.getAttribute("userId");
		
		//DB에서 해당 ID의 회원 정보를 찾음(회원이름)
		String userName = memberService.findMemberName(userId);
		
		Map<String, Object> map = new HashMap<>();
		map.put("userName", userName);
		
		return map;
	}
}
