package ma.sg.df.creditbureau.service;

import com.creditinfo.response.REPONSE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.sg.df.creditbureau.domain.*;
import ma.sg.df.creditbureau.dto.CbDemandeCriteriaDTO;
import ma.sg.df.creditbureau.dto.CreditBureauDemandeDTO;
import ma.sg.df.creditbureau.dto.CreditBureauReportViewAdapter;
import ma.sg.df.creditbureau.dto.ReportDTO;
import ma.sg.df.creditbureau.exception.EntityNotFoundException;
import ma.sg.df.creditbureau.mapper.CreditBureauMapper;
import ma.sg.df.creditbureau.repository.CreditBureauDemandeRepository;
import ma.sg.df.creditbureau.repository.DocumentRepository;
import ma.sg.df.creditbureau.repository.UserRepository;
import ma.sg.df.loan.dto.creditinfo.request.RequeteDTO;
import ma.sg.df.loan.dto.creditinfo.response.EnteteDTO;
import ma.sg.df.loan.dto.creditinfo.response.ReponseDTO;
import ma.sg.df.loan.exception.UnauthorizedUserException;
import ma.sg.df.loan.mapper.CreditInfoMapper;
import ma.sg.df.loan.resource.helper.ResourceHelper;
import ma.sg.df.loan.service.CreditInfoService;
import ma.sg.df.loan.util.XMLMarshallerUtil;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditBureauService {

    private final ResourceHelper resourceHelper;
    private final CreditInfoService creditInfoService;

    private final CreditInfoMapper creditInfoMapper;
    private final CreditBureauMapper creditBureauMapper;
    private final CreditBureauDemandeRepository creditBureauDemandeRepository;
    private final JasperReportService jasperReportService;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final GedService gedService;

    @Transactional("cbTransactionManager")
    public ReportDTO traiterDemandeCreditBureau(CreditBureauDemande creditBureauDemande, TypeClient typeClient) {
        List<String> creditInfoData = new ArrayList<>(2);
        RequeteDTO creditInfoRequest = buildRequeteDto(creditBureauDemande, typeClient);
        String typeRapport = Optional.ofNullable(creditBureauDemande)
                .map(CreditBureauDemande::getTypeRapport)
                .map(TypeRapport::getCode)
                .orElse(null);
        ReponseDTO creditInfoResponse = getCreditInfoReportData(creditInfoRequest, creditInfoData, typeRapport);
        creditBureauDemande.setNumInterogation(Optional.ofNullable(creditInfoResponse.getEntete()).map(EnteteDTO::getEnqTransRefNumber).orElse(null));

        CreditBureauReportViewAdapter creditBureauReportViewAdapter =
                creditBureauMapper.buildCreditBureauReportViewAdapter(creditBureauDemande, creditInfoRequest,
                        creditInfoResponse, typeClient);

        Report report = saveReport(creditBureauReportViewAdapter, creditBureauDemande, creditInfoData);

        ReportDTO reportDTO = creditBureauMapper.toDocumentDto(report);
        if (creditBureauDemande.getReports() == null)
            creditBureauDemande.setReports(new ArrayList<>());
        creditBureauDemande.getReports().add(report);


        try {
            generateAndSaveReportToGed(creditBureauReportViewAdapter, creditBureauDemande, creditInfoData, report, reportDTO);

        } catch (Exception exception) {
            log.error(String.valueOf(exception));

        }

        return reportDTO;
        //documentDTO.setCreditInfoResponse(creditInfoData.get(1));

    }

    /**
     * Save credit bureau demande to the DB and generate related report.
     */
    @Transactional("cbTransactionManager")
    public CreditBureauDemandeDTO generateCreditBureauReport(
            CreditBureauDemandeDTO creditBureauRequestDTO, String username) {

        log.info("Start generateCreditBureauReport with params: {}, {}", creditBureauRequestDTO, username);

        CreditBureauDemande creditBureauDemande = buildDemande(creditBureauRequestDTO, username);
        creditBureauDemandeRepository.save(creditBureauDemande);

        List<ReportDTO> reports = new ArrayList<>();

        if (creditBureauDemande.getTypeClient() == TypeClient.PP
                || creditBureauDemande.getTypeClient() == TypeClient.PM) {

            reports.add(traiterDemandeCreditBureau(creditBureauDemande, creditBureauDemande.getTypeClient()));

        } else if (creditBureauDemande.getTypeClient() == TypeClient.PP_PRO) {

            reports.add(traiterDemandeCreditBureau(creditBureauDemande, TypeClient.PP));
            reports.add(traiterDemandeCreditBureau(creditBureauDemande, TypeClient.PM));
        }

        creditBureauDemandeRepository.save(creditBureauDemande);

        CreditBureauDemandeDTO creditBureauDemandeDTO =
                this.creditBureauMapper.mapCreditBureauDemandeToDto(creditBureauDemande);

        creditBureauDemandeDTO.setReports(reports);

        log.info("End generateCreditBureauReport with result: {}", creditBureauDemandeDTO);

        return creditBureauDemandeDTO;

    }

    private CreditBureauDemande buildDemande(CreditBureauDemandeDTO creditBureauRequestDTO,
                                             String username) {

        log.info("Start buildDemande with params: {} , {}", creditBureauRequestDTO, username);

        CreditBureauDemande creditBureauDemande = this.creditBureauMapper
                .mapCreditBureauRequestDtoToCreditBureauDemande(creditBureauRequestDTO);

        CbUser user = userRepository.findByEmail(username).orElse(null);
        creditBureauDemande.setUser(user);
        creditBureauDemande.setCanalSource(getCanalSource());
        creditBureauDemande.setDateDemande(LocalDateTime.now());

        log.info("End buildDemande");
        return creditBureauDemande;
    }

    private Report saveReport(CreditBureauReportViewAdapter creditBureauReportViewAdapter,
                              CreditBureauDemande creditBureauDemande,
                              List<String> creditInfoData) {

        log.info("Start Save Report to database with params: {}", creditBureauReportViewAdapter);

        String numeroIdentite = null;
        if (creditBureauDemande.getTypeClient() == TypeClient.PP) {
            numeroIdentite = creditBureauDemande.getNumeroPieceIdentite();
        } else {
            if (creditBureauDemande.getRc() != null) {
                numeroIdentite = String.valueOf(creditBureauDemande.getRc());
            } else if (creditBureauDemande.getPatente() != null) {
                numeroIdentite = String.valueOf(creditBureauDemande.getPatente());
            } else {
                numeroIdentite = creditBureauDemande.getIdFiscal();
            }
        }

        String reportName = "Rapport de solvabilité_" + numeroIdentite + "_"
                + creditBureauDemande.getDateDemande().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));

        Report report = Report.builder()
                .nom(reportName)
                .type(creditBureauDemande.getTypeClient().toString())
                .isGeneratedFromCacheData(false)
                .creditBureauDemande(creditBureauDemande)
                .dateChargement(LocalDateTime.now())
                .extension("pdf")
                .creditInfoResponse((Optional.ofNullable(creditInfoData.get(1)).orElse(null)))
                .build();

        log.info("End Save Report to database with params: {}", report);


        return documentRepository.save(report);

    }


    private ReportDTO generateAndSaveReportToGed(
            CreditBureauReportViewAdapter creditBureauReportViewAdapter,
            CreditBureauDemande creditBureauDemande,
            List<String> creditInfoData,
            Report report,
            ReportDTO reportDTO) {

        log.info("Start generateAndSaveReportToGed");

        try {
            byte[] reportBytes = jasperReportService.generatePDFReport(creditBureauReportViewAdapter);
            reportDTO.setData(Base64.getEncoder().encodeToString(reportBytes));
            reportDTO.setTaille(reportBytes.length);

            Long idDocument = gedService.saveReport(reportBytes, report.getNom());
            report.setIdDocubase(idDocument);
            report.setTaille(reportBytes.length);
            report.setDateCreation(LocalDateTime.now());
            documentRepository.save(report);
        } catch (Exception e) {
            log.error(String.valueOf(e));

        }

        log.info("End generateAndSaveReportToGed with result: {} ", reportDTO);
        return reportDTO;
    }

    private RequeteDTO buildRequeteDto(CreditBureauDemande creditBureauDemande, TypeClient typeClient) {
        log.info("Start buildRequeteDto ");

        // Get report data
        RequeteDTO creditInfoRequest = creditBureauMapper.mapCreditBureauRequestToCreditInfoRequest(creditBureauDemande, typeClient);


        Optional<String> userOrigin = Optional.ofNullable(creditBureauDemande.getUser().getUserOrigin());

        if (!userOrigin.filter(val -> val.equals("DAA")).isPresent()) {
            resourceHelper.appendCreditInfoCredentials(creditInfoRequest);
        } else {
            resourceHelper.appendCreditInfoCredentialsForDAA(creditInfoRequest);
        }

         resourceHelper.appendCanalSource(creditInfoRequest);

        log.info("End buildRequeteDto with  result : {} ", creditInfoRequest);
        return creditInfoRequest;
    }

    private ReponseDTO getCreditInfoReportData(RequeteDTO creditInfoRequest, List<String> creditInfoData, String typeRapport) {

        log.info("Start getCreditInfoReportData ");

        // Get report data
        ReponseDTO result = creditInfoService.getReportWithErrors(creditInfoRequest, creditInfoData, typeRapport);

        log.info("End getCreditInfoReportData with  result : {} ", result);
        return result;

    }

    public Page<CreditBureauDemandeDTO> searchDemandes(String username, CbDemandeCriteriaDTO cbDemandeCriteriaDTO) {

        log.info("Start searchDemande with params: user: {}, criteria: {}", username, cbDemandeCriteriaDTO);

        CbUser user = userRepository.findByEmail(username).orElseThrow(() -> new EntityNotFoundException("Utilisateur invalide"));
        String canalSource = getCanalSource();
        cbDemandeCriteriaDTO.setCanalSource(canalSource);
        cbDemandeCriteriaDTO.setUserOrigin(user.getUserOrigin());
        cbDemandeCriteriaDTO.setSDateDemandeFrom(cbDemandeCriteriaDTO.getDateDemandeFrom() != null ? "" : null);
        cbDemandeCriteriaDTO.setSDateDemandeTo(cbDemandeCriteriaDTO.getDateDemandeTo() != null ? "" : null);
        cbDemandeCriteriaDTO.setClient(cbDemandeCriteriaDTO.getClient() != null ? "%" + cbDemandeCriteriaDTO.getClient().toLowerCase() + "%" : null);
        cbDemandeCriteriaDTO.setGestionnaire(cbDemandeCriteriaDTO.getGestionnaire() != null ? "%" + cbDemandeCriteriaDTO.getGestionnaire().toLowerCase() + "%" : null);
        cbDemandeCriteriaDTO.setNumInterrogation(cbDemandeCriteriaDTO.getNumInterrogation() != null ? "%" + cbDemandeCriteriaDTO.getNumInterrogation() + "%" : null);

        if (user.getRoles().stream().map(Role::getCode).anyMatch(role -> role.equals(AuthorizationService.PILOTAGE_ROLE) || role.equals(AuthorizationService.PILOTAGE_DAA_ROLE))) {
            cbDemandeCriteriaDTO.setUserOrigin(user.getUserOrigin());
            return this.creditBureauDemandeRepository.findByCriteria(cbDemandeCriteriaDTO, cbDemandeCriteriaDTO.getPageable())
                    .map(creditBureauMapper::mapCreditBureauDemandeToDtoIgnoreReports);
        }

        cbDemandeCriteriaDTO.setUsername(username);
        return this.creditBureauDemandeRepository.findByCriteria(cbDemandeCriteriaDTO, cbDemandeCriteriaDTO.getPageable())
                .map(creditBureauMapper::mapCreditBureauDemandeToDtoIgnoreReports);

    }

    public ReportDTO getCreditBureauReport(String uuidReport, String username) {

        log.info("Start getCreditBureauReport with params: {}, {}", uuidReport, username);

        Report document = documentRepository.findByUuid(uuidReport)
                .orElseThrow(() -> new EntityNotFoundException("Aucun rapport correspondant trouvé"));

        CbUser user = document.getCreditBureauDemande().getUser();

        CbUser authenticatedUser = userRepository.findByEmail(username).orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur correspondant trouvé"));

        if (username == null || (!username.equals(user.getEmail()) && authenticatedUser.getRoles().stream().map(Role::getCode).noneMatch(role -> role.equals(AuthorizationService.PILOTAGE_ROLE) || role.equals(AuthorizationService.PILOTAGE_DAA_ROLE)))) {
            throw new UnauthorizedUserException("Vous ne pouvez pas accéder à cette ressource");
        }
        if (document.getIdDocubase() == null) {
            return getCreditBureauReportFromXML(document);
        } else {
            log.info("Start getCreditBureauReport based on GED");
            String b64Report = gedService.getDocument(String.valueOf(document.getIdDocubase()));

            ReportDTO report = creditBureauMapper.toDocumentDto(document);
            report.setData(b64Report);

            log.info("End getCreditBureauReport with result: {}", report);
            return report;

        }
    }

    public CreditBureauDemandeDTO getByUuidAndUsername(String uuid, String username) {

        log.info("Start getByUuidAndUsername with params: {}, {}", uuid, username);

        CbUser user = userRepository.findByEmail(username).orElseThrow(() -> new EntityNotFoundException("Utilisateur invalide"));
        String canalSource = getCanalSource();

        CreditBureauDemandeDTO result =
                creditBureauDemandeRepository.findFirstByUuidAndCanalSource(uuid, canalSource)
                        .map(creditBureauMapper::mapCreditBureauDemandeToDto)
                        .orElseThrow(() -> new EntityNotFoundException("Aucune Demande Correspondante trouvée"));

        if (username == null || (!username.equals(result.getUser().getEmail())
                && user.getRoles().stream().map(Role::getCode).noneMatch(role -> role.equals(AuthorizationService.PILOTAGE_ROLE) || role.equals(AuthorizationService.PILOTAGE_DAA_ROLE)))) {
            throw new UnauthorizedUserException("Vous ne pouvez pas accéder à cette ressource");
        }

        log.info("creditBureauDemandeRepository.findFirstByUuidAndCanalSource result: {}", result);
        return result;

    }

    public ReportDTO getCreditBureauReportFromXML(Report document) {
        log.info("Start getCreditBureauReport based on XML");

        CreditBureauDemande creditBureauDemande = document.getCreditBureauDemande();

        REPONSE result = XMLMarshallerUtil.unmarshall(document.getCreditInfoResponse(), REPONSE.class);

        ReponseDTO reponse = creditInfoMapper.mapResponseToResponseDTO(result);
        RequeteDTO creditInfoRequest = buildRequeteDto(creditBureauDemande, creditBureauDemande.getTypeClient());

        TypeClient typeClient = null;
        if (reponse.getDetailRep().getIdenm() != null) {
            typeClient = TypeClient.PM;
        } else {
            typeClient = TypeClient.PP;
        }

        CreditBureauReportViewAdapter creditBureauReportViewAdapter =
                creditBureauMapper.buildCreditBureauReportViewAdapter(creditBureauDemande, creditInfoRequest,
                        reponse, typeClient);

        byte[] reportBytes = jasperReportService.generatePDFReport(creditBureauReportViewAdapter);

        String numeroIdentite = null;
        if (creditBureauDemande.getTypeClient() == TypeClient.PP) {
            numeroIdentite = creditBureauDemande.getNumeroPieceIdentite();
        } else {
            numeroIdentite = String.valueOf(creditBureauDemande.getRc());
        }

        String reportName = "Rapport de solvabilité_" + numeroIdentite + "_"
                + creditBureauDemande.getDateDemande().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));

        Report report = Report.builder()
                .nom(reportName)
                .type(creditBureauDemande.getTypeClient().toString())
                .creditBureauDemande(creditBureauDemande)
                .dateChargement(LocalDateTime.now())
                .build();

        ReportDTO reportDTO = creditBureauMapper.toDocumentDto(report);
        reportDTO.setData(Base64.getEncoder().encodeToString(reportBytes));

        log.info("End getCreditBureauReport with result: {}", report);

        return reportDTO;
    }


    private String getCanalSource() {
        return Optional.ofNullable(resourceHelper.getCanalSource())
                .orElseThrow(() -> new UnauthorizedUserException("Canal Source missing"));
    }

}
