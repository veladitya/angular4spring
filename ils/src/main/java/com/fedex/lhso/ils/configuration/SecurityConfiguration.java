package com.fedex.lhso.ils.configuration;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER) 
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/login.html")
				.permitAll().anyRequest().authenticated().and()
				.httpBasic().and()
				.logout().invalidateHttpSession(false).logoutSuccessUrl("/login.html")
				.and()
			    .csrf()
			    .disable(); 
			    //csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
	}
}