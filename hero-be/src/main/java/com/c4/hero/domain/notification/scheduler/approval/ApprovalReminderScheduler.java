package com.c4.hero.domain.notification.scheduler.approval;

import com.c4.hero.domain.notification.event.approval.ApprovalNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class Name: ApprovalReminderScheduler
 * Description: 결재 대기 독촉 알림 스케줄러
 *
 * History
 * 2026/01/02 (혜원) 최초 작성
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalReminderScheduler {

    private final ApplicationEventPublisher eventPublisher;
    // TODO: ApprovalMapper 주입 필요

    /**
     * 매일 오전 10시, 3일 이상 미결재 문서 독촉 알림
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void sendReminderForPendingApprovals() {
        log.info("[스케줄러] 결재 대기 독촉 알림 시작");

        // TODO: 3일 이상 대기 중인 결재 건 조회
        // List<Map<String, Object>> pendingApprovals = approvalMapper.selectPendingApprovalsOverDays(3);

        // for (Map<String, Object> approval : pendingApprovals) {
        //     eventPublisher.publishEvent(
        //         ApprovalNotificationEvent.ApprovalReminderEvent.builder()
        //             .docId((Integer) approval.get("docId"))
        //             .templateKey((String) approval.get("templateKey"))
        //             .title((String) approval.get("title"))
        //             .drafterId((Integer) approval.get("drafterId"))
        //             .drafterName((String) approval.get("drafterName"))
        //             .approverId((Integer) approval.get("approverId"))
        //             .waitingDays((Integer) approval.get("waitingDays"))
        //             .requestedAt((LocalDateTime) approval.get("requestedAt"))
        //             .build()
        //     );
        // }

        log.info("[스케줄러] 결재 대기 독촉 알림 완료");
    }
}