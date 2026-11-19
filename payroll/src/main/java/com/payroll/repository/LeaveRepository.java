package com.payroll.repository;

import com.payroll.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    @Query("SELECT l FROM LeaveRequest l "
            + "WHERE l.employee.id = :employeeId "
            + "ORDER BY l.createdAt DESC")
    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(@Param("employeeId") Long employeeId);

    @Query("SELECT l FROM LeaveRequest l JOIN FETCH l.employee ORDER BY l.createdAt DESC")
    List<LeaveRequest> findAllWithEmployee();
}