package com.loanapp.loan_application.serviceimpl.customer;

import com.loanapp.loan_application.dto.customer.CustomerProfileResponse;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.repository.register.UserRepository;
import com.loanapp.loan_application.service.customer.CustomerService;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

 private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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

//    @Override
//    public CustomerProfileResponse updateProfile(String email, CustomerProfileResponse response) {
//
//        Customer customer = customerRepository.findByEmail(email).orElseThrow(()->
//                new RuntimeException("Customer not Not Exsits"));
//
//        customer.setFirstName(response.getFirstName());
//        customer.setLastName(response.getLastName());
//        customer.setAge(response.getAge());
//        customer.setEmploymentType(response.getEmploymentType());
//        customer.setPanCard(response.getPanCard());
//        customerRepository.save(customer);
//
//        CustomerProfileResponse customerProfileResponse = new CustomerProfileResponse();
//        customerProfileResponse.setEmail(customer.getEmail());
//        customerProfileResponse.setFirstName(customer.getFirstName());
//        customerProfileResponse.setLastName(customer.getLastName());
//        customerProfileResponse.setAge(customer.getAge());
//        customerProfileResponse.setEmploymentType(customer.getEmploymentType());
//        customerProfileResponse.setPanCard(customer.getPanCard());
//
//        return customerProfileResponse;
//    }

    @Override
    public CustomerProfileResponse updateProfile(String email, CustomerProfileResponse response) {

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer does not exist"));

        customer.setFirstName(response.getFirstName());
        customer.setLastName(response.getLastName());
        customer.setAge(response.getAge());
        customer.setEmploymentType(response.getEmploymentType());

        Customer updatedCustomer = customerRepository.save(customer);

        CustomerProfileResponse profileResponse = new CustomerProfileResponse();

        profileResponse.setFirstName(updatedCustomer.getFirstName());
        profileResponse.setLastName(updatedCustomer.getLastName());
        profileResponse.setEmail(updatedCustomer.getEmail());
        profileResponse.setMobileNo(updatedCustomer.getMobileNo());
        profileResponse.setEmploymentType(updatedCustomer.getEmploymentType());
        profileResponse.setPanCard(updatedCustomer.getPanCard());
        profileResponse.setAge(updatedCustomer.getAge());

        return profileResponse;
    }
}
