package org.example.vacationcalculator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CalculateRequest {
    private BigDecimal avgSalary;
    private LocalDate startDate;
    private Integer days;
}