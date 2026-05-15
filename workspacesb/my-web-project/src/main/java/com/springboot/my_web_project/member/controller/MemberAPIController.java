package com.springboot.my_web_project.member.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.my_web_project.common.util.JwtUtil;
import com.springboot.my_web_project.entity.Member;
import com.springboot.my_web_project.member.service.MemberService;
import com.springboot.my_web_project.member.vo.MemberVO;

@RestController
public class MemberAPIController {

	/* 
	 * 필드주입(DI): 객체생성 후에 누군가 실수로 값을 바꿀 수 있는 열린 구조가 될 수 있다.
	 * 필드에 final을 붙일 수 없다
	 * @Autowired private JwtUtil jwtUtil;
	 */
	private final JwtUtil jwtUtil;
	private final MemberService memberService;
	
	/*
	 * 생성자주입(CI): 객체가 한 번 생성되면 의존성이 절대 바뀔 수 없음을 보장
	 * 필드에 final을 붙일 수 있다 객체의 불변성
	 */
	public MemberAPIController(JwtUtil jwtUtil, MemberService memberService) {
		this.jwtUtil = jwtUtil;
		this.memberService = memberService;
	}
	
	/*
	 * 로그인
	 * 성공과 실패가 아주 명확하게 갈리는 작업은 ResponseEntity사용 
	 * 데이터(Map 등)뿐만 아니라 상태 코드까지 보내는 도구임.
	 * 결론 데이터 + 상태 코드 전달용
	 */
	@PostMapping("/api/member/login") //login 입력데이터 안전
	public ResponseEntity<?> loginApi(@RequestBody MemberVO memberVO) {
		
		//DB 조회(LoginService 사용)
		Optional<Member> memberOpt = memberService.login(memberVO.getUserId(), memberVO.getPassword());
		
		if(memberOpt.isPresent()) {
			Member member = memberOpt.get();
			String userId = member.getUserId();
			
			//토큰발행
			String accessToken = jwtUtil.generateAccessToken(userId);
			String refreshToken = jwtUtil.generateRefreshToken(userId);
			
			//DB에 RefreshToken 저장
			memberService.saveRefreshToken(userId, refreshToken);
			
			//JSON 응답
			Map<String, Object> res = new HashMap<>();
			res.put("accessToken", accessToken);
			res.put("refreshToken", refreshToken); // 실제로는 HttpOnly 쿠키에 넣는걸 추천하지만 일단 전달
			res.put("message", member.getUserName() + "님, 로그인 되었습니다.");
			
			//로그인 성공시
			return ResponseEntity.ok(res); 
		}else {
			//로그인 실패시
			return ResponseEntity.status(401).body("아이디 또는 비밀번호가 틀렸습니다.");
		}
	}
	
	//로그인 유지 만료시 토큰 재발급
	@PostMapping("/api/member/refresh")
	public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
		//클라이언트(브라우저)가 보낸 JSON에서 두 토큰을 꺼냄
		String expiredAccessToken = request.get("accessToken"); //만료된 헌 토큰
	    String refreshToken = request.get("refreshToken");		//재발급시 필요 토큰

	    //Refresh Token이 유효한지 검사
	    if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
	        
	    	//만료된 헌 토큰에서 아이디를 알아냄(만료 후 재발급하기위해)
	    	String userId = jwtUtil.getUserIdFromExpiredToken(expiredAccessToken);
	        
	        //토큰 이 같으면 true || 없거나 다르면 false
	        boolean isMatched = memberService.checkRefreshToken(userId, refreshToken);
	        
	        if(isMatched) {
	        	//일치하면 새 AccessToken 발행
	        	String newAccessToken = jwtUtil.generateAccessToken(userId);
	        	Map<String, String> res = new HashMap<>();
		        res.put("accessToken", newAccessToken);
		        return ResponseEntity.ok(res);
	        }
	    }

	    //Refresh Token도 만료되었거나 가짜라면 다시 로그인 시킴
	    return ResponseEntity.status(401).body("세션이 만료되었습니다. 다시 로그인해주세요.");
	}
	
	//ID 중복 체크
	@PostMapping("/api/member/dupIdCheck")
	public ResponseEntity<?> dupIdCheck(@RequestBody Map<String, String> req) {
		String userId = req.get("userId");
		
		//아이디가 넘어오지 않았거나 공백이면 에러 반환
		if(userId == null || userId.trim().isEmpty()) {
			return ResponseEntity.badRequest().body("아이디를 입력해주세요.");
		}
		
		//중복여부 확인
		boolean isDuplicated = memberService.isIdDuplicated(userId.trim().toLowerCase());
		
		if(isDuplicated) {
			//중복된 ID가 존재하면 에러 반환
			return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
		}
		
		//중복아니면 success
		return ResponseEntity.ok().build();
	}
	
}
