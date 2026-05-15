package com.springboot.my_web_project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.springboot.my_web_project.common.interceptor.JwtInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	/* 
	 * 필드주입(DI): 객체생성 후에 누군가 실수로 값을 바꿀 수 있는 열린 구조가 될 수 있다.
	 * 필드에 final을 붙일 수 없다
	 * @Autowired
	 * private JwtInterceptor jwtInterceptor;
	 */
	
	private final JwtInterceptor jwtInterceptor;
	/*
	 * 생성자주입(CI): 객체가 한 번 생성되면 의존성이 절대 바뀔 수 없음을 보장
	 * 필드에 final을 붙일 수 있다 객체의 불변성
	 */
	public WebConfig(JwtInterceptor jwtInterceptor) {
		this.jwtInterceptor = jwtInterceptor;
	}

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor) //문지기 배치
                .addPathPatterns("/api/**") //검사할 주소 범위
                .excludePathPatterns(
                		"/api/member/login",
                		"/api/member/refresh",
                		"/api/member/join",
                		"/api/member/dupIdCheck"); //검사제외것들
    }
    
    //비밀번호 암호화 설정
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
}
