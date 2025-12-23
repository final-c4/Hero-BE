package com.c4.hero.domain.vacation.google;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/google-calendar")
@RequiredArgsConstructor
public class GoogleCalendarSyncController {

    private final GoogleCalendarSyncService syncService;

    @PostMapping("/sync")
    public GoogleCalendarSyncService.SyncResult syncMonth(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return syncService.syncMonth(year, month);
    }
}
