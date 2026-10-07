package com.loanapp.loan_application.serviceimpl;


import com.loanapp.loan_application.dto.RegistrationRequest;
import com.loanapp.loan_application.entity.registration.Customer;
import com.loanapp.loan_application.entity.registration.Role;
import com.loanapp.loan_application.entity.registration.User;
import com.loanapp.loan_application.repository.registration.CustomerRepository;
import com.loanapp.loan_application.repository.registration.RoleRepository;
import com.loanapp.loan_application.repository.registration.UserRepository;
import com.loanapp.loan_application.service.mail.EmailService;
import com.loanapp.loan_application.service.registration.AuthService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapperConfig;
    private final EmailService emailService;


    public AuthServiceImpl(UserRepository userRepository, CustomerRepository customerRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapperConfig, EmailService emailService) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapperConfig = modelMapperConfig;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public User register(RegistrationRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role role = roleRepository.findByRoleName("Customer").orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));

        String passwordHash = passwordEncoder.encode(request.getPassword());

        Customer customer = modelMapperConfig.map(request, Customer.class);
        customer.setPassword(passwordHash);
        customer.setCreatedAt(LocalDateTime.now());
        customerRepository.save(customer);

        User user = modelMapperConfig.map(request, User.class);
        user.setPassword(passwordHash);
        user.setRole(role);
        user.setCustomer(customer);
        User savedUser = userRepository.save(user);
      emailService.sendOtp(user.getEmail(),request.getPassword());
        return savedUser;
    }
}