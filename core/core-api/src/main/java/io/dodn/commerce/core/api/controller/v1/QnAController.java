package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.request.AddQuestionRequest;
import io.dodn.commerce.core.api.controller.v1.request.UpdateQuestionRequest;
import io.dodn.commerce.core.api.controller.v1.response.QnAResponse;
import io.dodn.commerce.core.domain.QnAService;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.response.ApiResponse;
import io.dodn.commerce.core.support.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QnAController {
    private final QnAService qnaService;

    @GetMapping("/v1/qna")
    public ApiResponse<PageResponse<QnAResponse>> getQnA(
            @RequestParam Long productId,
            @RequestParam Integer offset,
            @RequestParam Integer limit) {
        var page = qnaService.findQnA(productId, new OffsetLimit(offset, limit));
        return ApiResponse.success(new PageResponse<>(QnAResponse.of(page.content()), page.hasNext()));
    }

    @PostMapping("/v1/questions")
    public ApiResponse<Object> createQuestion(
            User user,
            @RequestBody AddQuestionRequest request) {
        qnaService.addQuestion(user, request.productId(), request.toContent());
        return ApiResponse.success();
    }

    @PutMapping("/v1/questions/{questionId}")
    public ApiResponse<Object> updateQuestion(
            User user,
            @PathVariable Long questionId,
            @RequestBody UpdateQuestionRequest request) {
        qnaService.updateQuestion(user, questionId, request.toContent());
        return ApiResponse.success();
    }

    @DeleteMapping("/v1/questions/{questionId}")
    public ApiResponse<Object> deleteQuestion(
            User user,
            @PathVariable Long questionId) {
        qnaService.removeQuestion(user, questionId);
        return ApiResponse.success();
    }
}
