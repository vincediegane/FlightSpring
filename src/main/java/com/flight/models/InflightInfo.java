/**
 * 
 */
package com.flight.models;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VINCENT
 *
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InflightInfo {
  private String title;
  private String description;
}
