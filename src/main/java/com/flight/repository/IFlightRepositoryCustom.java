/**
 * 
 */
package com.flight.repository;

import java.util.List;

import com.flight.models.Flight;
import com.flight.models.FlightCriteria;

/**
 * @author Vincent
 *
 */
public interface IFlightRepositoryCustom {
  public List<Flight> searchFlight(FlightCriteria flightCriteria);
}
