package org.example.vacationcalculator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.vacationcalculator.dto.CalendarDayDto;
import org.example.vacationcalculator.exception.ExternalServiceException;
import org.example.vacationcalculator.service.CalendarApiService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarApiServiceImpl implements CalendarApiService {

    private final WebClient webClient;

    public Mono<CalendarDayDto> getCalendarDayInfo(LocalDate date) {
        log.info("Requesting calendar day: " + date);

        return webClient.get()
                .uri("/{year}/{month}/{day}",
                        date.getYear(),
                        date.getMonthValue(),
                        date.getDayOfMonth())
                .retrieve()
                .bodyToMono(CalendarDayDto.class)
                .doOnError(ex -> log.error("Calendar API error", ex));
    }

    public Mono<Boolean> isWorkingDay(LocalDate date) {
        return getCalendarDayInfo(date)
                .doOnNext(dto -> log.info("CalendarDayDto: {}", dto))
                .map(dto -> Boolean.TRUE.equals(dto.getIsWorkingDay()))
                .onErrorMap(ex -> new ExternalServiceException("Calendar API failed", ex));
    }

}
