package org.example.vacationcalculator.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
}
