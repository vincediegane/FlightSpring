/**
 * 
 */
package com.flight.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flight.models.Flight;

/**
 * @author VINCENT
 *
 */
@Repository
//public interface IFlightRepository extends JpaRepository<Flight, Long>,IFlightRepositoryCustom {
public interface IFlightRepository extends JpaRepository<Flight, Long> {

}
