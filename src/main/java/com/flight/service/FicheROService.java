package com.sgma.ms.ro.services;

import com.sgma.ms.ro.dtos.FicheDto;
import com.sgma.ms.ro.entities.Fiche;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface FicheROService {
    public Fiche create(Fiche fiche);

    public List<Fiche> getByTaskDefinitionKey(String taskDefinitionKey);
    List<Fiche> getByStatus(String status);
    Page<Fiche> getAll(Pageable pageable);
    public List<Fiche> findAll();

    Fiche get(UUID id);

    void submit(UUID id, Fiche fiche);

    List<Fiche> getCompletedEvenements();

    Fiche update(UUID id, Fiche fiche);

    void assignToUser(Fiche fiche, String username);
    List<Fiche> getByAssigneeAndStatus(String assignee, String status);
}
