/**
 * 
 */
package com.flight.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.flight.models.Account;

/**
 * @author Vincent
 *
 */
@Repository
public interface IAccountRepository extends CrudRepository<Account, Integer> {
	@Query("select account from Account account where account.username = ?1")
	Account findByUsername(String username);
}
