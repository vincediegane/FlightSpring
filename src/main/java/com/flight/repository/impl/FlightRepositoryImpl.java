/**
 * 
 */
package com.flight.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import com.flight.models.Flight;
import com.flight.models.FlightCriteria;
import com.flight.repository.IFlightRepositoryCustom;

/**
 * @author Vincent
 *
 */
public class FlightRepositoryImpl implements IFlightRepositoryCustom {

	@Autowired
	EntityManager entityManager;
	public List<Flight> searchFlight(FlightCriteria flightCriteria) {
	  CriteriaBuilder cbuild = entityManager.getCriteriaBuilder();
	  CriteriaQuery<Flight> cquery = cbuild.createQuery(Flight.class);
	  Root<Flight> flight = cquery.from(Flight.class);
	  List<Predicate> predicates = new ArrayList<>();
		  
	  if (flightCriteria.getCompany() != null) {
		predicates.add(cbuild.equal(flight.get("company").get("companyName"), flightCriteria.getCompany()));
	  }
	  if (flightCriteria.getFlightType() != null) {
		predicates.add(cbuild.equal(flight.get("flightType"), flightCriteria.getFlightType()));
	  }

	  if (flightCriteria.getTravelType() != null) {
		predicates.add(cbuild.equal(flight.get("travelType"), flightCriteria.getTravelType()));
	  }

	  cquery.where(predicates.toArray(new Predicate[0]));
	  return entityManager.createQuery(cquery).getResultList();
	}

}
