package org.example.vacationcalculator.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.vacationcalculator.dto.CalculateRequest;
import org.example.vacationcalculator.dto.CalculateResponse;
import org.example.vacationcalculator.exception.BadRequestException;
import org.example.vacationcalculator.service.CalendarApiService;
import org.example.vacationcalculator.service.VacationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VacationServiceImpl implements VacationService {

    private final CalendarApiService calendarApiService;
    private static final BigDecimal CALC_DIVIDER = BigDecimal.valueOf(29.3);

    public CalculateResponse calcBasic(CalculateRequest request) {
        BigDecimal avgSalary = request.getAvgSalary();
        Integer days = request.getDays();

        if (avgSalary == null || days == null) {
            throw new BadRequestException("avgSalary and days must be provided");
        }

        BigDecimal dailyRate = avgSalary.divide(CALC_DIVIDER, 10, RoundingMode.HALF_UP);
        BigDecimal total = dailyRate.multiply(BigDecimal.valueOf(days))
                .setScale(2, RoundingMode.HALF_UP);

        return new CalculateResponse(total, days);
    }

    public Mono<CalculateResponse> calcWithStartAndDays(CalculateRequest request) {
        BigDecimal avgSalary = request.getAvgSalary();
        LocalDate startDate = request.getStartDate();
        Integer days = request.getDays();

        if (avgSalary == null || startDate == null || days == null) {
            throw new BadRequestException("avgSalary, startDate and days must be provided");
        }

        Mono<Long> paidDaysFlux = Flux.range(0, days)
                .map(startDate::plusDays)
                .flatMap(calendarApiService::isWorkingDay)
                .filter(Boolean::booleanValue)
                .count();

        return paidDaysFlux.map(paidDays -> {
            BigDecimal dailyRate = avgSalary.divide(CALC_DIVIDER, 10, RoundingMode.HALF_UP);
            BigDecimal total = dailyRate.multiply(BigDecimal.valueOf(paidDays))
                    .setScale(2, RoundingMode.HALF_UP);

            return new CalculateResponse(total, paidDays.intValue());
        });
    }
}
