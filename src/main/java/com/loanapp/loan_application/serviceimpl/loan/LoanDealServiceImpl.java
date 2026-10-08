package com.loanapp.loan_application.serviceimpl.loan;

import com.loanapp.loan_application.dto.loan.LoanDealRequestDto;
import com.loanapp.loan_application.dto.loan.LoanDealResponseDto;
import com.loanapp.loan_application.entity.Customer;
import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.entity.loan.LoanType;
import com.loanapp.loan_application.entity.scorecard.ScoreCard;
import com.loanapp.loan_application.exception.InvalidInputException;
import com.loanapp.loan_application.exception.ResourceNotFoundException;
import com.loanapp.loan_application.repository.CustomerRepository;
import com.loanapp.loan_application.repository.loan.LoanDealRepo;

import com.loanapp.loan_application.repository.scorecard.ScoreCardRepo;
import com.loanapp.loan_application.service.loan.LoanDealService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanDealServiceImpl  implements LoanDealService {

    private final LoanDealRepo dealRepo;
    private final CustomerRepository customerRepo;
    private final ScoreCardRepo cardRepo;
    private final ModelMapper mapper;

    @Override
    public LoanDealResponseDto createLoanDeal(LoanDealRequestDto request) {
        if (request==null || request.getCustomerId()==null || request.getLoanType()==null){
            throw new InvalidInputException("Invalid input: Customer or Loan Type Can't Be Empty");
        }
        if(request.getAmount()==null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidInputException("Invalid input: Amount Can't Be Empty Or Less Than 0");
        }
        Customer customer = customerRepo.findById(request.getCustomerId()).orElseThrow(()->new ResourceNotFoundException("Customer Not Found"));
        ScoreCard card = cardRepo.getScoreCardByCustomer_CustomerId(customer.getCustomerId()).orElseThrow(()->new ResourceNotFoundException("Card Not Found"));

        LoanDeal deal = LoanDeal.builder()
                .amount(request.getAmount())
                .approvedAmount(card.getEligibleLoanAmount())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankName(request.getBankName())
                .customer(customer)
                .emiAmount(getEmiAmount(request.getAmount(),request.getLoanType(), request.getTenureMonths()))
                .emiDay(request.getEmiDay())
                .ifscCode(request.getIfscCode())
                .interestRate(request.getLoanType().equals(LoanType.CAR_LOAN)? new BigDecimal(10.0)
                        : request.getLoanType().equals(LoanType.HOME_LOAN)?new BigDecimal(15.0): BigDecimal.ZERO)
                .loanType(request.getLoanType())
                .tenureMonths(request.getTenureMonths())
                .build();

        LoanDeal savedDeal =dealRepo.save(deal);
        return mapper.map(savedDeal, LoanDealResponseDto.class);

    }

    @Override
    public LoanDealResponseDto getLoanDealById(Long id) {
        LoanDeal deal = dealRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("LoanDeal Not Found"));
        return mapper.map(deal, LoanDealResponseDto.class);
    }

    @Override
    public List<LoanDealResponseDto> getLoanDealByCustomerId(Long customerId) {
        return dealRepo.getLoanDealByCustomer_customerId(customerId).stream().map(deal-> mapper.map(deal, LoanDealResponseDto.class)).toList();
    }

    @Override
    public List<LoanDealResponseDto> getLoanDealByLoanType(LoanType loanType) {
        return dealRepo.getLoanDealByLoanType(loanType).stream().map(deal-> mapper.map(deal, LoanDealResponseDto.class)).toList();
    }
    private BigDecimal getEmiAmount(BigDecimal amount, LoanType loanType, Long tenureMonths) {
        return switch (loanType){
            case CAR_LOAN ->  emiFormula(amount, new BigDecimal(10.0) ,tenureMonths);
            case HOME_LOAN -> emiFormula(amount, new BigDecimal(15.0), tenureMonths);
            default -> throw new InvalidInputException("Invalid Input");
        };

    }
    private BigDecimal emiFormula (BigDecimal amount, BigDecimal interestRate,Long tenureMonths){                 // Emi = P*r*(1+r)^n
        BigDecimal rate = interestRate.divide(BigDecimal.valueOf(12),10,BigDecimal.ROUND_HALF_UP)           //======================
                .divide(BigDecimal.valueOf(100),10,BigDecimal.ROUND_HALF_UP)   ;                           //        (1+r)^n -1
       BigDecimal power =BigDecimal.valueOf(Math.pow(BigDecimal.ONE.add(rate).doubleValue(), tenureMonths));
        BigDecimal numerator = amount.multiply(rate).multiply(power);

        BigDecimal denominator = power.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
