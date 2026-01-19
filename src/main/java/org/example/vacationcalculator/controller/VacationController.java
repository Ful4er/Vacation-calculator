package org.example.vacationcalculator.controller;

import lombok.RequiredArgsConstructor;
import org.example.vacationcalculator.dto.CalculateRequest;
import org.example.vacationcalculator.dto.CalculateResponse;
import org.example.vacationcalculator.service.VacationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VacationController {

    private final VacationService vacationService;

    @GetMapping("/calculate")
    public CalculateResponse calculate(@ModelAttribute CalculateRequest request) {

        if (request.getStartDate() != null) {
            return vacationService.calcWithStartAndDays(request).block();
        }

        return vacationService.calcBasic(request);
    }
}
