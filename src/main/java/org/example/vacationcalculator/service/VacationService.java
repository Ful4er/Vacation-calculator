package org.example.vacationcalculator.service;

import org.example.vacationcalculator.dto.CalculateRequest;
import org.example.vacationcalculator.dto.CalculateResponse;
import reactor.core.publisher.Mono;

public interface VacationService {
    CalculateResponse calcBasic(CalculateRequest request);
    Mono<CalculateResponse> calcWithStartAndDays(CalculateRequest request);
}
