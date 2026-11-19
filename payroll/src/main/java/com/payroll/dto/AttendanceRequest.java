package com.payroll.dto;

import com.payroll.enums.AttendanceStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
public class AttendanceRequest {

    @NotNull(message = "Employee id is required")
    private Long employeeId;

    @NotNull(message = "Attendance date is required")
    @PastOrPresent(message = "Attendance date cannot be in the future")
    private LocalDate attendanceDate;

    @NotNull(message = "Status is required")
    private AttendanceStatus status;

    @PositiveOrZero(message = "Overtime hours cannot be negative")
    @DecimalMax(value = "24", message = "Overtime hours cannot exceed 24")
    @Digits(integer = 2, fraction = 2, message = "Invalid overtime hours")
    private BigDecimal overtimeHours;
}
