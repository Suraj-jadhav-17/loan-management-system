package com.loanapp.loan_application.serviceimpl.register;


import com.loanapp.loan_application.dto.register.LoginDto;
import com.loanapp.loan_application.dto.register.RegistrationRequest;
import com.loanapp.loan_application.dto.register.VerifyOtpRequest;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.entity.register.Role;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.repository.register.RoleRepository;
import com.loanapp.loan_application.repository.register.UserRepository;
import com.loanapp.loan_application.response.LoginResponse;
import com.loanapp.loan_application.service.jwt.JwtService;
import com.loanapp.loan_application.service.mail.EmailService;
import com.loanapp.loan_application.service.registration.AuthService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapperConfig;
    private final EmailService emailService;
    private final JwtService jwtService;


    public AuthServiceImpl(UserRepository userRepository, CustomerRepository customerRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapperConfig, EmailService emailService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapperConfig = modelMapperConfig;
        this.emailService = emailService;
        this.jwtService = jwtService;
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

        if (customerRepository.existsByAadhaarNo(request.getAadhaarNo())) {
            throw new RuntimeException("Aadhaar No. already registered");
        }

        if (customerRepository.existsByPanCard(request.getPanCard())) {
            throw new RuntimeException("PanCard  already registered");
        }

        if (customerRepository.existsByMobileNo(request.getMobileNo())) {
            throw new RuntimeException("Mobile No. already registered");
        }

        Role role = roleRepository.findByRoleName("Customer").orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));

        String passwordHash = passwordEncoder.encode(request.getPassword());

        BigDecimal investmentAmount = request.getMonthlyIncome().multiply(BigDecimal.valueOf(40)).divide(BigDecimal.valueOf(100));

        if (request.getMonthlyInvestment().compareTo(investmentAmount) > 0) {
            throw new RuntimeException(
                    "You are not eligible to take the loan because your monthly investment exceeds the allowed limit."
            );
        }

        String emailOtp = String.valueOf((int)(Math.random() * 900000) + 100000);
        Customer customer = modelMapperConfig.map(request, Customer.class);
        customer.setPassword(passwordHash);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setOtp(emailOtp);
        customerRepository.save(customer);

        User user = modelMapperConfig.map(request, User.class);
        user.setPassword(passwordHash);
        user.setRole(role);
        user.setCustomer(customer);
        User savedUser = userRepository.save(user);

        emailService.sendOtp(user.getEmail(), request.getPassword());
        emailService.sendEmailOtp(user.getEmail(),emailOtp);
        return savedUser;
    }

    @Override
    public LoginResponse login(LoginDto loginDto) {
        User user = modelMapperConfig.map(loginDto, User.class);
        User checkEmail = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("Email not found"));

        if (!passwordEncoder.matches(user.getPassword(), checkEmail.getPassword())) {
            throw new RuntimeException("Password not match");
        }

        String accessToken = jwtService.generateToken(checkEmail.getEmail(), checkEmail.getRole().getRoleName());
        String refreshToken = jwtService.generateRefreshToken(accessToken);


        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public boolean checkOtp(String email, VerifyOtpRequest otp) {

        Customer userOtp = modelMapperConfig.map(otp,Customer.class);
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        System.out.println("Request OTP: [" + userOtp.getOtp() + "]");
        System.out.println("Database OTP: [" + customer.getOtp() + "]");

        if (customer.getOtp() != null && customer.getOtp().equals(userOtp.getOtp())) {

            System.out.println("OTP matched: true");

            customer.setIsEmailVerified(true);
            customer.setOtp(null);

            customerRepository.save(customer);

            return true;
        }

        System.out.println("OTP matched: false");
        return false;
    }
}