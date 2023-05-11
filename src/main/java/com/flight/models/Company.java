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
}
