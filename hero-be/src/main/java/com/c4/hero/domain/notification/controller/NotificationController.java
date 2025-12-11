package com.c4.hero.domain.notification.controller;

import com.c4.hero.domain.notification.dto.NotificationDTO;
import com.c4.hero.domain.notification.entity.Notification;
import com.c4.hero.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 * Class Name: Notification
 * Description: 알림 엔티티
 *
 * History
 * 2025/12/11 (최혜원) 최초 작성
 * </pre>
 *
 * @author 최혜원
 * @version 1.0
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name="알림 API")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림 목록 조회
     */
    @Operation(summary = "알림 목록 조회", description = "직원이 나의 알림 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Notification.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)})
    @GetMapping("/{employeeId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @PathVariable Integer employeeId){
        return ResponseEntity.ok(notificationService.selectAllNotification(employeeId));
    }

}
