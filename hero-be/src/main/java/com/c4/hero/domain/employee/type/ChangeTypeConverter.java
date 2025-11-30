package com.c4.hero.domain.employee.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

/**
 * ChangeType Enum을 DB의 코드값과 변환
 */
@Converter(autoApply = true)
public class ChangeTypeConverter implements AttributeConverter<ChangeType, String> {

    @Override
    public String convertToDatabaseColumn(ChangeType changeType) {
        if (changeType == null) {
            return null;
        }
        return changeType.getCode();
    }

    @Override
    public ChangeType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(ChangeType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
