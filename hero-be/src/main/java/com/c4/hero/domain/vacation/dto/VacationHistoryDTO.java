package com.c4.hero.domain.vacation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VacationHistoryDTO {
    private int vacationLogId;

    private LocalDate startDate;
    private LocalDate endDate;
    private String vacationTypeName;
    private String reason;
    private String approvalStatus;

    public VacationHistoryDTO(LocalDate startDate,
                              LocalDate endDate,
                              String vacationTypeName,
                              String reason,
                              String approvalStatus) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.vacationTypeName = vacationTypeName;
        this.reason = reason;
        this.approvalStatus = approvalStatus;
    }
}
