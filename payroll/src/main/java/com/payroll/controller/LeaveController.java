package com.payroll.controller;

import com.payroll.dto.LeaveRequestDto;
import com.payroll.dto.LeaveResponse;
import com.payroll.dto.LeaveStatusRequest;
import com.payroll.security.AccessChecker;
import com.payroll.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;
    private final AccessChecker accessChecker;

    @PostMapping
    public ResponseEntity<LeaveResponse> apply(@Valid @RequestBody LeaveRequestDto request) {
        accessChecker.checkEmployeeAccess(request.getEmployeeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveService.applyLeave(request));
    }

    @PatchMapping("/{id}/status")
    public LeaveResponse updateStatus(@PathVariable Long id,
                                      @Valid @RequestBody LeaveStatusRequest request) {
        return leaveService.updateStatus(id, request);
    }

    @GetMapping
    public List<LeaveResponse> getAll() {
        return leaveService.getAll();
    }

    @GetMapping("/employee/{employeeId}")
    public List<LeaveResponse> getByEmployee(@PathVariable Long employeeId) {
        accessChecker.checkEmployeeAccess(employeeId);
        return leaveService.getByEmployee(employeeId);
    }
}