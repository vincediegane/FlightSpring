/**
 * 
 */
package com.flight.service;

import java.util.List;

import com.flight.models.Flight;
import com.flight.models.FlightCriteria;

/**
 * @author VINCENT
 *
 */
public interface IFlightService {
  Flight addFlight(Flight flight);
  List<Flight> getAllFlights();
//  List<Flight> searchFlight(FlightCriteria flightCriteria);
  Flight getFlight(Long idFlight);
}
