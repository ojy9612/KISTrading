package com.example.kistrading.entity;

import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class TimeStamped {

    @CreatedDate
    @Comment("생성 일자")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Comment("수정 일자")
    private LocalDateTime modifiedAt;
}
