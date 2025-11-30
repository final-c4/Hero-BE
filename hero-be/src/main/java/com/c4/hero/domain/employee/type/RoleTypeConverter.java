package com.c4.hero.domain.employee.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

/**
 * RoleType Enum을 DB의 코드값과 변환
 */
@Converter(autoApply = true)
public class RoleTypeConverter implements AttributeConverter<RoleType, String> {

    @Override
    public String convertToDatabaseColumn(RoleType roleType) {
        if (roleType == null) {
            return null;
        }
        return roleType.getCode();
    }

    @Override
    public RoleType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(RoleType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
