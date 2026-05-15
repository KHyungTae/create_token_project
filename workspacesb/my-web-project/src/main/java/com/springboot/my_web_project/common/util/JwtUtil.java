package com.springboot.my_web_project.common.util;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
//@RequiredArgsConstructor
public class JwtUtil {

	//비밀키 보관 변수
	private final Key key;
	//acessToken 만료시간(유효기간) 보관 변수
	private final long accessTokenExp;
	//refreshToken 만료시간(유효기간) 보관 변수
	private final long refreshTokenExp;

	//객체를 만드는 동시에 설정값을 생성자 파라미터로 던져준다. 객체가 생성됨과 동시에 key가 완성됨.
	public JwtUtil(@Value("${jwt.secret}") String secretKey,
				   @Value("${jwt.expiration}") long accessTokenExp,
				   @Value("${jwt.refresh-expiration}") long refreshTokenExp) {
		//HS256 알고리즘에 적합한 형태의 key객체 변환 32자 256비트
	    this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
	    this.accessTokenExp = accessTokenExp;
	    this.refreshTokenExp = refreshTokenExp;
	}
	
	//Access Token 생성
	public String generateAccessToken(String userId) {
		return createToken(userId, accessTokenExp);
	}
	
	//Refresh Token 생성
	public String generateRefreshToken(String userId) {
		return createToken(userId, refreshTokenExp);
	}
	
	/*
	 * 토큰 생성
	 * 로그인 성공할 때마다 실행됨. 
	 * (위에 key가 이미 서버실행시 딱한번 만들어지면 만들어진 key도장을 꺼내서 찍기만한다.)
	 */
	public String createToken(String userId, long exp) {
		return Jwts.builder()
				.setSubject(userId) //페이로드: 로그인 정보
				.setIssuedAt(new Date()) //페이로드: 발행 시간
				.setExpiration(new Date(System.currentTimeMillis() + exp)) //페이로드: 만료시간
				.signWith(key, SignatureAlgorithm.HS256) // 서명: HS256 알고리즘 적용
				.compact(); // 압축: 최종 문자열로 변환
	}
	
	/*
	 * Claims(데이터 덩어리) 추출 - 공통 메서드
	 * private:내부 클래스 안에서 다른 메서드들이 가져다 사용
	 */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) //비밀키준비
                .build()
                .parseClaimsJws(token) //맞는지 확인 후 파싱
                .getBody(); //실제로 저장한데이터(Claims)부분만 뺴옴
    }

    //토큰에서 사용자 아이디 추출 (로그인상태 유요할때 정상 통행용, 살아있는 토큰사용)
    public String getUserId(String token) {
        return getAllClaims(token).getSubject();
    }

    //토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            getAllClaims(token); // 파싱 성공 시 유효
            return true;
        } catch (Exception e) {
            return false; // 만료되었거나 변조된 경우
        }
    }
    
    //만료된 토큰에서도 데이터를 꺼낼 수 있는 메서드 (재발급용, 만료된 토큰사용)
    public String getUserIdFromExpiredToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject(); // 만료되었어도 에러 객체에서 데이터를 꺼냄
        }
    }
}
