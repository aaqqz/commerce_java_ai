package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionEntity extends BaseEntity {
    private Long userId;
    // NOTE: QNA 는 아예 상품 전용으로 지정
    private Long productId;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    public static QuestionEntity create(Long userId, Long productId, String title, String content) {
        QuestionEntity question = new QuestionEntity();
        question.userId = userId;
        question.productId = productId;
        question.title = title;
        question.content = content;

        return question;
    }

    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
