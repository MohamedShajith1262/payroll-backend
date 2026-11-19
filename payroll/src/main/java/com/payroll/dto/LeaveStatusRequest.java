package com.payroll.dto;

import com.payroll.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LeaveStatusRequest {

    @NotNull(message = "Status is required")
    private LeaveStatus status;
}