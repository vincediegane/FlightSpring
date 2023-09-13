package com.sgma.ms.ro.controllers;

import com.sgma.ms.ro.dtos.FicheDto;
import com.sgma.ms.ro.entities.Fiche;
import com.sgma.ms.ro.mappers.EvenementMapper;
import com.sgma.ms.ro.mappers.FicheMapper;
import com.sgma.ms.ro.services.EventROService;
import com.sgma.ms.ro.services.FicheROService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class FicheROController {

    @Autowired
    FicheMapper ficheMapper;

    @Autowired
    EvenementMapper evenementMapper;

    @Autowired
    FicheROService ficheROService;

    @Autowired
    EventROService eventROService;

    @PostMapping("/start")
    public FicheDto ficheDto(@RequestBody FicheDto ficheDto) {
        Fiche fiche = ficheROService.create(ficheMapper.toEntity(ficheDto));
        return ficheMapper.toDTO(fiche);

    }

    @GetMapping("/activityKey/{activityKey}")
    public List<FicheDto> getByActivityKey(@PathVariable String activityKey) {
        return ficheMapper.toDtoList(ficheROService.getByTaskDefinitionKey(activityKey));
    }

    @GetMapping("/status/{status}")
    public List<FicheDto> getByStatus(@PathVariable String status){
        return ficheMapper.toDtoList(ficheROService.getByStatus(status));
    }


//    @GetMapping("/fiches")
//    public Map<String, Object> getAll(@RequestParam(defaultValue = "0") int page,
//                      @RequestParam(defaultValue = "10") int size) {
//        Pageable pageable= PageRequest.of(page, size);
//        Page<Fiche> fichePage=ficheROService.getAll(pageable);
//        List<FicheDto> requests=ficheMapper.toDtoList(fichePage.getContent());
//        Map<String, Object> response = new HashMap<>();
//        response.put("fiches", requests);
//        response.put("currentPage", fichePage.getNumber());
//        response.put("totalItems", fichePage.getTotalElements());
//        response.put("totalPages", fichePage.getTotalPages());
//
//        return  response;
//    }
    @GetMapping("/fiches")
    public List<Fiche> getAll() {
        List<Fiche> fichePage=ficheROService.findAll();

        return  fichePage;
    }
    @GetMapping("/fiches/{id}")
    public FicheDto getById(@PathVariable UUID id) {
        return ficheMapper.toDTO(ficheROService.get(id));

    }

    @PostMapping("/fiches/task/{id}/complete")
    public void approuver(@PathVariable UUID id, @RequestBody FicheDto fiche) {

        ficheROService.submit(id, ficheMapper.toEntity(fiche));
    }
    @PutMapping("/fiches/{id}")
    public FicheDto update(@PathVariable UUID id, @RequestBody FicheDto ficheDto) {
        return ficheMapper.toDTO(ficheROService.update(id, ficheMapper.toEntity(ficheDto)));
    }

    @GetMapping("/fiches/state/completed")
    public List<FicheDto> getCompletedEvenements(){
        return ficheMapper.toDtoList(ficheROService.getCompletedEvenements());
    }

    @PostMapping("/fiches/assignee/{username}")
    public void setAssignee(@RequestBody FicheDto ficheDto, @PathVariable String username){
        ficheROService.assignToUser(ficheMapper.toEntity(ficheDto), username);
    }

    @GetMapping("/fiches/affected")
    public List<FicheDto> getByAssigneeAndStatus(@RequestParam("assignee") String assignee, @RequestParam("status") String status){
        return ficheMapper.toDtoList(ficheROService.getByAssigneeAndStatus(assignee, status));
    }
}
