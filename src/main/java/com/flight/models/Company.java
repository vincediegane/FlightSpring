/**
 * 
 */
package com.flight.models;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flight.enumerations.CompanyName;

/**
 * @author VINCENT
 *
 */
@Entity
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long idCompany;

  @Column(name = "company_name")
  @Enumerated(EnumType.STRING)
  private CompanyName CompanyName;

//  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
  private Set<Flight> flights;

  @ElementCollection
  @CollectionTable(name = "cabin_details", joinColumns = @JoinColumn(name = "company_id"))
  private Set<CabinDetail> cabinDetails;

  @ElementCollection
  @CollectionTable(name = "inflights_infos", joinColumns = @JoinColumn(name = "company_id"))
  private Set<InflightInfo> inflightInfos;

/**
 * 
 */
public Company() {
	super();
	// TODO Auto-generated constructor stub
}

/**
 * @param idCompany
 * @param companyName
 * @param flights
 * @param cabinDetails
 * @param inflightInfos
 */
public Company(Long idCompany, com.flight.enumerations.CompanyName companyName, Set<Flight> flights,
		Set<CabinDetail> cabinDetails, Set<InflightInfo> inflightInfos) {
	super();
	this.idCompany = idCompany;
	CompanyName = companyName;
	this.flights = flights;
	this.cabinDetails = cabinDetails;
	this.inflightInfos = inflightInfos;
}

/**
 * @return the idCompany
 */
public Long getIdCompany() {
	return idCompany;
}

/**
 * @param idCompany the idCompany to set
 */
public void setIdCompany(Long idCompany) {
	this.idCompany = idCompany;
}

/**
 * @return the companyName
 */
public CompanyName getCompanyName() {
	return CompanyName;
}

/**
 * @param companyName the companyName to set
 */
public void setCompanyName(CompanyName companyName) {
	CompanyName = companyName;
}

/**
 * @return the flights
 */
public Set<Flight> getFlights() {
	return flights;
}

/**
 * @param flights the flights to set
 */
public void setFlights(Set<Flight> flights) {
	this.flights = flights;
}

/**
 * @return the cabinDetails
 */
public Set<CabinDetail> getCabinDetails() {
	return cabinDetails;
}

/**
 * @param cabinDetails the cabinDetails to set
 */
public void setCabinDetails(Set<CabinDetail> cabinDetails) {
	this.cabinDetails = cabinDetails;
}

/**
 * @return the inflightInfos
 */
public Set<InflightInfo> getInflightInfos() {
	return inflightInfos;
}

/**
 * @param inflightInfos the inflightInfos to set
 */
public void setInflightInfos(Set<InflightInfo> inflightInfos) {
	this.inflightInfos = inflightInfos;
}
}
