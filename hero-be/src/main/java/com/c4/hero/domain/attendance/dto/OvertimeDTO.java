package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OvertimeDTO {
    private int overtimeId;
    private String date;
    private String startTime;
    private String endTime;
    private float overtimeHours;
    private String reason;
}
