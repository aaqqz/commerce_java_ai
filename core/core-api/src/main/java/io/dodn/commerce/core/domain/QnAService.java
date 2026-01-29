package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.AnswerEntity;
import io.dodn.commerce.storage.db.core.AnswerRepository;
import io.dodn.commerce.storage.db.core.QuestionEntity;
import io.dodn.commerce.storage.db.core.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnAService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public Page<QnA> findQnA(Long productId, OffsetLimit offsetLimit) {
        Slice<QuestionEntity> questions = questionRepository.findByProductIdAndStatus(
                productId,
                EntityStatus.ACTIVE,
                offsetLimit.toPageable()
        );

        Map<Long, AnswerEntity> answers = answerRepository.findByQuestionIdIn(
                        questions.getContent().stream()
                                .map(QuestionEntity::getId)
                                .toList()
                ).stream()
                .filter(AnswerEntity::isActive)
                .collect(Collectors.toMap(AnswerEntity::getQuestionId, a -> a));

        return new Page<>(
                questions.getContent().stream()
                        .map(it -> new QnA(
                                new Question(
                                        it.getId(),
                                        it.getUserId(),
                                        it.getTitle(),
                                        it.getContent()
                                ),
                                answers.containsKey(it.getId())
                                        ? new Answer(
                                                answers.get(it.getId()).getId(),
                                                answers.get(it.getId()).getAdminId(),
                                                answers.get(it.getId()).getContent()
                                        )
                                        : Answer.EMPTY

                        ))
                        .toList(),
                questions.hasNext()
        );
    }

    public Long addQuestion(User user, Long productId, QuestionContent content) {
        QuestionEntity saved = questionRepository.save(
                QuestionEntity.create(
                        user.id(),
                        productId,
                        content.title(),
                        content.content()
                )
        );

        return saved.getId();
    }

    @Transactional
    public Long updateQuestion(User user, Long questionId, QuestionContent content) {
        QuestionEntity found = questionRepository.findByIdAndUserId(questionId, user.id())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));
        if (!found.isActive()) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }

        found.updateContent(content.title(), content.content());

        return found.getId();
    }

    @Transactional
    public Long removeQuestion(User user, Long questionId) {
        QuestionEntity found = questionRepository.findByIdAndUserId(questionId, user.id())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));
        if (!found.isActive()) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }

        found.delete();

        return found.getId();
    }

    /**
     * NOTE: 답변은 어드민 쪽 기능임
     * fun addAnswer(user: User, questionId: Long, content: String): Long {...}
     * fun updateAnswer(user: User, answerId: Long, content: String): Long {...}
     * fun removeAnswer(user: User, answerId: Long): Long {...}
     */
}
