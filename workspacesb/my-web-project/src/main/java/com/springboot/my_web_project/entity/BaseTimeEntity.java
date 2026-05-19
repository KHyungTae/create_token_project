package com.springboot.my_web_project.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@MappedSuperclass //직접 테이블로 안만들고, 상속받은 자식클래스에 필드(컬럼)만 넘겨줌
//@EntityListeners(AuditingEntityListener.class) //데이터가 생성되거나 수정될 때 이를 감지해서 날짜를 자동으로 넣어주는 역할
@Getter
public abstract class BaseTimeEntity {

	//@CreatedDate //생성 시 자동 저장
    @Column(name = "CREATED_DT", updatable = false) //수정 시 이 컬럼은 건드리지 않음
    private LocalDateTime createdDt;

    //@LastModifiedDate //수정 시 자동 갱신
    @Column(name = "UPDATE_DT")
    private LocalDateTime updateDt;
    
    //가입할때 딱 1번 실행
    @PrePersist
    public void prePersist() {
        this.createdDt = LocalDateTime.now();
        this.updateDt = null; // 가입 시 명시적으로 null 유지
    }
    
    //수정할때마다 실행
    @PreUpdate
    public void preUpdate() {
        this.updateDt = LocalDateTime.now(); // 수정 시에만 갱신
    }
}
