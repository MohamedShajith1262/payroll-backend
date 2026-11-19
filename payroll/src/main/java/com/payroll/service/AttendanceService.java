package com.payroll.service;

import com.payroll.dto.AttendanceRequest;
import com.payroll.dto.AttendanceResponse;
import com.payroll.entity.Attendance;
import com.payroll.entity.Employee;
import com.payroll.enums.AttendanceStatus;
import com.payroll.exception.BusinessRuleException;
import com.payroll.exception.DuplicateResourceException;
import com.payroll.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeService employeeService;

    @Transactional
    public AttendanceResponse markAttendance(AttendanceRequest request) {
        Employee employee = employeeService.getEntityById(request.getEmployeeId());

        if (attendanceRepository.existsByEmployeeIdAndAttendanceDate(
                employee.getId(), request.getAttendanceDate())) {
            throw new DuplicateResourceException("Attendance already marked for this employee on "
                    + request.getAttendanceDate());
        }

        BigDecimal overtime = request.getOvertimeHours() == null
                ? BigDecimal.ZERO : request.getOvertimeHours();

        if (request.getStatus() == AttendanceStatus.ABSENT
                && overtime.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuleException("Overtime cannot be recorded for an absent employee");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAttendanceDate(request.getAttendanceDate());
        attendance.setStatus(request.getStatus());
        attendance.setOvertimeHours(overtime);

        try {
            return AttendanceResponse.from(attendanceRepository.saveAndFlush(attendance));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("Attendance already marked for this employee on "
                    + request.getAttendanceDate());
        }
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getByEmployee(Long employeeId) {
        employeeService.getEntityById(employeeId);
        return attendanceRepository.findByEmployeeIdOrderByAttendanceDateDesc(employeeId).stream()
                .map(AttendanceResponse::from)
                .toList();
    }
}