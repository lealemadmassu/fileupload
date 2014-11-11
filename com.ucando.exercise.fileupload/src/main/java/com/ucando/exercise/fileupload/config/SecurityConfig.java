package com.ucando.exercise.fileupload.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.ucando.exercise.fileupload.service.MongoUserDetailsService;

@ComponentScan
@Configuration
@EnableWebMvcSecurity
public class SecurityConfig extends WebMvcConfigurerAdapter
{
    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        registry.addViewController("/login").setViewName("login.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public ApplicationSecurity applicationSecurity()
    {
        return new ApplicationSecurity();
    }

    @Bean
    public AuthenticationSecurity authenticationSecurity()
    {
        return new AuthenticationSecurity();
    }

    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter
    {
        @Autowired
        private SecurityProperties security;

        @Override
        protected void configure(HttpSecurity http) throws Exception
        {
            http.authorizeRequests()
                    .antMatchers("/resources/**", "/login").permitAll()
                    .anyRequest().fullyAuthenticated()
                    .and()
                    .httpBasic()
                    .and().formLogin()
                    .loginPage("/login").permitAll()
                    .and().logout().logoutUrl("/login?logout").permitAll().and()
                    .csrf().disable();

        }
    }

    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    protected static class AuthenticationSecurity extends
            GlobalAuthenticationConfigurerAdapter
    {
        @Autowired
        private MongoUserDetailsService userDetailService;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception
        {
            final ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder(256);
            auth.userDetailsService(userDetailService).passwordEncoder(shaPasswordEncoder);
        }

    }
}
