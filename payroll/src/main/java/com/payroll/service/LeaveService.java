package com.payroll.service;

import com.payroll.dto.LeaveRequestDto;
import com.payroll.dto.LeaveResponse;
import com.payroll.dto.LeaveStatusRequest;
import com.payroll.entity.Employee;
import com.payroll.entity.LeaveRequest;
import com.payroll.enums.LeaveStatus;
import com.payroll.exception.BusinessRuleException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeService employeeService;

    @Transactional
    public LeaveResponse applyLeave(LeaveRequestDto request) {
        Employee employee = employeeService.getEntityById(request.getEmployeeId());

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessRuleException("End date cannot be before start date");
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setReason(request.getReason().trim());
        return LeaveResponse.from(leaveRepository.save(leave));
    }

    @Transactional
    public LeaveResponse updateStatus(Long leaveId, LeaveStatusRequest request) {
        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));

        if (request.getStatus() == LeaveStatus.PENDING) {
            throw new BusinessRuleException("Status must be APPROVED or REJECTED");
        }

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BusinessRuleException("Leave request is already " + leave.getStatus());
        }

        leave.setStatus(request.getStatus());
        return LeaveResponse.from(leave);
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> getAll() {
        return leaveRepository.findAllWithEmployee().stream()
                .map(LeaveResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> getByEmployee(Long employeeId) {
        employeeService.getEntityById(employeeId);   // 404 if employee doesn't exist
        return leaveRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId).stream()
                .map(LeaveResponse::from)
                .toList();
    }
}