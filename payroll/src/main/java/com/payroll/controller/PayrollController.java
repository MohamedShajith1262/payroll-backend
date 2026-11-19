package com.payroll.controller;

import com.payroll.dto.PayrollResponse;
import com.payroll.security.AccessChecker;
import com.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;
    private final AccessChecker accessChecker;

    @GetMapping("/{employeeId}")
    public PayrollResponse calculate(@PathVariable Long employeeId,
                                     @RequestParam String month) {
        accessChecker.checkEmployeeAccess(employeeId);
        return payrollService.calculate(employeeId, month);
    }
}