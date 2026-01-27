package io.dodn.commerce.storage.db.core.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
class AesConverter implements AttributeConverter<String, String> {

    private final AesHelper aesHelper;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute != null ? aesHelper.encrypt(attribute) : null;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData != null ? aesHelper.decrypt(dbData) : null;
    }
}
