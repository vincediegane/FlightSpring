/**
 * 
 */
package com.flight.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Vincent
 *
 */
@Entity
@Data
@AllArgsConstructor
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
}
