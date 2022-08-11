/**
 * 
 */
package com.flight.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.flight.dto.AccountDto;
import com.flight.models.Account;

/**
 * @author Vincent
 *
 */
public interface IAccountService extends UserDetailsService {
	Account addAccount(AccountDto accountDto);
}
