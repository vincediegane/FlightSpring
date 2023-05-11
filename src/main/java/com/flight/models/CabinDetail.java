/**
 * 
 */
package com.flight.models;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.flight.enumerations.CabinClass;

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
public class CabinDetail {
  @Enumerated(EnumType.STRING)
  private CabinClass cabinClass;
  private String bagages;
  private String cancellation;
  private String rebooking;
  private String refund;
  private Double fare;
}
