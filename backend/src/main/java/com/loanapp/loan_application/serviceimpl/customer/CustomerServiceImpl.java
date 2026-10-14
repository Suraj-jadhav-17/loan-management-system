package com.loanapp.loan_application.serviceimpl.customer;

import com.loanapp.loan_application.dto.cuatomer.CustomerProfileResponse;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.customer.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

 private final CustomerRepository customerRepository;
 private final ModelMapper modelMapper;


    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;

        this.modelMapper = modelMapper;
    }

    @Override
    public CustomerProfileResponse findByProfile(String email) {
       Customer customer =  customerRepository.findByEmail(email).orElseThrow(()->
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

        Customer customer = customerRepository.findByEmail(email).orElseThrow(()->
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


}