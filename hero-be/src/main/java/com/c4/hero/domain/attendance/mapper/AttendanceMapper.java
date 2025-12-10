package com.c4.hero.domain.attendance.mapper;

import com.c4.hero.domain.attendance.dto.OvertimeDTO;
import com.c4.hero.domain.attendance.dto.PersonalDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Interface Name: AttendanceMapper
 * Description: 근태 관련 데이터 조회를 위한 MyBatis Mapper 인터페이스
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * @author 이지윤
 * @version 2.0
 */
@Mapper
public interface AttendanceMapper {

    /**
     * 개인 근태 기록 목록(페이지)을 조회합니다.
     *
     * @param offset    조회 시작 위치 (0부터 시작)
     * @param size      조회할 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 개인 근태 기록 리스트
     */
    List<PersonalDTO> selectPersonalPage(
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 개인 근태 기록 총 개수를 조회합니다.
     *
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 개인 근태 기록 총 개수
     * 각 메서드별로 startDate,endDate가 있는 이뉴는 각 쿼리에서 같은 필터를 써서,
       리스트와 totalCount가 일치하게 만들기 위해서이다.
     */
    int selectPersonalCount(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    List<OvertimeDTO> selectOvertimePage();
}
