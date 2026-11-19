package com.payroll.dto;

import com.payroll.enums.SalaryType;
import com.payroll.enums.WorkRole;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotNull(message = "Role is required")
    private WorkRole role;

    @NotNull(message = "Salary type is required")
    private SalaryType salaryType;

    @NotNull(message = "Salary amount is required")
    @Positive(message = "Salary amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Invalid salary amount")
    private BigDecimal salaryAmount;
}