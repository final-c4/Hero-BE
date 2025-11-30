package com.c4.hero.domain.employee.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

/**
 * EmployeeStatus Enum을 DB의 'A', 'O', 'R' 코드값과 변환
 */
@Converter(autoApply = true)
public class EmployeeStatusConverter implements AttributeConverter<EmployeeStatus, String> {

    /**
     * Enum -> DB 데이터 변환
     * @param status Enum (e.g., EmployeeStatus.ACTIVE)
     * @return DB에 저장될 문자열 (e.g., "A")
     */
    @Override
    public String convertToDatabaseColumn(EmployeeStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode();
    }

    /**
     * DB 데이터 -> Enum 변환
     * @param code DB에서 읽어온 문자열 (e.g., "A")
     * @return 변환된 Enum (e.g., EmployeeStatus.ACTIVE)
     */
    @Override
    public EmployeeStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(EmployeeStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
