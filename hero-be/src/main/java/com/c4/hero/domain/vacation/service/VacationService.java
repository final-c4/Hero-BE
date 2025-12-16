package com.c4.hero.domain.vacation.service;

import com.c4.hero.common.pagination.PageCalculator;
import com.c4.hero.common.pagination.PageInfo;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
import com.c4.hero.domain.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;

    public PageResponse<VacationHistoryDTO> findVacationHistory(
            Integer employeeId,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    ){
        PageInfo pageInfo = PageCalculator.calculate(
                page,
                size,
                Integer.MAX_VALUE // JPA Page에서는 실제 count는 repository가 처리
        );

        PageRequest pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.getSize());

        Page<VacationHistoryDTO> pageResult = vacationRepository.findVacationHistory(
                employeeId,
                startDate,
                endDate,
                pageable
        );

        return PageResponse.of(
                pageResult.getContent(),
                pageResult.getNumber() + 1,
                pageResult.getSize(),
                pageResult.getTotalElements()
        );
    }
}
