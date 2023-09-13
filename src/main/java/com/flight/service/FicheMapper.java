package com.sgma.ms.ro.mappers;

import com.sgma.ms.ro.dtos.FicheDto;
import com.sgma.ms.ro.entities.Fiche;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FicheMapper {
    FicheDto toDTO(Fiche fiche);

    Fiche toEntity(FicheDto ficheDto);

    List<FicheDto> toDtoList(List<Fiche> ficheList);

    List<Fiche> toEntityList(List<FicheDto> ficheDto);
}
