package com.payroll.dto;

import com.payroll.entity.Attendance;
import com.payroll.enums.AttendanceStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AttendanceResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private BigDecimal overtimeHours;

    public static AttendanceResponse from(Attendance attendance) {
        AttendanceResponse response = new AttendanceResponse();
        response.setId(attendance.getId());
        response.setEmployeeId(attendance.getEmployee().getId());
        response.setEmployeeName(attendance.getEmployee().getName());
        response.setAttendanceDate(attendance.getAttendanceDate());
        response.setStatus(attendance.getStatus());
        response.setOvertimeHours(attendance.getOvertimeHours());
        return response;
    }
}