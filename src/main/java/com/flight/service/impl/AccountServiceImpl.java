/**
 * 
 */
package com.flight.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flight.dto.AccountDto;
import com.flight.models.Account;
import com.flight.repository.IAccountRepository;
import com.flight.service.IAccountService;

/**
 * @author Vincent
 *
 */
@Service
public class AccountServiceImpl implements IAccountService {

	@Autowired
	IAccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public AccountServiceImpl() {
	}

	@Override
	public Account addAccount(AccountDto accountDto) {
		Account account = new Account();
		account.setUsername(accountDto.getUsername());
		account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
		account.setConfPassword(accountDto.getConfPassword());
		account.setEmail(accountDto.getEmail());
		return accountRepository.save(account);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		Account account = accountRepository.findByUsername(username);
		if(account == null) throw new UsernameNotFoundException("Account not found with username : " + username);
		return new User(username, account.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, new ArrayList<>());
	}

}
