package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangeLogDTO {
    private int workSystemChangeLogId;
    private String date;
    private String changeReason;
    private String startTime;
    private String endTime;
    private String workSystemName;
}
