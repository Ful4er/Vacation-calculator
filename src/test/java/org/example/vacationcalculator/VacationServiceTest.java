package org.example.vacationcalculator;

import org.example.vacationcalculator.dto.CalculateRequest;
import org.example.vacationcalculator.dto.CalculateResponse;
import org.example.vacationcalculator.exception.BadRequestException;
import org.example.vacationcalculator.service.CalendarApiService;
import org.example.vacationcalculator.service.impl.VacationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VacationServiceTest {

    private CalendarApiService calendarApiService;
    private VacationServiceImpl service;

    @BeforeEach
    void setUp() {
        calendarApiService = mock(CalendarApiService.class);
        service = new VacationServiceImpl(calendarApiService);
    }

    @Test
    void basicCalculation() {
        BigDecimal avgSalary = BigDecimal.valueOf(60000);

        BigDecimal expected = avgSalary
                .divide(BigDecimal.valueOf(29.3), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(10))
                .setScale(2, RoundingMode.HALF_UP);

        CalculateRequest request = new CalculateRequest();
        request.setAvgSalary(avgSalary);
        request.setDays(10);

        CalculateResponse response = service.calcBasic(request);

        assertThat(response.getTotal()).isEqualTo(expected);
        assertThat(response.getPaidDays()).isEqualTo(10);
    }

    @Test
    void basicBadRequest_whenNullSalary() {
        CalculateRequest request = new CalculateRequest();
        request.setDays(5);

        assertThrows(BadRequestException.class, () -> service.calcBasic(request));
    }

    @Test
    void basicBadRequest_whenNullDays() {
        CalculateRequest request = new CalculateRequest();
        request.setAvgSalary(BigDecimal.valueOf(50000));

        assertThrows(BadRequestException.class, () -> service.calcBasic(request));
    }

    @Test
    void calcWithStartAndDays_allWorkingDays() {
        LocalDate start = LocalDate.of(2026, 1, 5);

        for (int i = 0; i < 5; i++) {
            when(calendarApiService.isWorkingDay(start.plusDays(i)))
                    .thenReturn(Mono.just(true));
        }

        CalculateRequest request = new CalculateRequest();
        request.setAvgSalary(BigDecimal.valueOf(58000));
        request.setStartDate(start);
        request.setDays(5);

        BigDecimal expected = BigDecimal.valueOf(58000)
                .divide(BigDecimal.valueOf(29.3), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(5))
                .setScale(2, RoundingMode.HALF_UP);

        StepVerifier.create(service.calcWithStartAndDays(request))
                .assertNext(response -> {
                    assertThat(response.getPaidDays()).isEqualTo(5);
                    assertThat(response.getTotal()).isEqualTo(expected);
                })
                .verifyComplete();
    }

    @Test
    void calcWithStartAndDays_someNonWorkingDays() {
        LocalDate start = LocalDate.of(2026, 1, 1);

        when(calendarApiService.isWorkingDay(start)).thenReturn(Mono.just(false));
        when(calendarApiService.isWorkingDay(start.plusDays(1))).thenReturn(Mono.just(true));
        when(calendarApiService.isWorkingDay(start.plusDays(2))).thenReturn(Mono.just(false));

        CalculateRequest request = new CalculateRequest();
        request.setAvgSalary(BigDecimal.valueOf(40000));
        request.setStartDate(start);
        request.setDays(3);

        BigDecimal expected = BigDecimal.valueOf(40000)
                .divide(BigDecimal.valueOf(29.3), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(1))
                .setScale(2, RoundingMode.HALF_UP);

        StepVerifier.create(service.calcWithStartAndDays(request))
                .assertNext(response -> {
                    assertThat(response.getPaidDays()).isEqualTo(1);
                    assertThat(response.getTotal()).isEqualTo(expected);
                })
                .verifyComplete();
    }

    @Test
    void calcWithStartAndDays_missingParams_throwsBadRequest() {
        CalculateRequest request = new CalculateRequest();
        assertThrows(BadRequestException.class, () -> service.calcWithStartAndDays(request).block());
    }
}
