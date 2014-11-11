package com.ucando.exercise.fileupload.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends AbstractEntity
{
    private final String username;

    private final String password;
    private final int    role;

    public Customer(String username, String password, int role)
    {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
