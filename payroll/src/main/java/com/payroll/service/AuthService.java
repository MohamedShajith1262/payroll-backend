package com.payroll.service;

import com.payroll.dto.LoginRequest;
import com.payroll.dto.LoginResponse;
import com.payroll.dto.RegisterRequest;
import com.payroll.entity.AppUser;
import com.payroll.entity.Employee;
import com.payroll.enums.UserRole;
import com.payroll.exception.DuplicateResourceException;
import com.payroll.exception.InvalidCredentialsException;
import com.payroll.repository.AppUserRepository;
import com.payroll.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmployeeService employeeService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        return buildResponse(user);
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();

        if (appUserRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already taken");
        }
        Employee employee = employeeService.getEntityById(request.getEmployeeId());
        if (appUserRepository.existsByEmployeeId(employee.getId())) {
            throw new DuplicateResourceException("This employee already has an account");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.EMPLOYEE);
        user.setEmployee(employee);
        appUserRepository.save(user);

        return buildResponse(user);
    }

    private LoginResponse buildResponse(AppUser user) {
        Long employeeId = user.getEmployee() == null ? null : user.getEmployee().getId();
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name(), employeeId);
        return new LoginResponse(token, user.getUsername(), user.getRole().name(), employeeId);
    }
}