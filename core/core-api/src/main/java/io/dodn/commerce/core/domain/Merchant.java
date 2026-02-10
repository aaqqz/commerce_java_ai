package io.dodn.commerce.core.domain;

public record Merchant(
        Long id,
        String name,
        Integer settlementCycle
) {
}
