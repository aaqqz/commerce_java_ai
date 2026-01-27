package io.dodn.commerce.core.support.error;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorTypeTest {
    @Test
    void ErrorCode_중복_사용_확인() {
        Map<ErrorCode, Long> counts = Arrays.stream(ErrorType.values())
                .map(ErrorType::getCode)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Set<ErrorCode> duplicates = counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        assertTrue(duplicates.isEmpty(), "중복된 ErrorCode가 있습니다: " + duplicates);
    }

    @Test
    void ErrorCode가_ErrorType에서_모두_사용되는지_확인() {
        Set<ErrorCode> declaredCodes = EnumSet.allOf(ErrorCode.class);
        Set<ErrorCode> usedCodes = Arrays.stream(ErrorType.values())
                .map(ErrorType::getCode)
                .collect(Collectors.toSet());

        declaredCodes.removeAll(usedCodes);

        assertTrue(declaredCodes.isEmpty(), "사용되지 않은 ErrorCode가 있습니다: " + declaredCodes);
    }
}
