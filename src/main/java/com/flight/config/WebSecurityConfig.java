/**
 * 
 */
package com.flight.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.flight.security.JwtFilter;
import com.flight.service.IAccountService;

/**
 * @author Vincent
 *
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	public static final String FLIGHT_API = "/flight-webservices/api/v1.0/flights/";

	@Autowired
	private IAccountService accountService;
	
	@Autowired
	JwtFilter jwtFilter;

	@Autowired
	protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(accountService).passwordEncoder(passwordEncoder());
	}

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
    
    @Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		super.configure(auth);
//	}

	public WebSecurityConfig() {
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		super.configure(http);
		http.csrf().disable()
		.authorizeRequests()
		.antMatchers(FLIGHT_API + "authenticate", FLIGHT_API + "register")
		.permitAll()
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		.anyRequest().authenticated()
		.and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
