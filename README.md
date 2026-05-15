###토큰 로그인

###회원가입 비밀번호암호화

###추가할사항

아이디 - /^[a-z0-9][a-z0-9_\-]{4,19}$/
패스워드 - /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+=-]).{8,16}$/

이름 - /[^가-힣a-zA-Z0-9]/gi
이메일 - /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/

핸드폰 - /^((01[1|6|7|8|9])[1-9][0-9]{6,7})$|(010[1-9][0-9]{7})$/
	phoneNo.replace(/ /gi, "").replace(/-/gi, "") - 공백, 하이픈 제거

	//숫자로만 적었을시 화면에 하이픈 보이게하는방법
	if(num.length == 10) {
                formatNum = num.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
            } else if(num.length == 11) {
                formatNum = num.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
            } else {
                formatNum = num;
            }

+join.html
$(document).ready(function() {
    let validationStatus = {
        userId: false, password: false, email: false, userName: false, tel: false
    };

    // 🌟 입력창 테두리와 하단 메시지를 동시에 제어하는 코어 함수
    function updateValidationUI($group, $msg, message, status) {
        if (status === "error") {
            $group.removeClass("border-success").addClass("border-error");
            $msg.text(message).removeClass("success").addClass("error").show();
        } else if (status === "success") {
            $group.removeClass("border-error").addClass("border-success");
            $msg.text(message).removeClass("error").addClass("success").show();
        } else {
            // 정상 클리어 상태 (메시지 숨김 및 테두리 초기화)
            $group.removeClass("border-error border-success");
            $msg.hide().text("");
        }
    }

    ================================================================================================
    function checkJoinButtonValidity() {
        let isValid = validationStatus.userId && validationStatus.password && 
                      validationStatus.email && validationStatus.userName && validationStatus.tel;
        $('#joinBtn').prop("disabled", !isValid);
    }

	function checkJoinButtonValidity() {
	    let isValid = Object.values(validationStatus).every(status => status === true);
	    $('#joinBtn').prop("disabled", !isValid);
	}
    ================================================================================================


    // 1. 아이디 검증 (영어 소문자/숫자 4~12자)
    //blur: "사용자가 이 입력창에서 입력을 마쳤으니, 이제 작성된 내용을 검사하거나 화면을 정리해라"
    $('#userId').on("blur", function() {
        let userId = $(this).val().trim();
        let $group = $('#idGroup');
        let $msg = $('#idMsg');
        let idReg = /^[a-z0-9][a-z0-9_\-]{4,19}$/;

        if (!userId) {
            updateValidationUI($group, $msg, "아이디는 필수 입력 항목입니다.", "error");
            validationStatus.userId = false;
            checkJoinButtonValidity();
            return;
        }
        if (!idReg.test(userId)) {
            updateValidationUI($group, $msg, "아이디는 4~12자의 영문 소문자, 숫자만 사용 가능합니다.", "error");
            validationStatus.userId = false;
            checkJoinButtonValidity();
            return;
        }

        $.ajax({
            type: 'POST',
            url: '/api/member/dupIdCheck',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({ userId: userId.toLowerCase() }),
            success: function() {
                updateValidationUI($group, $msg, "사용 가능한 아이디입니다.", "success");
                validationStatus.userId = true;
                checkJoinButtonValidity();
            },
            error: function() {
                updateValidationUI($group, $msg, "이미 존재하는 아이디입니다.", "error");
                validationStatus.userId = false;
                checkJoinButtonValidity();
            }
        });
    });

    // 2. 비밀번호 검증 (영문+숫자+특수문자 8~16자)
    $('#password').on("blur", function() {
        let password = $(this).val();
        let $group = $('#pwGroup');
        let $msg = $('#pwMsg');
        let pwReg = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+=-]).{8,16}$/;

        if (!password) {
            updateValidationUI($group, $msg, "비밀번호는 필수 입력 항목입니다.", "error");
            validationStatus.password = false;
        } else if (!pwReg.test(password)) {
            updateValidationUI($group, $msg, "비밀번호: 8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해 주세요.", "error");
            validationStatus.password = false;
        } else {
            updateValidationUI($group, $msg, "사용 가능한 비밀번호입니다.", "clear");
            validationStatus.password = true;
        }
        checkJoinButtonValidity();
    });

	// 2. 비밀번호 검증 (영문+숫자+특수문자 8~16자) 2
	// 💡 예시: 비밀번호 입력창 제어 (document.ready 내부)
	$('#password').on("blur", function() {
	    let password = $(this).val().trim();
	    let $group = $('#pwGroup');
	    let $msg = $('#pwMsg'); // HTML의 id가 pswdMsg네요! 확인!
	    let pwReg = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+=-]).{8,16}$/;
	    
	    // 아이디 때와 똑같이 리턴값으로 흐름 제어!
	    if (passwordValidation(password, $group, $msg, pwReg) === false) {
		return;
	    }
	});

	// 💡 비밀번호 검증 함수 (ready 블록 밖)
	function passwordValidation(password, $group, $msg, pwReg) {
	    if(!password) {
		updateValidationUI($group, $msg, "비밀번호: 필수 입력 항목입니다.", "error");
		validationStatus.password = false;
		checkJoinButtonValidity();
		return false;
	    }
	    if(!pwReg.test(password)) {
		updateValidationUI($group, $msg, "비밀번호: 8~20자의 영문, 숫자를 조합해주세요.", "error");
		validationStatus.password = false;
		checkJoinButtonValidity();
		return false;
	    }

	    updateValidationUI($group, $msg, "안전한 비밀번호입니다.", "success");
	    validationStatus.password = true;
	    checkJoinButtonValidity(); 
	    return true;
	}



    // 3. 이메일 검증
    $('#email').on("blur", function() {
        let email = $(this).val().trim();
        let $group = $('#emailGroup');
        let $msg = $('#emailMsg');
        let emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if (!email) {
            updateValidationUI($group, $msg, "", "clear");
            validationStatus.email = true; 
        } else if (!emailReg.test(email)) {
            updateValidationUI($group, $msg, "이메일: 이메일 주소가 정확한지 확인해 주세요.", "error");
            validationStatus.email = false;
        } else {
            updateValidationUI($group, $msg, "", "clear");
            validationStatus.email = true;
        }
        checkJoinButtonValidity();
    });

    // 4. 이름 검증
    $('#userName').on("blur", function() {
        let userName = $(this).val().trim();
        let $group = $('#nameGroup');
        let $msg = $('#userNameMsg');
	let nameReg = /[^가-힣a-zA-Z0-9]/gi;

        if (!userName) {
            updateValidationUI($group, $msg, "이름은 필수 입력 항목입니다.", "error");
            validationStatus.userName = false;
        }else if(!nameReg.test(userName) { 
	    updateValidationUI($group, $msg, "이름: 한글, 영문 대/소문자를 사용해 주세요. (특수기호, 공백 사용 불가)", "error");
            validationStatus.userName = false;
	}else {
            updateValidationUI($group, $msg, "", "clear");
            validationStatus.userName = true;
        }
        checkJoinButtonValidity();
    });

    // 5. 휴대전화번호 검증
    $('#tel').on("input blur", function(e) {
        let $this = $(this);
        let val = $this.val().replace(/[^0-9]/g, "");
        $this.val(val);

        let $group = $('#phoneGroup');
        let $msg = $('#phoneMsg');
        let phoneReg = /^01\d{3,4}\d{4}$/;

        if (e.type === "blur") {
            if (!val) {
                updateValidationUI($group, $msg, "휴대전화번호는 필수 입력 항목입니다.", "error");
                validationStatus.tel = false;
            } else if (!phoneReg.test(val)) {
                updateValidationUI($group, $msg, "휴대전화번호: 휴대전화번호가 정확한지 확인해 주세요.", "error");
                validationStatus.tel = false;
            } else {
                updateValidationUI($group, $msg, "", "clear");
                validationStatus.tel = true;
            }
            checkJoinButtonValidity();
        }
    });

	//5. 휴대전화번호 검증 2
	// [필수] 화면 HTML의 input 태그에 maxlength="13"이 설정되어 있어야 포맷이 깨지지 않습니다.
	$('#tel').on("input blur", function(e) {
	    let $this = $(this);
	    let rawVal = $this.val().replace(/[^0-9]/g, ""); // 1. 숫자만 추출 (예: 01089561245)
	    
	    // 2. [추가] 사용자가 타이핑 중(input)일 때 화면에 하이픈을 실시간으로 끼워넣음
	    if (e.type === "input") {
		let formattedVal = "";
		if (rawVal.length < 4) {
		    formattedVal = rawVal;
		} else if (rawVal.length < 8) {
		    formattedVal = rawVal.replace(/(\d{3})(\d{1,4})/, '$1-$2');
		} else if (rawVal.length < 11) {
		    formattedVal = rawVal.replace(/(\d{3})(\d{3})(\d{1,4})/, '$1-$2-$3');
		} else {
		    formattedVal = rawVal.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
		}
		$this.val(formattedVal); // 화면에는 '010-8956-1245'로 표시됨
	    }

	    let $group = $('#phoneGroup');
	    let $msg = $('#phoneMsg');
	    
	    // 첫 번째 질문에서 다룬 엄격한 정규식으로 대체 (가짜 번호 차단)
	    let phoneReg = /^((01[1|6|7|8|9])[1-9][0-9]{6,7})$|(010[1-9][0-9]{7})$/;

	    // 3. 포커스가 빠져나갈 때(blur) 유효성 검사 수행 (검사는 하이픈 없는 순수 숫자로 진행)
	    if (e.type === "blur") {
		if (!rawVal) {
		    updateValidationUI($group, $msg, "휴дзен화번호는 필수 입력 항목입니다.", "error");
		    validationStatus.tel = false;
		} else if (!phoneReg.test(rawVal)) {
		    updateValidationUI($group, $msg, "휴대전화번호: 휴대전화번호가 정확한지 확인해 주세요.", "error");
		    validationStatus.tel = false;
		} else {
		    updateValidationUI($group, $msg, "", "clear");
		    validationStatus.tel = true;
		}
		checkJoinButtonValidity();
	    }
	});

	//5. 휴대전화번호 검증 3
	// DOM 탐색 최소화를 위해 변수를 함수 밖으로 이동
	const $phoneGroup = $('#phoneGroup');
	const $phoneMsg = $('#phoneMsg');
	const phoneReg = /^((01[1|6|7|8|9])[1-9][0-9]{6,7})$|(010[1-9][0-9]{7})$/;

	$('#tel').on("input blur", function(e) {
	    let $this = $(this);
	    let rawVal = $this.val().replace(/[^0-9]/g, ""); 
	    
	    // 1. 실시간 포맷팅 (input)
	    if (e.type === "input") {
		let formattedVal = "";
		if (rawVal.length < 4) {
		    formattedVal = rawVal;
		} else if (rawVal.length < 8) {
		    formattedVal = rawVal.replace(/(\d{3})(\d{1,4})/, '$1-$2');
		} else if (rawVal.length < 11) {
		    formattedVal = rawVal.replace(/(\d{3})(\d{3})(\d{1,4})/, '$1-$2-$3');
		} else {
		    formattedVal = rawVal.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
		}
		$this.val(formattedVal);

		// [개선] 타이핑 중 11자리가 완성되면 blur를 기다리지 않고 즉시 검사 수행
		if (rawVal.length === 11) {
		    validatePhoneNumber(rawVal);
		}
	    }

	    // 2. 포커스 아웃 (blur)
	    if (e.type === "blur") {
		validatePhoneNumber(rawVal);
	    }
	});

	// 유효성 검사 로직 분리
	function validatePhoneNumber(rawVal) {
	    if (!rawVal) {
		updateValidationUI($phoneGroup, $phoneMsg, "휴대전화번호는 필수 입력 항목입니다.", "error");
		validationStatus.tel = false;
	    } else if (!phoneReg.test(rawVal)) {
		updateValidationUI($phoneGroup, $phoneMsg, "휴대전화번호가 정확한지 확인해 주세요.", "error");
		validationStatus.tel = false;
	    } else {
		updateValidationUI($phoneGroup, $phoneMsg, "", "clear");
		validationStatus.tel = true;
	    }
	    checkJoinButtonValidity();
	}

    // 가입 데이터 전송
    $('#joinBtn').on("click", function() {
        let joinData = {
            userId: $('#userId').val().trim().toLowerCase(),
            password: $('#password').val(),
            email: $('#email').val().trim(),
            userName: $('#userName').val().trim(),
            tel: $('#tel').val().trim()
        };

        $.ajax({
            type: 'POST',
            url: '/api/member/join',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(joinData),
            success: function() {
                alert("회원가입이 완료되었습니다!");
                location.href = "/login";
            },
            error: function(xhr) {
                alert("가입 실패: " + xhr.responseText);
            }
        });
    });

    $('#mainBtn').on("click", function() { location.href = "/main"; });
});


+service
// 1. 아이디 중복 체크 (존재하면 true, 없으면 false)
public boolean isIdDuplicated(String userId) {
    // 이미 소문자로 변환되어 들어온 userId를 기준으로 조회
    return memberRepository.existsById(userId);
}

// 2. 회원가입 처리 (비밀번호 암호화 필수)
@Transactional
public void join(Member member) {
    // 🌟 화면에서 넘어온 평문 패스워드를 BCrypt로 안전하게 암호화
    String encodedPassword = encoder.encode(member.getPassword());
    member.setPassword(encodedPassword);
    
    // 오라클 DB에 INSERT 실행
    memberRepository.save(member);
}


+MemberAPIController
// 1. 아이디 중복 체크 API
@PostMapping("/api/member/dupIdCheck")
public ResponseEntity<?> dupIdCheck(@RequestBody Map<String, String> request) {
    String userId = request.get("userId");
    
    // 만약 아이디가 넘어오지 않았거나 공백이면 에러 반환
    if (userId == null || userId.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("아이디를 입력해주세요.");
    }
    
    // 서비스단에 중복 여부 확인 요청
    boolean isDuplicated = memberService.isIdDuplicated(userId.trim().toLowerCase());
    
    if (isDuplicated) {
        // 중복된 아이di가 존재하면 400 Bad Request와 메시지 반환
        return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
    }
    
    // 중복되지 않았다면 200 OK 반환
    return ResponseEntity.ok().build();
}

// 2. 회원가입 가입 완료 API
@PostMapping("/api/member/join")
public ResponseEntity<?> joinApi(@RequestBody MemberVO memberVO) {
    
    // 유효성 검사 (서버단 방어 코드)
    if (memberVO.getUserId() == null || memberVO.getPassword() == null || memberVO.getUserName() == null) {
        return ResponseEntity.badRequest().body("필수 가입 정보가 누락되었습니다.");
    }
    
    // VO 가방에 든 데이터를 엔티티 객체로 가공
    Member member = new Member();
    member.setUserId(memberVO.getUserId().trim().toLowerCase()); // 아이디 소문자 강제 정착
    member.setPassword(memberVO.getPassword());
    member.setUserName(memberVO.getUserName().trim());
    
    // 기존 MemberVO에 email과 tel 필드가 없다면 추가 선언이 필요합니다.
    // 만약 전송 테스트를 위해 임시로 담으려면 아래와 같이 반영하세요.
    // member.setEmail(memberVO.getEmail()); 
    // member.setTel(memberVO.getTel());

    try {
        memberService.join(member); // 서비스 호출 및 DB 저장
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("서버 오류로 인해 가입에 실패했습니다.");
    }
}
