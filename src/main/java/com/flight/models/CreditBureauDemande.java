package ma.sg.df.creditbureau.domain;

import lombok.*;
import ma.sg.df.creditbureau.util.UuidUtils;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("JpaDataSourceORMInspection")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "demande")
public class CreditBureauDemande {

	@Id
	@SequenceGenerator(name = "SEQ_DEMANDE", sequenceName = "SEQ_DEMANDE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DEMANDE")
	private Long id;

	@Column
	private String uuid;

	@Column
	private String numInterogation;

	@Enumerated(EnumType.STRING)
	private TypeClient typeClient;

	@Column
	private String canalSource;

	@Column
	private LocalDate dateFinancement;

	@Column
	private Double montant;

	@CreationTimestamp
	private LocalDateTime dateDemande;

	@OneToMany(mappedBy = "creditBureauDemande", orphanRemoval = true)
	public List<Report> reports;

	@ManyToOne
	@JoinColumn(name = "raison_requete_id")
	private RaisonRequete raisonRequete;

	@ManyToOne
	@JoinColumn(name = "raison_id")
	private TypeCredit typeCredit;

	@ManyToOne
	@JoinColumn(name = "objet_credit_id")
	private ObjetCredit objetCredit;

	@ManyToOne
	@JoinColumn(name = "type_contrat_id")
	private TypeContrat typeContrat;

	@ManyToOne
	@JoinColumn(name = "type_rapport_id")
	private TypeRapport typeRapport;

	@Column
	private double reportPrice;

	// PP
	@ManyToOne
	@JoinColumn(name = "nature_piece_identite_id")
	private NaturePieceIdentite naturePieceIdentite;

	@Column
	private String numeroPieceIdentite;

	@ManyToOne
	@JoinColumn(name = "sexe_id")
	private Sexe sexe;

	@Column
	private String nom;

	@Column
	private String prenom;

	@Column
	private LocalDate dateNaissance;

	@ManyToOne
	@JoinColumn(name = "ville_residence_id")
	private Ville villeResidence;
	////

	// PM
	@Column
	private String codeClient;
	@Column
	private String raisonSociale;
	@Column
	private BigInteger rc;

	// Date Creation entreprise
	@Column
	private LocalDate dateCreation;

	@ManyToOne
	@JoinColumn(name = "ville_siege_id")
	private Ville villeSiege;

	@ManyToOne
	@JoinColumn(name = "ville_tribunal_id")
	private Tribunal villeTribunal;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private CbUser user;

	// PP Pro
	@Column
	private String idFiscal;

	@Column
	private Integer patente;

	@PrePersist
	public void prePersist() {
		String hashedUuid = UuidUtils.getHashedUuid(this.dateDemande);
		this.setUuid(hashedUuid);
	}
	//

}
