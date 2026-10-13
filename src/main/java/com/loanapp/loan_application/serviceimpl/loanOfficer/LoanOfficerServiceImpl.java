package com.loanapp.loan_application.serviceimpl.loanOfficer;

import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.response.CustomerResponse;
import com.loanapp.loan_application.service.loanOfficer.LoanOfficerService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class LoanOfficerServiceImpl implements LoanOfficerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;


    public LoanOfficerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<CustomerResponse> getAllLoans(int page, int limit, String sortBy, String direction) {

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Customer> customers;

        customers = customerRepository.findAll(pageable);


        return customers.map(customer -> modelMapper.map(customer, CustomerResponse.class));
    }
}
