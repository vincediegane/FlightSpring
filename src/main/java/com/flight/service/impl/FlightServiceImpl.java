/**
 * 
 */
package com.flight.service.impl;

import java.util.List;
//import java.util.Optional;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.flight.models.Flight;
//import com.flight.models.FlightCriteria;
import com.flight.repository.IFlightRepository;
import com.flight.service.IFlightService;

/**
 * @author VINCENT
 *
 */
@Service
@Transactional
public class FlightServiceImpl implements IFlightService{

  @Autowired
  private IFlightRepository flightRepository;

  public Flight addFlight(Flight flight) {
    return flightRepository.save(flight);
  }

  public List<Flight> getAllFlights() {
	return flightRepository.findAll();
  }

  public Flight getFlight(Long idFlight) {
	return flightRepository.findById(idFlight).orElse(null);
  }

//  public List<Flight> searchFlight(FlightCriteria flightCriteria) {
//	return flightRepository.searchFlight(flightCriteria);
//  }


}
