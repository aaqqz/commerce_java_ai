package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.QuestionContent;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddQuestionRequest {
    private Long productId;
    private String title;
    private String content;

    public QuestionContent toContent() {
        if (title.isEmpty()) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }
        if (title.length() > 100) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }
        if (content.isEmpty()) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }
        return new QuestionContent(title, content);
    }
}
