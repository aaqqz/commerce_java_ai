package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "answer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerEntity extends BaseEntity {
    private Long adminId;
    private Long questionId;
    private String content;

    public AnswerEntity(Long adminId, Long questionId, String content) {
        this.adminId = adminId;
        this.questionId = questionId;
        this.content = content;
    }
}
