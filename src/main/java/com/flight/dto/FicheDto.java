package com.sgma.ms.ro.dtos;

import com.sgma.ms.ro.entities.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FicheDto {
    private static final long serialVersionUID = 1L;
    private UUID id;
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
    List<EvenementDto> evenements;
    List<Comment> comments;

}
