package com.payroll.repository;

import com.payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT COUNT(a) > 0 FROM Attendance a "
            + "WHERE a.employee.id = :employeeId AND a.attendanceDate = :date")
    boolean existsByEmployeeIdAndAttendanceDate(@Param("employeeId") Long employeeId,
                                                @Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a "
            + "WHERE a.employee.id = :employeeId "
            + "ORDER BY a.attendanceDate DESC")
    List<Attendance> findByEmployeeIdOrderByAttendanceDateDesc(@Param("employeeId") Long employeeId);

    @Query("SELECT a FROM Attendance a "
            + "WHERE a.employee.id = :employeeId "
            + "AND a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(@Param("employeeId") Long employeeId,
                                                              @Param("startDate") LocalDate startDate,
                                                              @Param("endDate") LocalDate endDate);
}