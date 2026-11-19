package com.payroll.service;

import com.payroll.config.PayrollProperties;
import com.payroll.dto.PayrollResponse;
import com.payroll.entity.Attendance;
import com.payroll.entity.Employee;
import com.payroll.enums.AttendanceStatus;
import com.payroll.enums.SalaryType;
import com.payroll.exception.BusinessRuleException;
import com.payroll.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private static final int MONEY_SCALE = 2;   // final answers: 2 decimal places
    private static final int CALC_SCALE = 10;   // inside calculations: more precision

    private final AttendanceRepository attendanceRepository;
    private final EmployeeService employeeService;
    private final PayrollProperties payrollProperties;

    @Transactional(readOnly = true)
    public PayrollResponse calculate(Long employeeId, String month) {
        Employee employee = employeeService.getEntityById(employeeId);
        YearMonth yearMonth = parseMonth(month);

        List<Attendance> records = attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(
                employeeId, yearMonth.atDay(1), yearMonth.atEndOfMonth());

        long presentDays = records.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();
        long absentDays = records.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();
        BigDecimal overtimeHours = records.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .map(Attendance::getOvertimeHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ratePerDay;
        if (employee.getSalaryType() == SalaryType.MONTHLY) {
            ratePerDay = employee.getSalaryAmount().divide(
                    BigDecimal.valueOf(payrollProperties.getDaysInMonth()),
                    CALC_SCALE, RoundingMode.HALF_UP);
        } else {
            ratePerDay = employee.getSalaryAmount();
        }

        BigDecimal basePay = ratePerDay.multiply(BigDecimal.valueOf(presentDays));

        BigDecimal hourlyRate = ratePerDay.divide(
                BigDecimal.valueOf(payrollProperties.getHoursPerDay()),
                CALC_SCALE, RoundingMode.HALF_UP);
        BigDecimal overtimePay = hourlyRate
                .multiply(overtimeHours)
                .multiply(payrollProperties.getOvertimeMultiplier());

        basePay = basePay.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        overtimePay = overtimePay.setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        PayrollResponse response = new PayrollResponse();
        response.setEmployeeId(employee.getId());
        response.setEmployeeName(employee.getName());
        response.setMonth(yearMonth.toString());
        response.setSalaryType(employee.getSalaryType());
        response.setSalaryAmount(employee.getSalaryAmount());
        response.setRatePerDay(ratePerDay.setScale(MONEY_SCALE, RoundingMode.HALF_UP));
        response.setPresentDays(presentDays);
        response.setAbsentDays(absentDays);
        response.setBasePay(basePay);
        response.setOvertimeHours(overtimeHours);
        response.setOvertimePay(overtimePay);
        response.setGrossSalary(basePay.add(overtimePay));
        return response;
    }

    private YearMonth parseMonth(String month) {
        try {
            return YearMonth.parse(month);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new BusinessRuleException("Month must be in yyyy-MM format, for example 2026-11");
        }
    }
}