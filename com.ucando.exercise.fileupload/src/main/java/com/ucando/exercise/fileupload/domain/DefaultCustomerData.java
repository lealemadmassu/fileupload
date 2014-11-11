package com.ucando.exercise.fileupload.domain;

import lombok.Value;

public class DefaultCustomerData
{

    public static final DefaultCustomer[] defaultCustomers = new DefaultCustomer[]{
                                                           new DefaultCustomer("user"),
                                                           new DefaultCustomer("admin"),
                                                           new DefaultCustomer("customer")
                                                           };

    @Value
    public static class DefaultCustomer
    {
        String name;
    }
}
