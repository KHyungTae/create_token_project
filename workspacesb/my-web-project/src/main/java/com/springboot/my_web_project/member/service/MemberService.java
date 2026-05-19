package com.springboot.my_web_project.member.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.my_web_project.entity.Member;
import com.springboot.my_web_project.entity.RefreshToken;
import com.springboot.my_web_project.repository.MemberRepository;
import com.springboot.my_web_project.repository.RefreshTokenRepository;

@Service
//@RequiredArgsConstructor //CI(생성자 주입) 자동 생성
public class MemberService {

	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BCryptPasswordEncoder encoder;
	
	public MemberService(MemberRepository memberRepository
					, RefreshTokenRepository refreshTokenRepository
					, BCryptPasswordEncoder encoder) {
		
		this.memberRepository = memberRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.encoder = encoder;
	}
	
	//로그인 검증(DB에서 아이디와 비밀번호가 일치하는 회원 찾기)
	public Optional<Member> login(String userId, String password) {
		Optional<Member> memberOpt = memberRepository.findById(userId);
		
		//.isPresent(): 로그인 성공/실패만 확인하는 메서드
		if(memberOpt.isPresent()) {
			Member member = memberOpt.get();
			/*
			 * encoder.matches(평문비밀번호, 암호화된 비밀번호)
			 * 암호화된 패스워드는 ==이나 .equals()로 비교할 수 없다
			 */
		    boolean isMatch = encoder.matches(password, member.getPassword());
			if(isMatch) {
				//암호화된 비밀번호가 맞으면 회원정보를 리턴
				return Optional.of(member);
			}
		}
		//암호화된 비밀번호가 다르거나 틀리면 빈회원정보를 리턴
		return Optional.empty();
	}
	
	//리프레시 토큰 저장 또는 업데이트 (로그인 성공 시 호출)
	@Transactional
	public void saveRefreshToken(String userId, String tokenValue) {
		//해당 유저의 기존 코튼이 있는지 확인
		RefreshToken token = refreshTokenRepository.findByUserId(userId)
				.orElse(new RefreshToken()); //없으면 새로 생성
		
		token.setUserId(userId);
		token.setTokenValue(tokenValue);
		
		//현재 시간에 정확히 14일(2주)을 더한 만료 시각을 함께 세팅!
		token.setExpiryDate(LocalDateTime.now().plusDays(14));
		
		//ID가 있으면 Update, 없으면 Insert가 자동으로 일어남
		refreshTokenRepository.save(token);
	}
	
	//사용자 이름 조회
	public String findMemberName(String userId) {
		/*
		 * ID로 Member를 찾는다
		 * findById는 repository에 없는 이유는 entity에서 member테이블에 userId는
		 * @Id로 지정된 기본키(PK)이므로 JPA가 기본으로 제공하는 findById를 사용하면된다.
		 */
		return memberRepository.findById(userId) 
				.map(member -> member.getUserName()) //Member가 있으면, 그 안의 UserName만 꺼낸다
				.orElse("알 수 없는 사용자"); //만약 Member가 없으면 이 글자를 대신 준다
	}
	
	//해당 유저와 토큰정보가 같은지 체크
	public boolean checkRefreshToken(String userId, String tokenValue) {
		//DB에서 유저의 토큰 정보를 가져옴
		return refreshTokenRepository.findByUserId(userId)
				.map(tokenEntity -> tokenEntity.getTokenValue().equals(tokenValue))
				.orElse(false); //유저 정보가 없거나 토큰이 다르면 false
	}
	
	//ID 중복 체크(존재 true, 미존재 false)
	public boolean isIdDuplicated(String userId) {
		return memberRepository.existsById(userId);
	}

	//회원가입
	public void join(Member member) {
		//비밀번호 암호화
		String encodePassword = encoder.encode(member.getPassword());
		member.setPassword(encodePassword);
		
		memberRepository.save(member);
	}
	
}
