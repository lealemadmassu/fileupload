package com.ucando.exercise.fileupload.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ucando.exercise.fileupload.domain.Customer;
import com.ucando.exercise.fileupload.domain.DefaultCustomerData;
import com.ucando.exercise.fileupload.domain.DefaultCustomerData.DefaultCustomer;

@Service
public class CustomerService
{
    @Autowired
    CustomerRepository customerRepository;

    @PostConstruct
    public void insertDefaultCustomers()
    {
        final ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder(256);

        for (DefaultCustomer defaultCustomer : DefaultCustomerData.defaultCustomers)
        {
            Customer customer = findByUserName(defaultCustomer.getName());

            if (customer == null)
            {
                save(new Customer(defaultCustomer.getName(), shaPasswordEncoder.encodePassword(defaultCustomer.getName(), null), 1));
            }
        }
    }

    public Customer findByUserName(final String username)
    {
        return this.customerRepository.findByUsername(username);
    }

    public Customer save(final Customer customer)
    {
        return this.customerRepository.save(customer);
    }

    public List<Customer> findAll()
    {
        return customerRepository.findAll();
    }
}
