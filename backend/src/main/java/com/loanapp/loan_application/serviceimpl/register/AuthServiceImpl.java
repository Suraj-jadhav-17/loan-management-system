package com.loanapp.loan_application.serviceimpl.register;

import com.loanapp.loan_application.dto.register.AuthFlowResponse;
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
import java.security.SecureRandom;
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
    private final TotpService totpService;

    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(UserRepository userRepository, CustomerRepository customerRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapperConfig, EmailService emailService, JwtService jwtService, TotpService totpService) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapperConfig = modelMapperConfig;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.totpService = totpService;
    }

    @Transactional
    @Override
    public User register(RegistrationRequest request) {

        if (customerRepository.existsByEmail(request.getEmail()) || userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (customerRepository.existsByAadhaarNo(request.getAadhaarNo())) {
            throw new RuntimeException("Aadhaar No. already registered");
        }

        if (customerRepository.existsByPanCard(request.getPanCard())) {
            throw new RuntimeException("PAN card already registered");
        }

        if (customerRepository.existsByMobileNo(request.getMobileNo())) {
            throw new RuntimeException("Mobile No. already registered");
        }

        Role role = roleRepository.findByRoleName("Customer").orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));

        BigDecimal allowedInvestment = request.getMonthlyIncome().multiply(BigDecimal.valueOf(40)).divide(BigDecimal.valueOf(100));

        if (request.getMonthlyInvestment() != null && request.getMonthlyInvestment().compareTo(allowedInvestment) > 0) {
            throw new RuntimeException("Monthly investment cannot exceed 40% of monthly income.");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        String emailOtp = String.valueOf(100000 + secureRandom.nextInt(900000));

        Customer customer = modelMapperConfig.map(request, Customer.class);

        customer.setPassword(passwordHash);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setOtp(emailOtp);

        customerRepository.save(customer);

        User user = modelMapperConfig.map(request, User.class);

        user.setPassword(passwordHash);
        user.setRole(role);
        user.setCustomer(customer);
        user.setMfaEnabled(false);
        user.setMfaSecret(null);

        User savedUser = userRepository.save(user);

        emailService.sendEmailOtp(savedUser.getEmail(), emailOtp);

        return savedUser;
    }

    @Transactional
    @Override
    public boolean checkOtp(String email, VerifyOtpRequest request) {

        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));

        if (customer.getOtp() != null && customer.getOtp().equals(request.getOtp())) {

            customer.setIsEmailVerified(true);
            customer.setOtp(null);

            customerRepository.save(customer);
            return true;
        }

        return false;
    }

    @Transactional
    @Override
    public AuthFlowResponse login(LoginDto loginDto) {

        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getRole() == null) {
            throw new RuntimeException("User role is not assigned");
        }

        if (user.getCustomer() != null && !Boolean.TRUE.equals(user.getCustomer().getIsEmailVerified())) {
            throw new RuntimeException("Please verify your email before logging in");
        }

        String purpose = user.isMfaEnabled() ? "MFA_LOGIN" : "MFA_SETUP";

        String verificationToken = jwtService.generateVerificationToken(user.getEmail(), purpose);

        return new AuthFlowResponse(user.isMfaEnabled() ? "MFA_REQUIRED" : "MFA_SETUP_REQUIRED", user.isMfaEnabled() ? "Enter your authenticator OTP or request recovery." : "Set up your authenticator app to continue.", user.getEmail(), verificationToken, null, null, null, null);
    }

    @Transactional
    @Override
    public AuthFlowResponse setupAuthenticator(String verificationToken) {

        String email = getVerifiedEmail(verificationToken, "MFA_SETUP");

        User user = getUser(email);

        String secret = user.getMfaSecret();
        if (secret == null || secret.isBlank()) {
            secret = totpService.generateSecret();
            user.setMfaSecret(secret);
            userRepository.save(user);
        }

        String setupUri = totpService.generateOtpUri(user.getEmail(), secret);
        String qrCodeDataUrl = totpService.generateQrCodeDataUrl(setupUri);

        return new AuthFlowResponse("AUTHENTICATOR_SETUP", "Scan this QR code with your authenticator app, then enter its 6-digit code.", user.getEmail(), verificationToken, setupUri, qrCodeDataUrl, null, null);
    }

    @Transactional
    @Override
    public LoginResponse verifyAuthenticatorSetup(String verificationToken, String otp) {

        String email = getVerifiedEmail(verificationToken, "MFA_SETUP");

        User user = getUser(email);

        if (!totpService.verifyOtp(user.getMfaSecret(), otp)) {
            throw new RuntimeException("Invalid authenticator OTP");
        }

        user.setMfaEnabled(true);
        userRepository.save(user);

        return issueTokens(user);
    }

    @Transactional
    @Override
    public LoginResponse verifyAuthenticatorLogin(String verificationToken, String otp) {

        String email = getVerifiedEmail(verificationToken, "MFA_LOGIN");

        User user = getUser(email);

        if (!user.isMfaEnabled()) {
            throw new RuntimeException("Authenticator is not enabled for this account");
        }

        if (!totpService.verifyOtp(user.getMfaSecret(), otp)) {
            throw new RuntimeException("Invalid authenticator OTP");
        }

        return issueTokens(user);
    }

    @Transactional
    @Override
    public String requestRecoveryOtp(String verificationToken) {

        String email = getVerifiedEmail(verificationToken, "MFA_LOGIN");

        User user = getUser(email);

        if (!user.isMfaEnabled()) {
            throw new RuntimeException("Authenticator recovery is not required");
        }

        String recoveryOtp = String.valueOf(100000 + secureRandom.nextInt(900000));

        user.setRecoveryOtp(recoveryOtp);
        user.setRecoveryOtpExpiry(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        emailService.sendNotificationEmail(user.getEmail(), "Authenticator Recovery OTP", "Your authenticator recovery OTP is: " + recoveryOtp + ". It expires in 5 minutes. " + "Do not share this OTP with anyone.");

        return "Recovery OTP sent to your registered email.";
    }

    @Transactional
    @Override
    public AuthFlowResponse verifyRecoveryOtp(String verificationToken, String otp) {

        String email = getVerifiedEmail(verificationToken, "MFA_LOGIN");

        User user = getUser(email);

        if (user.getRecoveryOtp() == null || user.getRecoveryOtpExpiry() == null || LocalDateTime.now().isAfter(user.getRecoveryOtpExpiry()) || !user.getRecoveryOtp().equals(otp)) {
            throw new RuntimeException("Invalid or expired recovery OTP");
        }

        user.setRecoveryOtp(null);
        user.setRecoveryOtpExpiry(null);
        user.setMfaEnabled(false);
        user.setMfaSecret(null);

        userRepository.save(user);

        String setupToken = jwtService.generateVerificationToken(user.getEmail(), "MFA_SETUP");

        return new AuthFlowResponse("MFA_RECOVERY_VERIFIED", "Recovery successful. Set up your new authenticator.", user.getEmail(), setupToken, null, null, null, null);
    }

    @Override
    public AuthFlowResponse resetAuthenticator(String verificationToken) {
        return setupAuthenticator(verificationToken);
    }

    private String getVerifiedEmail(String token, String expectedPurpose) {

        if (token == null || token.isBlank() || !jwtService.isValidVerificationToken(token, expectedPurpose)) {
            throw new RuntimeException("Invalid or expired verification token");
        }

        return jwtService.extractEmail(token);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private LoginResponse issueTokens(User user) {

        String role = user.getRole().getRoleName();

        String accessToken = jwtService.generateToken(user.getEmail(), role);

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new LoginResponse(accessToken, refreshToken);
    }
}