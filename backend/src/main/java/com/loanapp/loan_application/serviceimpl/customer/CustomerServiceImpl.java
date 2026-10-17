package com.loanapp.loan_application.serviceimpl.customer;

import com.loanapp.loan_application.dto.cuatomer.CustomerProfileResponse;
import com.loanapp.loan_application.dto.cuatomer.EmiSummaryResponse;
import com.loanapp.loan_application.dto.dashBoard.CustomerDashboardResponse;
import com.loanapp.loan_application.dto.payment.LoanPaymentRecordDto;
import com.loanapp.loan_application.entity.emischeduler.EmiSchedules;
import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.entity.loan.LoanStatus;
import com.loanapp.loan_application.entity.payment.LoanPayments;
import com.loanapp.loan_application.entity.payment.PaymentStatus;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.emischedulerepository.EmiScheduleRepository;
import com.loanapp.loan_application.repository.loan.LoanDealRepo;
import com.loanapp.loan_application.repository.payment.LoanPaymentsRepository;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.customer.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final LoanDealRepo loanDealRepo;
    private final EmiScheduleRepository emiScheduleRepository;
    private final LoanPaymentsRepository loanPaymentsRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, LoanDealRepo loanDealRepo, EmiScheduleRepository emiScheduleRepository, LoanPaymentsRepository loanPaymentsRepository) {
        this.customerRepository = customerRepository;

        this.modelMapper = modelMapper;
        this.loanDealRepo = loanDealRepo;
        this.emiScheduleRepository = emiScheduleRepository;
        this.loanPaymentsRepository = loanPaymentsRepository;
    }

    @Override
    public CustomerProfileResponse findByProfile(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("Customer not Not Exsits"));


        CustomerProfileResponse response = new CustomerProfileResponse();


        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setMobileNo(customer.getMobileNo());
        response.setAge(customer.getAge());
        response.setPanCard(customer.getPanCard());
        response.setEmploymentType(customer.getEmploymentType());


        return response;
    }

    @Override
    public CustomerProfileResponse updateProfile(String email, CustomerProfileResponse response) {

        Customer customer = customerRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("Customer not Not Exsits"));

        customer.setFirstName(response.getFirstName());
        customer.setLastName(response.getLastName());
        customer.setAge(response.getAge());
        customer.setEmploymentType(response.getEmploymentType());
        customer.setPanCard(response.getPanCard());
        customerRepository.save(customer);

        CustomerProfileResponse customerProfileResponse = new CustomerProfileResponse();
        customerProfileResponse.setEmail(customer.getEmail());
        customerProfileResponse.setFirstName(customer.getFirstName());
        customerProfileResponse.setLastName(customer.getLastName());
        customerProfileResponse.setAge(customer.getAge());
        customerProfileResponse.setEmploymentType(customer.getEmploymentType());
        customerProfileResponse.setPanCard(customer.getPanCard());

        return customerProfileResponse;
    }

    @Override
    public List<CustomerProfileResponse> getAllCustomerList() {

        List<Customer> customers = customerRepository.findAll();

        return customers.stream()
                .map(customer -> modelMapper.map(customer, CustomerProfileResponse.class))
                .toList();
    }


    @Override
    public CustomerDashboardResponse getDashboard(String email) {

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<LoanDeal> loans = loanDealRepo
                .getLoanDealByCustomer_customerId(customer.getCustomerId());

        CustomerDashboardResponse response = new CustomerDashboardResponse();

        long activeLoan = 0;

        for (LoanDeal loan : loans) {
            if (loan.getStatus() == LoanStatus.ACTIVE) {
                activeLoan++;
            }
        }

        response.setActiveLoan(activeLoan);
        response.setTotalApplication(loans.size());

        BigDecimal totalPaidAmount = BigDecimal.ZERO;
        long paidEmi = 0;
        EmiSchedules upcomingEmi = null;

        for (LoanDeal loan : loans) {

            List<EmiSchedules> emiSchedules =
                    emiScheduleRepository.findByLoanAccountId(loan.getId());

            for (EmiSchedules emi : emiSchedules) {

                if (emi.getPaymentStatus() == PaymentStatus.PAID) {

                    if (emi.getEmi() != null) {
                        totalPaidAmount = totalPaidAmount.add(emi.getEmi());
                    }

                    paidEmi++;
                }

                if (emi.getPaymentStatus() == PaymentStatus.PENDING
                        && emi.getDueDate() != null
                        && !emi.getDueDate().isBefore(LocalDate.now())) {

                    if (upcomingEmi == null
                            || emi.getDueDate().isBefore(upcomingEmi.getDueDate())) {
                        upcomingEmi = emi;
                    }
                }
            }
        }

        response.setTotalPaidAmount(totalPaidAmount);
        response.setPaidEmi(paidEmi);

        if (upcomingEmi != null) {
            response.setUpcomingEmi(upcomingEmi.getEmi());
            response.setUpcomingEmiDate(upcomingEmi.getDueDate());
        } else {
            response.setUpcomingEmi(BigDecimal.ZERO);
            response.setUpcomingEmiDate(null);
        }

        return response;
    }


    @Override
    public EmiSummaryResponse getEmiSummary(String email) {

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<LoanDeal> loans = loanDealRepo
                .getLoanDealByCustomer_customerId(customer.getCustomerId());

        EmiSummaryResponse response = new EmiSummaryResponse();

        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal remainingAmount = BigDecimal.ZERO;

        long paidCount = 0;
        long remainingCount = 0;

        EmiSchedules nextEmi = null;

        for (LoanDeal loan : loans) {

            List<EmiSchedules> emis =
                    emiScheduleRepository.findByLoanAccountId(loan.getId());

            for (EmiSchedules emi : emis) {

                if (emi.getPaymentStatus() == PaymentStatus.PAID) {
                    paidCount++;
                    paidAmount = paidAmount.add(
                            emi.getEmi() == null ? BigDecimal.ZERO : emi.getEmi()
                    );
                }

                if (emi.getPaymentStatus() == PaymentStatus.PENDING) {
                    remainingCount++;
                    remainingAmount = remainingAmount.add(
                            emi.getEmi() == null ? BigDecimal.ZERO : emi.getEmi()
                    );

                    if (emi.getDueDate() != null
                            && !emi.getDueDate().isBefore(LocalDate.now())) {

                        if (nextEmi == null
                                || emi.getDueDate().isBefore(nextEmi.getDueDate())) {
                            nextEmi = emi;
                        }
                    }
                }
            }
        }

        response.setTotalPaidAmount(paidAmount);
        response.setTotalPaidEmi(paidCount);
        response.setRemainingEmiAmount(remainingAmount);
        response.setRemainingEmiNumber(remainingCount);


        response.setUpcomingEmiAmount(
                nextEmi == null ? BigDecimal.ZERO : nextEmi.getEmi()
        );

        response.setUpcomingEmiNumber(
                nextEmi == null ? null : nextEmi.getInstallmentNo()
        );

        response.setUpcomingEmiDate(
                nextEmi == null ? null : nextEmi.getDueDate()
        );

        return response;
    }

    @Override
    public LoanPaymentRecordDto getReciptsPayment(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("Customer not found"));

        LoanPayments loanPayments = loanPaymentsRepository.findById(customer.getCustomerId()).orElseThrow(() ->
                new RuntimeException("No record found"));

        LoanPaymentRecordDto loanPaymentRecordDto = new LoanPaymentRecordDto();

        loanPaymentRecordDto.setLoanAccountId(loanPayments.getLoanAccountId());
        loanPaymentRecordDto.setPaymentId(loanPayments.getPaymentId());
        loanPaymentRecordDto.setPaymentAmount(loanPayments.getPaymentAmount());
        loanPaymentRecordDto.setPaymentName(loanPayments.getPaymentName());
        loanPaymentRecordDto.setPaymentStatus(String.valueOf(loanPayments.getPaymentStatus()));

        return loanPaymentRecordDto;
    }


}