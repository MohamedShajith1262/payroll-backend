package com.payroll.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccessChecker {

    public void checkEmployeeAccess(Long employeeId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return;
        }
        Object tokenEmployeeId = auth.getDetails();
        if (tokenEmployeeId == null || !tokenEmployeeId.equals(employeeId)) {
            throw new AccessDeniedException("You can access only your own data");
        }
    }
}