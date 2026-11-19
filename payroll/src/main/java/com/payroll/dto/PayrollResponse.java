package com.payroll.dto;

import com.payroll.enums.SalaryType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PayrollResponse {

    private Long employeeId;
    private String employeeName;
    private String month;
    private SalaryType salaryType;
    private BigDecimal salaryAmount;
    private BigDecimal ratePerDay;
    private long presentDays;
    private long absentDays;
    private BigDecimal basePay;
    private BigDecimal overtimeHours;
    private BigDecimal overtimePay;
    private BigDecimal grossSalary;
}