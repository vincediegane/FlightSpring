/**
 * 
 */
package com.flight.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Vincent
 *
 */
@Entity
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long idAccount;
  @Column
  private String username;
  @Column
  private String password;
  @Column
  private String confPassword;
  @Column
  private String email;
  @Column
  private boolean enabled;

  /**
   * 
   */
  public Account() {
	super();
	this.enabled = false;
  }

/**
 * @param idAccount
 * @param username
 * @param password
 * @param confPassword
 * @param email
 * @param enabled
 */
public Account(long idAccount, String username, String password, String confPassword, String email, boolean enabled) {
	super();
	this.idAccount = idAccount;
	this.username = username;
	this.password = password;
	this.confPassword = confPassword;
	this.email = email;
	this.enabled = enabled;
}

/**
 * @return the idAccount
 */
public long getIdAccount() {
	return idAccount;
}

/**
 * @param idAccount the idAccount to set
 */
public void setIdAccount(long idAccount) {
	this.idAccount = idAccount;
}

/**
 * @return the username
 */
public String getUsername() {
	return username;
}

/**
 * @param username the username to set
 */
public void setUsername(String username) {
	this.username = username;
}

/**
 * @return the password
 */
public String getPassword() {
	return password;
}

/**
 * @param password the password to set
 */
public void setPassword(String password) {
	this.password = password;
}

/**
 * @return the confPassword
 */
public String getConfPassword() {
	return confPassword;
}

/**
 * @param confPassword the confPassword to set
 */
public void setConfPassword(String confPassword) {
	this.confPassword = confPassword;
}

/**
 * @return the email
 */
public String getEmail() {
	return email;
}

/**
 * @param email the email to set
 */
public void setEmail(String email) {
	this.email = email;
}

/**
 * @return the enabled
 */
public boolean isEnabled() {
	return enabled;
}

/**
 * @param enabled the enabled to set
 */
public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}
}
