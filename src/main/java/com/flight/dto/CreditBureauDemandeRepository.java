package ma.sg.df.creditbureau.repository;

import ma.sg.df.creditbureau.domain.CbUser;
import ma.sg.df.creditbureau.domain.CreditBureauDemande;
import ma.sg.df.creditbureau.dto.CbDemandeCriteriaDTO;
import ma.sg.df.creditbureau.dto.GetDemandesPricingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CreditBureauDemandeRepository extends JpaRepository<CreditBureauDemande, Long> {

    @Query("select cb from CreditBureauDemande cb join fetch cb.reports where cb.canalSource = :canalSource")
    List<CreditBureauDemande> findByCanalSource(String canalSource);

    @Query("select cb from CreditBureauDemande cb join fetch cb.reports where cb.user = :user and cb.canalSource = :canalSource")
    List<CreditBureauDemande> findByUserAndCanalSource(CbUser user, String canalSource);

    @Transactional
    @Query("select cb from CreditBureauDemande cb join fetch cb.reports where cb.uuid = :uuid and cb.canalSource = :canalSource")
    Optional<CreditBureauDemande> findFirstByUuidAndCanalSource(String uuid, String canalSource);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Query("select cb from CreditBureauDemande cb \n" +
            "WHERE 1 = 1 \n" +
            "AND ( :#{#criteria.typeClient} is null or cb.typeClient = :#{#criteria.typeClient} ) \n" +
            "AND ( :#{#criteria.canalSource} is null or cb.canalSource = :#{#criteria.canalSource} ) \n" +
            "AND ( :#{#criteria.numInterrogation} is null or cb.numInterogation LIKE :#{#criteria.numInterrogation} ) \n" +
            "AND ( :#{#criteria.client} is null or LOWER(cb.nom) LIKE :#{#criteria.client} or LOWER(cb.raisonSociale) LIKE :#{#criteria.client} ) \n" +
            "AND ( :#{#criteria.sDateDemandeFrom} is null or cb.dateDemande >= :#{#criteria.dateDemandeFrom} ) \n" +
            "AND ( :#{#criteria.sDateDemandeTo} is null or cb.dateDemande <= :#{#criteria.dateDemandeTo} ) \n" +
            "AND ( :#{#criteria.userOrigin} is null or cb.user.userOrigin = :#{#criteria.userOrigin} ) \n" +
            "AND ( :#{#criteria.username} is null or cb.user.email = :#{#criteria.username} ) \n" +
            "AND ( :#{#criteria.gestionnaire} is null or LOWER(cb.user.nom) LIKE :#{#criteria.gestionnaire} ) \n" +
            "ORDER BY cb.dateDemande desc")
    Page<CreditBureauDemande> findByCriteria(@Param("criteria") CbDemandeCriteriaDTO demandeCriteria, Pageable pageable);

    @Transactional
    @Query("SELECT COALESCE(SUM(cb.reportPrice), 0) \n" +
            "FROM CreditBureauDemande cb  \n" +
            "WHERE 1 = 1 \n"+
            "AND  (cb.dateDemande > :#{#criteria.dateDemandeFrom}) \n"+
            "AND (cb.dateDemande < :#{#criteria.dateDemandeTo}) \n" +
            "AND cb.user.userOrigin LIKE :#{#criteria.userOrigin}")
    Double generateDemandesPricing(@Param("criteria") GetDemandesPricingRequest analyticsQueryRequest);
}
