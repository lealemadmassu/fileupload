package com.ucando.exercise.fileupload.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ucando.exercise.fileupload.domain.Customer;

@Service
public class MongoUserDetailsService implements UserDetailsService
{
    @Autowired
    private CustomerService customerService;

    private User            userdetails;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException
    {
        final boolean enabled = true;
        final boolean accountNonExpired = true;
        final boolean credentialsNonExpired = true;
        final boolean accountNonLocked = true;
        final Customer customer = this.customerService.findByUserName(username);

        this.userdetails = new User(customer.getUsername(),
                customer.getPassword(),
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                getAuthorities(customer.getRole()));
        return this.userdetails;
    }

    public List<GrantedAuthority> getAuthorities(final Integer role)
    {
        final List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        if (role.intValue() == 1)
        {
            authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        }
        else if (role.intValue() == 2)
        {
            authList.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authList;
    }

}
