package ma.sg.df.creditbureau.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.sg.df.creditbureau.domain.CbUser;
import ma.sg.df.creditbureau.domain.TypeClient;
import ma.sg.df.creditbureau.dto.CbDemandeCriteriaDTO;
import ma.sg.df.creditbureau.dto.CreditBureauDemandeDTO;
import ma.sg.df.creditbureau.dto.ReportDTO;
import ma.sg.df.creditbureau.repository.UserRepository;
import ma.sg.df.creditbureau.service.Action;
import ma.sg.df.creditbureau.service.AuthorizationService;
import ma.sg.df.creditbureau.service.CreditBureauService;
import ma.sg.df.creditbureau.service.UserService;
import ma.sg.df.loan.audit.Event;
import ma.sg.df.loan.config.GenericApiResponses;
import ma.sg.df.loan.resource.helper.ResourceHelper;
import ma.sg.df.loan.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping("credit-bureau")
public class CreditBureauResource {

    public static final String TAG_CREDIT_BUREAU = "credit-bureau";

    private final CreditBureauService creditBureauService;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final ResourceHelper resourceHelper;
    private final AuthorizationService authorizationService;

    @PostMapping(path = "/demandes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    @GenericApiResponses
    @Operation(summary = "Generate credit bureau Pdf report", description = "This resource is used to generate and return a pdf report based on informations retrieved from credit info endpoint", tags = TAG_CREDIT_BUREAU, responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation, check payload content")})
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<CreditBureauDemandeDTO> generateCreditBureauPDFReport(
            @RequestHeader(name = "X-USER", required = false) String username,
            @RequestBody @Valid CreditBureauDemandeDTO creditBureauRequestDTO) {
        log.info("Start resource generate credit bureau report by params: {}", creditBureauRequestDTO);

        CreditBureauDemandeDTO creditBureauResponseDTO = this.creditBureauService
                .generateCreditBureauReport(creditBureauRequestDTO, username);

        String message = "generate credit bureau report by params : " + creditBureauRequestDTO;
        auditService.audit(resourceHelper.buildAuditRequestDTO(message, Event.GENERATE_CREDIT_BUREAU_REPORT));

        HttpHeaders headers = new HttpHeaders();

        log.info("End resource generate credit bureau report by params: {}", creditBureauRequestDTO);

        if (creditBureauResponseDTO.getReports().size() == 1 && (creditBureauResponseDTO.getReports().get(0).getTaille() > 0) ||
                creditBureauResponseDTO.getReports().size() == 2 && (creditBureauResponseDTO.getReports().get(0).getTaille() > 0) && (creditBureauResponseDTO.getReports().get(1).getTaille() > 0)) {

            headers.setLocation(
                    linkTo(methodOn(this.getClass()).getCreditBureauDemandesByUuid(creditBureauResponseDTO.getUuid(), null))
                            .toUri());
            return ResponseEntity.ok().headers(headers).body(creditBureauResponseDTO);

        } else {
            return ResponseEntity.status(509).body(creditBureauResponseDTO);

        }
    }

    @GetMapping(path = "/demandes", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<CreditBureauDemandeDTO>> searchCreditBureauDemandes(
            @RequestHeader(name = "X-USER", required = false) String user,
            @RequestParam(value = "numInterrogation", required = false) final String numInterrogation,
            @RequestParam(value = "typeClient", required = false) final String typeClient,
            @RequestParam(value = "nomClient", required = false) final String nomClient,
            @RequestParam(value = "dateDemandeFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateDemandeFrom,
            @RequestParam(value = "dateDemandeTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateDemandeTo,
            @RequestParam(value = "gestionnaire", required = false) final String gestionnaire,
            @PageableDefault(page = 0, size = 10) final Pageable pageable) {

        log.info("[Credit-bureau - Demandes] Start resource search credit bureau demandes by params : {}", user);

        authorizationService.authorize(user, Action.GET_DEMANDES);

        final CbDemandeCriteriaDTO cbDemandeCriteriaDTO = CbDemandeCriteriaDTO.builder()
                .numInterrogation(numInterrogation)
                .typeClient(Optional.ofNullable(typeClient).map(TypeClient::valueOf).orElse(null))
                .client(nomClient)
                .dateDemandeFrom(dateDemandeFrom)
                .dateDemandeTo(dateDemandeTo)
                .gestionnaire(gestionnaire)
                .pageable(pageable).build();


        Page<CreditBureauDemandeDTO> creditBureauResponseDTO = this.creditBureauService.searchDemandes(user, cbDemandeCriteriaDTO);

        log.info("[Credit-bureau - Demandes] End resource search credit bureau demandes by params : {}", user);
        return ResponseEntity.ok(creditBureauResponseDTO);
    }

    @GetMapping(path = "/demandes/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CreditBureauDemandeDTO> getCreditBureauDemandesByUuid(@PathVariable("uuid") String uuid,
                                                                                @RequestHeader(name = "X-USER") @NotEmpty(message = "error.notNullEmpty") String user) {

        log.info("[Credit-bureau - Demandes] Start resource get credit bureau demande by uuid : {}", uuid);

        authorizationService.authorize(user, Action.GET_DEMANDES);

        CreditBureauDemandeDTO creditBureauResponseDTO = this.creditBureauService.getByUuidAndUsername(uuid, user);

        log.info("[Credit-bureau - Demandes] End resource get credit bureau demande by uuid : {}", uuid);
        return ResponseEntity.ok(creditBureauResponseDTO);
    }

    @GetMapping(path = "/reports/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @GenericApiResponses
    @Operation(summary = "Retrieve report credit info", description = "This resource is used to get an existing credit bureau pdf report", tags = TAG_CREDIT_BUREAU, responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation, check payload content")})
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<ReportDTO> getCreditBureauReport(@PathVariable(name = "uuid") String uuidReport,
                                                           @RequestHeader(name = "X-USER", required = false) String user) {

        log.info("[Credit-bureau - Demandes] Start resource get credit bureau report by uuidReport: {}", uuidReport);

        authorizationService.authorize(user, Action.GET_DEMANDES);

        ReportDTO reponseDTO = this.creditBureauService.getCreditBureauReport(uuidReport, user);

        String message = "get credit bureau pdf report by params : " + uuidReport;
        auditService.audit(resourceHelper.buildAuditRequestDTO(message, Event.GET_CREDIT_BUREAU_REPORT));

        ResponseEntity<ReportDTO> responseEntity = new ResponseEntity<>(reponseDTO, HttpStatus.OK);

        log.info("[Credit-bureau - Demandes] End resource get credit bureau report by uuidReport: {}", uuidReport);
        return responseEntity;
    }

}
