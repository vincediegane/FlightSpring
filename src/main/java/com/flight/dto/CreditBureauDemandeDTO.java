package ma.sg.df.creditbureau.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.sg.df.creditbureau.domain.TypeClient;
import ma.sg.df.creditbureau.resource.validator.AtLeastOneNotNull;
import ma.sg.df.creditbureau.resource.validator.AtMostOneNotNull;
import ma.sg.df.creditbureau.resource.validator.DateBeforeDateDemande;
import ma.sg.df.creditbureau.resource.validator.TypeClientIdentificationConsistent;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AtLeastOneNotNull(fields = { "ppIdent", "pmIdent" }, message = "error.creditbureau.identInfoPresent")
//@AtMostOneNotNull(fields = { "ppIdent", "pmIdent" }, message = "error.creditbureau.uniqueIdentInfo")
@TypeClientIdentificationConsistent
public class CreditBureauDemandeDTO {

	private String uuid;

	private String numInterogation;

	@NotNull(message = "error.notNullEmpty")
	private TypeClient typeClient;

	@Valid
	private PPIdentDto ppIdent;
	@Valid
	private PMIdentDto pmIdent;

	@NotNull(message = "error.notNullEmpty")
	@DateBeforeDateDemande
	private LocalDate dateFinancement;

	private LocalDateTime dateDemande;

	@NotNull(message = "error.notNullEmpty")
	private Double montant;

	private List<ReportDTO> reports;

	@NotNull(message = "error.notNullEmpty")
	@Valid
	private ReferentielDTO raisonRequete;

	@NotNull(message = "error.notNullEmpty")
	@Valid
	private ReferentielDTO typeCredit;

	@NotNull(message = "error.notNullEmpty")
	@Valid
	private ReferentielDTO objetCredit;

	@NotNull(message = "error.notNullEmpty")
	@Valid
	private ReferentielDTO typeContrat;

	@NotNull(message = "error.notNullEmpty")
	@Valid
	private ReferentielDTO typeRapport;

	private double reportPrice;

	private UserDto user;

}
