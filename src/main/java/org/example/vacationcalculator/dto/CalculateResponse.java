package org.example.vacationcalculator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CalculateResponse {
    private BigDecimal total;
    private int paidDays;
}