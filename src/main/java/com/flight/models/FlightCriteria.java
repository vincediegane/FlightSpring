/**
 * 
 */
package com.flight.models;

import java.time.LocalDate;
import java.time.LocalTime;
import com.flight.enumerations.CompanyName;
import com.flight.enumerations.FlightType;
import com.flight.enumerations.TravelType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Vincent
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightCriteria {
  private CompanyName company;
  private FlightType flightType;
  private TravelType travelType;
  private String departureLocation;
  private String arrivalLocation;
  private Double fareMin;
  private Double fareMax;
  private LocalTime flightDurationMin;
  private LocalTime flightDurationMax;
  private String aircraftType;
  private LocalDate departureDateMin;
  private LocalDate arrivalDateMin;
  private LocalDate backDateMin;
  private LocalTime departureTimeMin;
  private LocalTime arrivalTimeMin;
  private LocalTime BactTimeMin;
  private LocalDate departureDateMax;
  private LocalDate arrivalDateMax;
  private LocalDate backDateMax;
  private LocalTime departureTimeMax;
  private LocalTime arrivalTimeMax;
  private LocalTime BactTimeMax;
  private LocalTime connectionDurationMin;
  private LocalTime connectionDurationMax;
}
