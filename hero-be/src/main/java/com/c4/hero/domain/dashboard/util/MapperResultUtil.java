package com.c4.hero.domain.dashboard.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * <pre>
 * Class Name  : MapperResultUtil
 * Description : MyBatis Mapper 결과(Map) 타입 변환 유틸리티
 *
 * History
 * 2025/12/26 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
public class MapperResultUtil {

    /**
     * Map에서 Integer 값 추출
     *
     * @param map 결과 맵
     * @param key 키
     * @return Integer 값 (없으면 0)
     */
    public static Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).intValue();
        if (value instanceof BigInteger) return ((BigInteger) value).intValue();
        return Integer.parseInt(value.toString());
    }

    /**
     * Map에서 Long 값 추출
     *
     * @param map 결과 맵
     * @param key 키
     * @return Long 값 (없으면 0L)
     */
    public static Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).longValue();
        if (value instanceof BigInteger) return ((BigInteger) value).longValue();
        return Long.parseLong(value.toString());
    }

    /**
     * Map에서 Double 값 추출
     *
     * @param map 결과 맵
     * @param key 키
     * @return Double 값 (없으면 0.0)
     */
    public static Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0.0;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Float) return ((Float) value).doubleValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).doubleValue();
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        if (value instanceof Long) return ((Long) value).doubleValue();
        return Double.parseDouble(value.toString());
    }

    /**
     * Map에서 Boolean 값 추출
     *
     * @param map 결과 맵
     * @param key 키
     * @return Boolean 값 (없으면 false)
     */
    public static Boolean getBooleanValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Integer) return (Integer) value == 1;
        if (value instanceof Long) return ((Long) value) == 1L;
        if (value instanceof BigInteger) return ((BigInteger) value).intValue() == 1;
        return Boolean.parseBoolean(value.toString());
    }

    /**
     * Map에서 String 값 추출
     *
     * @param map 결과 맵
     * @param key 키
     * @return String 값 (없으면 빈 문자열)
     */
    public static String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return "";
        return value.toString();
    }
}