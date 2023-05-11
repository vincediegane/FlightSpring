/**
 * 
 */
package com.flight.models;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flight.enumerations.FlightType;
import com.flight.enumerations.TravelType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VINCENT
 *
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idFlight;

//  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "company_id")
  private Company company;

  @Column(name = "flight_type")
  @Enumerated(EnumType.STRING)
  private FlightType flightType;

  @Column(name = "travel_type")
  @Enumerated(EnumType.STRING)
  private TravelType travelType;

  @Column(name = "departure_date")
  private LocalDate departureDate;

  @Column(name = "arrival_date")
  private LocalDate arrivalDate;

  @Column(name = "back_date")
  private LocalDate backDate;

  @Column(name = "departure_time")
  private LocalTime departureTime;

  @Column(name = "arrival_time")
  private LocalTime arrivalTime;

  @Column(name = "back_time")
  private LocalTime backTime;

  @Column(name = "departure_location")
  private String departureLocation;

  @Column(name = "arrival_location")
  private String arrivalLocation;

  @Column(name = "flight_duration")
  private LocalTime flightDuration;

  @Column(name = "connection_duration")
  private LocalTime connectionDuration;

  @Column(name = "aircraft_type")
  private String airCraftType;
}
