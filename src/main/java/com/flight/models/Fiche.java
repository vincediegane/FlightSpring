package com.sgma.ms.ro.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fiche")
@Generated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Fiche {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ColumnTransformer(
            read =  "pgp_sym_decrypt(" +
                    "    title, " +
                    "    current_setting('encrypt.key')" +
                    ")",
            write = "pgp_sym_encrypt( " +
                    "    ?, " +
                    "    current_setting('encrypt.key')" +
                    ") "
    )
    @Column(columnDefinition = "bytea")
    private String title;
    private String directionRegionale;
    private String service;
    private String buSu;
    private String typeFraude;
    private String codeActivite1;
    private String codeActivite2;
    private String codeActivite3;
    private String codeCategorie;
    private String codeSousCategorie;
    private String marche;
    private String initiateur;
    private String responsableErreur;
    private String telephone;
    private Date dateDebut;
    private Date dateDeclaration;
    private Date dateDecouverte;
    private Date dateComptabilisation;
    private String description;
    private String causes;
    private String consequences;
    private String mesuresCorrectives;
    private String mesuresPreventives;
    private String status;
    private String origine;
    private String nature;
    private String refCaroline;
    private String numComptabilisation;
    private String decision;
    private String currentEvenementId;
    @OneToMany(mappedBy = "fiche", cascade = CascadeType.PERSIST, orphanRemoval = true)
    List<Evenement> evenements;
    @OneToMany(mappedBy = "fiche", cascade = CascadeType.PERSIST, orphanRemoval = true)
    List<Comment> comments;
}
