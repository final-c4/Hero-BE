package com.c4.hero.domain.employee.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

/**
 * AccountStatus Enum을 DB의 코드값과 변환
 */
@Converter(autoApply = true)
public class AccountStatusConverter implements AttributeConverter<AccountStatus, String> {

    @Override
    public String convertToDatabaseColumn(AccountStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode();
    }

    @Override
    public AccountStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(AccountStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
