package com.c4.hero.domain.vacation.google;

import com.c4.hero.domain.vacation.entity.VacationLog;
import com.c4.hero.domain.vacation.repository.VacationRepository;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleCalendarSyncService {

    private final Calendar calendarClient;
    private final VacationRepository vacationRepository;

    @Value("${google.calendar.id}")
    private String calendarId;

    /**
     * 특정 월의 휴가 로그를 Google Calendar에 동기화
     * - 해당 월과 "겹치는" 휴가 로그를 모두 가져와서 반영
     */
    @Transactional
    public SyncResult syncMonth(int year, int month) {
        LocalDate rangeStart = LocalDate.of(year, month, 1);
        LocalDate rangeEnd = rangeStart.withDayOfMonth(rangeStart.lengthOfMonth());

        // ✅ 우리가 Repository에 추가한 메서드로 조회 (월 겹침 포함)
        List<VacationLog> logs = vacationRepository.findOverlappingVacationLogs(rangeStart, rangeEnd);

        int inserted = 0;
        int updated = 0;
        int failed = 0;

        for (VacationLog log : logs) {
            try {

                String eventId = log.getGoogleEventId();
                Event event = buildEventFromLog(log);

                if (eventId == null || eventId.isBlank()) {
                    Event created = calendarClient.events()
                            .insert(calendarId, event)
                            .execute();

                    log.setGoogleEventId(created.getId()); // JPA 영속 상태면 dirty checking으로 반영됨
                    inserted++;
                    continue;
                }

                try {
                    calendarClient.events()
                            .update(calendarId, eventId, event)
                            .execute();
                    updated++;
                } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
                    // 캘린더에서 이벤트가 삭제된 경우(404) → 새로 생성 후 DB 갱신
                    if (e.getStatusCode() == 404) {
                        Event created = calendarClient.events()
                                .insert(calendarId, event)
                                .execute();
                        log.setGoogleEventId(created.getId());
                        inserted++;
                    } else {
                        throw e;
                    }
                }

            } catch (Exception ex) {
                failed++;
                System.err.println("[GoogleCalendarSync] failed. vacationLogId=" + log.getVacationLogId()
                        + ", msg=" + ex.getMessage());
            }
        }

        return new SyncResult(inserted, updated, failed, logs.size());
    }

    /**
     * VacationLog -> Google Calendar Event 변환
     * - 휴가: all-day 이벤트가 일반적으로 UX에 적합
     * - all-day 이벤트는 end.date가 "종료 다음날" (exclusive) 규칙
     */
    private Event buildEventFromLog(VacationLog log) {
        LocalDate start = log.getStartDate();
        LocalDate endInclusive = log.getEndDate();
        LocalDate endExclusive = endInclusive.plusDays(1);

        Event event = new Event();
        event.setSummary(buildSummary(log));
        event.setDescription(buildDescription(log));

        // all-day 이벤트
        event.setStart(new EventDateTime().setDate(new DateTime(start.toString())));
        event.setEnd(new EventDateTime().setDate(new DateTime(endExclusive.toString())));

        // 추적용(나중에 역추적/정리/삭제 동기화에 도움)
        Map<String, String> priv = new HashMap<>();
        priv.put("vacationLogId", String.valueOf(log.getVacationLogId()));
        Event.ExtendedProperties ep = new Event.ExtendedProperties();
        ep.setPrivate(priv);
        event.setExtendedProperties(ep);

        return event;
    }

    private String buildSummary(VacationLog log) {
        // 프로젝트 엔티티 관계가 있다면 이 형태로 추천:
        // ex) "김경영 - 연차"
        try {
            String empName = log.getEmployee().getEmployeeName();
            String typeName = log.getVacationType().getVacationTypeName();
            return empName + " - " + typeName;
        } catch (Exception ignore) {
            // 관계 매핑이 아직 불완전하면 최소 형태로
            return "휴가(" + log.getVacationLogId() + ")";
        }
    }

    private String buildDescription(VacationLog log) {
        String reason = null;
        try {
            reason = log.getReason();
        } catch (Exception ignore) {
        }
        return "HERO VacationLogId=" + log.getVacationLogId() + (reason != null ? ("\n사유: " + reason) : "");
    }

    public record SyncResult(int inserted, int updated, int failed, int total) {}
}
