package com.springboot.my_web_project.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.springboot.my_web_project.common.util.JwtUtil;

@Component
public class JwtInterceptor implements HandlerInterceptor {

	/* 
	 * 필드주입(DI): 객체생성 후에 누군가 실수로 값을 바꿀 수 있는 열린 구조가 될 수 있다.
	 * 필드에 final을 붙일 수 없다
	 * @Autowired private JwtUtil jwtUtil;
	 */
	
	private final JwtUtil jwtUtil;
	/*
	 * 생성자주입(CI): 객체가 한 번 생성되면 의존성이 절대 바뀔 수 없음을 보장
	 * 필드에 final을 붙일 수 있다 객체의 불변성
	 */
	public JwtInterceptor(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * OPTIONS 요청(Preflight) 
         * CORS(Cross-Origin Resource Sharing) 문제를 해결하기 위해 예비요청(Preflight Request)을 무조건 통과시키겠다는 코드
         * 즉, 통신 및 권한(CORS) 문제를 예비 요청을 허용하여 통신 길을 열어줌.
         */
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
        	return true;
        }

        //브라우저가 보낸 HTTP 요청 헤더(Authorization)에서 값을 꺼내온다
        String authHeader = request.getHeader("Authorization");

        //토큰 통과 절차
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) { //유효성 검사
            	//[추가]헤더에서 가져온 토큰에 userName을 꺼내서 request 바구니에 담는다
            	String userId = jwtUtil.getUserId(token);
            	request.setAttribute("userId", userId);
                return true; //토큰 통과
            }
        }

        //토큰 통과 못함
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        return false;
    }
}
