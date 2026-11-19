package com.payroll.dto;

import com.payroll.entity.LeaveRequest;
import com.payroll.enums.LeaveStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class LeaveResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private LocalDateTime createdAt;

    public static LeaveResponse from(LeaveRequest leave) {
        LeaveResponse response = new LeaveResponse();
        response.setId(leave.getId());
        response.setEmployeeId(leave.getEmployee().getId());
        response.setEmployeeName(leave.getEmployee().getName());
        response.setStartDate(leave.getStartDate());
        response.setEndDate(leave.getEndDate());
        response.setReason(leave.getReason());
        response.setStatus(leave.getStatus());
        response.setCreatedAt(leave.getCreatedAt());
        return response;
    }
}