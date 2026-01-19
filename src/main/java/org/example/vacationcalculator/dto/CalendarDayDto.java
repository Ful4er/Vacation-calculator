package org.example.vacationcalculator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CalendarDayDto {

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("month")
    private MonthInfo month;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("isWorkingDay")
    private Boolean isWorkingDay;

    @JsonProperty("isShortDay")
    private Boolean isShortDay;

    @JsonProperty("status")
    private Integer status;

    @Data
    @NoArgsConstructor
    public static class MonthInfo {
        @JsonProperty("name")
        private String name;

        @JsonProperty("id")
        private Integer id;
    }

}