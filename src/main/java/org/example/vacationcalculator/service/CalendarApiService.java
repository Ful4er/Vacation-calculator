package org.example.vacationcalculator.service;

import org.example.vacationcalculator.dto.CalendarDayDto;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface CalendarApiService {
    Mono<CalendarDayDto> getCalendarDayInfo(LocalDate date);
    Mono<Boolean> isWorkingDay(LocalDate date);
}
