package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.QnA;
import java.util.List;

public record QnAResponse(
        Long questionId,
        String questionTitle,
        String question,
        Long answerId,
        String answer
) {
    public static QnAResponse of(QnA qna) {
        return new QnAResponse(
                qna.question().id(),
                qna.question().title(),
                qna.question().content(),
                qna.answer().id(),
                qna.answer().content()
        );
    }

    public static List<QnAResponse> of(List<QnA> qna) {
        return qna.stream().map(QnAResponse::of).toList();
    }
}
