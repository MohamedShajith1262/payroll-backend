package com.payroll.dto;

import com.payroll.entity.Employee;
import com.payroll.enums.SalaryType;
import com.payroll.enums.WorkRole;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EmployeeResponse {

    private Long id;
    private String name;
    private WorkRole role;
    private SalaryType salaryType;
    private BigDecimal salaryAmount;
    private LocalDateTime createdAt;

    public static EmployeeResponse from(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setRole(employee.getRole());
        response.setSalaryType(employee.getSalaryType());
        response.setSalaryAmount(employee.getSalaryAmount());
        response.setCreatedAt(employee.getCreatedAt());
        return response;
    }
}