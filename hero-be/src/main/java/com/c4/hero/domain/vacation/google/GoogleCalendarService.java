package com.c4.hero.domain.vacation.google;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final Calendar calendar;

    @Value("${google.calendar.calendar-id}")
    private String calendarId;

    public Events listEvents(Instant timeMin, Instant timeMax) throws Exception {
        return calendar.events().list(calendarId)
                .setSingleEvents(true)
                .setOrderBy("startTime")
                .setTimeMin(new com.google.api.client.util.DateTime(timeMin.toString()))
                .setTimeMax(new com.google.api.client.util.DateTime(timeMax.toString()))
                .execute();
    }
}
