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

/**
 * @author VINCENT
 *
 */
@Entity
public class Flight {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idFlight;

//  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "company_id")
  private Company company;

  /**
 * 
 */
public Flight() {
	super();
	// TODO Auto-generated constructor stub
}

/**
 * @param idFlight
 * @param company
 * @param flightType
 * @param travelType
 * @param departureDate
 * @param arrivalDate
 * @param backDate
 * @param departureTime
 * @param arrivalTime
 * @param backTime
 * @param departureLocation
 * @param arrivalLocation
 * @param flightDuration
 * @param connectionDuration
 * @param airCraftType
 */
public Flight(Long idFlight, Company company, FlightType flightType, TravelType travelType, LocalDate departureDate,
		LocalDate arrivalDate, LocalDate backDate, LocalTime departureTime, LocalTime arrivalTime, LocalTime backTime,
		String departureLocation, String arrivalLocation, LocalTime flightDuration, LocalTime connectionDuration,
		String airCraftType) {
	super();
	this.idFlight = idFlight;
	this.company = company;
	this.flightType = flightType;
	this.travelType = travelType;
	this.departureDate = departureDate;
	this.arrivalDate = arrivalDate;
	this.backDate = backDate;
	this.departureTime = departureTime;
	this.arrivalTime = arrivalTime;
	this.backTime = backTime;
	this.departureLocation = departureLocation;
	this.arrivalLocation = arrivalLocation;
	this.flightDuration = flightDuration;
	this.connectionDuration = connectionDuration;
	this.airCraftType = airCraftType;
}

/**
 * @return the idFlight
 */
public Long getIdFlight() {
	return idFlight;
}

/**
 * @param idFlight the idFlight to set
 */
public void setIdFlight(Long idFlight) {
	this.idFlight = idFlight;
}

/**
 * @return the company
 */
public Company getCompany() {
	return company;
}

/**
 * @param company the company to set
 */
public void setCompany(Company company) {
	this.company = company;
}

/**
 * @return the flightType
 */
public FlightType getFlightType() {
	return flightType;
}

/**
 * @param flightType the flightType to set
 */
public void setFlightType(FlightType flightType) {
	this.flightType = flightType;
}

/**
 * @return the travelType
 */
public TravelType getTravelType() {
	return travelType;
}

/**
 * @param travelType the travelType to set
 */
public void setTravelType(TravelType travelType) {
	this.travelType = travelType;
}

/**
 * @return the departureDate
 */
public LocalDate getDepartureDate() {
	return departureDate;
}

/**
 * @param departureDate the departureDate to set
 */
public void setDepartureDate(LocalDate departureDate) {
	this.departureDate = departureDate;
}

/**
 * @return the arrivalDate
 */
public LocalDate getArrivalDate() {
	return arrivalDate;
}

/**
 * @param arrivalDate the arrivalDate to set
 */
public void setArrivalDate(LocalDate arrivalDate) {
	this.arrivalDate = arrivalDate;
}

/**
 * @return the backDate
 */
public LocalDate getBackDate() {
	return backDate;
}

/**
 * @param backDate the backDate to set
 */
public void setBackDate(LocalDate backDate) {
	this.backDate = backDate;
}

/**
 * @return the departureTime
 */
public LocalTime getDepartureTime() {
	return departureTime;
}

/**
 * @param departureTime the departureTime to set
 */
public void setDepartureTime(LocalTime departureTime) {
	this.departureTime = departureTime;
}

/**
 * @return the arrivalTime
 */
public LocalTime getArrivalTime() {
	return arrivalTime;
}

/**
 * @param arrivalTime the arrivalTime to set
 */
public void setArrivalTime(LocalTime arrivalTime) {
	this.arrivalTime = arrivalTime;
}

/**
 * @return the backTime
 */
public LocalTime getBackTime() {
	return backTime;
}

/**
 * @param backTime the backTime to set
 */
public void setBackTime(LocalTime backTime) {
	this.backTime = backTime;
}

/**
 * @return the departureLocation
 */
public String getDepartureLocation() {
	return departureLocation;
}

/**
 * @param departureLocation the departureLocation to set
 */
public void setDepartureLocation(String departureLocation) {
	this.departureLocation = departureLocation;
}

/**
 * @return the arrivalLocation
 */
public String getArrivalLocation() {
	return arrivalLocation;
}

/**
 * @param arrivalLocation the arrivalLocation to set
 */
public void setArrivalLocation(String arrivalLocation) {
	this.arrivalLocation = arrivalLocation;
}

/**
 * @return the flightDuration
 */
public LocalTime getFlightDuration() {
	return flightDuration;
}

/**
 * @param flightDuration the flightDuration to set
 */
public void setFlightDuration(LocalTime flightDuration) {
	this.flightDuration = flightDuration;
}

/**
 * @return the connectionDuration
 */
public LocalTime getConnectionDuration() {
	return connectionDuration;
}

/**
 * @param connectionDuration the connectionDuration to set
 */
public void setConnectionDuration(LocalTime connectionDuration) {
	this.connectionDuration = connectionDuration;
}

/**
 * @return the airCraftType
 */
public String getAirCraftType() {
	return airCraftType;
}

/**
 * @param airCraftType the airCraftType to set
 */
public void setAirCraftType(String airCraftType) {
	this.airCraftType = airCraftType;
}

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
