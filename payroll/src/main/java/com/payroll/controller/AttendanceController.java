package com.payroll.controller;

import com.payroll.dto.AttendanceRequest;
import com.payroll.dto.AttendanceResponse;
import com.payroll.security.AccessChecker;
import com.payroll.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AccessChecker accessChecker;

    @PostMapping
    public ResponseEntity<AttendanceResponse> mark(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.markAttendance(request));
    }

    @GetMapping("/employee/{employeeId}")
    public List<AttendanceResponse> getByEmployee(@PathVariable Long employeeId) {
        accessChecker.checkEmployeeAccess(employeeId);
        return attendanceService.getByEmployee(employeeId);
    }
}