package com.payroll.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "payroll")
@Getter
@Setter
public class PayrollProperties {

    private int daysInMonth;
    private int hoursPerDay;
    private BigDecimal overtimeMultiplier;
}