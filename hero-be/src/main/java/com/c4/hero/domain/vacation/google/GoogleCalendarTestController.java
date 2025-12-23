package com.c4.hero.domain.vacation.google;

import com.google.api.services.calendar.model.Events;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-calendar")
public class GoogleCalendarTestController {

    private final GoogleCalendarService service;

    @GetMapping("/ping")
    public Events ping() throws Exception {
        Instant now = Instant.now();
        return service.listEvents(now.minus(30, ChronoUnit.DAYS), now.plus(90, ChronoUnit.DAYS));
    }
}
