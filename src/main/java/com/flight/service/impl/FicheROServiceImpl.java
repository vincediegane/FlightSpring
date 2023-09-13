package com.sgma.ms.ro.services.impl;

import com.sgma.ms.ro.dao.FicheRepository;
import com.sgma.ms.ro.entities.Evenement;
import com.sgma.ms.ro.entities.Fiche;
import com.sgma.ms.ro.services.EventROService;
import com.sgma.ms.ro.services.FicheROService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FicheROServiceImpl implements FicheROService {
    @Autowired
    FicheRepository ficheRepository;

    @Autowired
    EventROService eventROService;

    @Override
    public Fiche create(Fiche fiche) {
        Fiche fiche1 = ficheRepository.save(fiche);
//        Evenement evenement = fiche1.getEvenements().get(0);
//        evenement.setFiche(fiche1);
//        evenement.setTasks(new ArrayList<>());
//        fiche1.setCurrentEvenementId(evenement.getId().toString());
//        eventROService.start(evenement);
        return fiche1;
    }

    @Override
    public List<Fiche> getByTaskDefinitionKey(String taskDefinitionKey) {
        List<Evenement> evenements = eventROService.getByTaskDefinitionKey(taskDefinitionKey);
        return mapEvenementToFiche(evenements);
    }

    @Override
    public List<Fiche> getByStatus(String status) {
        List<Evenement> evenements= eventROService.getByStatus(status);
        return mapEvenementToFiche(evenements);
    }
    @Override
    public Page<Fiche> getAll(Pageable pageable) {
        return ficheRepository.findAll(pageable);
    }

    public List<Fiche> findAll() {
        return ficheRepository.findAll();
    }

    private List<Fiche> mapEvenementToFiche(List<Evenement> evenements){
        List<Fiche> fiches = ficheRepository.findAll();
        List<Fiche> result = new ArrayList<>();
        evenements.forEach(evenement -> {
            for (Fiche fiche : fiches) {
                if (fiche.getEvenements().contains(evenement)) {
                    result.add(fiche);
                    break;
                }
            }
            ;
        });
        return result;
    }

    @Override
    public Fiche get(UUID id) {
        return ficheRepository.findById(id).get();
    }

    @Override
    public void submit(UUID id, Fiche fiche) {
        Evenement evenement = fiche.getEvenements().get(fiche.getEvenements().size() - 1);
        evenement.setDecision(fiche.getDecision());
        eventROService.submit(evenement);
    }

    @Override
    public List<Fiche> getCompletedEvenements() {
        return mapEvenementToFiche(eventROService.getCompletedEvenements());
    }

    @Override
    public Fiche update(UUID id, Fiche fiche) {
        return ficheRepository.save(fiche);
    }

    @Override
    public void assignToUser(Fiche fiche, String username) {
        Evenement evenement=eventROService.getById(UUID.fromString(fiche.getCurrentEvenementId()));
        evenement.setAssignee(username);
        eventROService.lockTask(evenement);
    }

    @Override
    public List<Fiche> getByAssigneeAndStatus(String assignee, String status){
        return mapEvenementToFiche(eventROService.getByAssigneeAndStatus(assignee, status));
    }


}
