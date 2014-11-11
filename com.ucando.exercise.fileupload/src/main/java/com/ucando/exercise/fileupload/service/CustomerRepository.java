package com.ucando.exercise.fileupload.service;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ucando.exercise.fileupload.domain.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String>
{
    Customer findByUsername(final String username);
}
