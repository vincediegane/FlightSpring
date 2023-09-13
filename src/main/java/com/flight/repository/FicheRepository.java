package com.sgma.ms.ro.dao;

import com.sgma.ms.ro.entities.Fiche;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FicheRepository extends JpaRepository<Fiche, UUID> {

    Page<Fiche> findAll(Pageable pageable);
    List<Fiche> findAll();
}
