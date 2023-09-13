package ma.sg.df.creditbureau.service;


import ma.sg.df.creditbureau.domain.*;
import ma.sg.df.creditbureau.dto.*;
import ma.sg.df.creditbureau.mapper.CreditBureauMapper;
import ma.sg.df.creditbureau.repository.CreditBureauDemandeRepository;
import ma.sg.df.creditbureau.repository.DocumentRepository;
import ma.sg.df.creditbureau.repository.UserRepository;
import ma.sg.df.loan.dto.creditinfo.request.RequeteDTO;
import ma.sg.df.loan.dto.creditinfo.response.DetailRepDTO;
import ma.sg.df.loan.dto.creditinfo.response.IdenMDTO;
import ma.sg.df.loan.dto.creditinfo.response.ReponseDTO;
import ma.sg.df.loan.mapper.CreditInfoMapper;
import ma.sg.df.loan.resource.helper.ResourceHelper;
import ma.sg.df.loan.service.CreditInfoService;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;


@RunWith(MockitoJUnitRunner.class)
public class CreditBureauServiceTest {

    private static final String CANAL_SOURCE = "credit-bureau-bff";

    @Mock
    private CreditBureauDemandeRepository creditBureauDemandeRepository;

    @Mock
    private ResourceHelper resourceHelper;
    @Mock
    private CreditInfoService creditInfoService;
    @Mock
    private CreditBureauMapper creditBureauMapper;

    @Mock
    private CreditInfoMapper creditInfoMapper;

    @Mock
    private JasperReportService jasperReportService;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private GedService gedService;

    @Mock
    private EntityManager em;

    @InjectMocks
    private CreditBureauService creditBureauService;

    @Test
    public void shouldGenerateCreditBureauReportForPPClient() throws IOException {
        //given
        CreditBureauDemandeDTO creditBureauDemandeDTO = CreditBureauDemandeDTO.builder().typeClient(TypeClient.PP).build();
        CreditBureauDemande creditBureauDemande = CreditBureauDemande.builder().typeClient(TypeClient.PP).dateDemande(LocalDateTime.now()).build();

        doReturn(creditBureauDemande).when(creditBureauDemandeRepository).save(any());
        doReturn(new Report()).when(documentRepository).save(any());
        doReturn(Optional.of(new CbUser())).when(userRepository).findByEmail(any());
        doReturn(Long.valueOf(1)).when(gedService).saveReport(any(), any());
        doAnswer((dto) -> {
            ((RequeteDTO) dto.getArgument(0)).setCanalSource(CANAL_SOURCE);
            return null;
        }).when(resourceHelper).appendCanalSource(any());
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        doAnswer(invocation -> {
            List<String> creditInfoData = ((List<String>)invocation.getArgument(1));
            creditInfoData.add(0, "xml request");
            creditInfoData.add(1, "xml response");
            return new ReponseDTO();
        }).when(creditInfoService).getReportWithErrors(any(), any(), any());

        doReturn(new CreditBureauReportViewAdapter()).when(creditBureauMapper).buildCreditBureauReportViewAdapter(any(), any(), any(), any());
        doReturn(creditBureauDemande).when(creditBureauMapper).mapCreditBureauRequestDtoToCreditBureauDemande(any());
        doReturn(new RequeteDTO()).when(creditBureauMapper).mapCreditBureauRequestToCreditInfoRequest(any(), any());
        doReturn(new CreditBureauDemandeDTO()).when(creditBureauMapper).mapCreditBureauDemandeToDto(any());
        doReturn(new ReportDTO()).when(creditBureauMapper).toDocumentDto(any());
        doReturn(IOUtils.toByteArray(new DefaultResourceLoader().getResource("classpath:/mock/test.pdf").getInputStream())).when(jasperReportService).generatePDFReport(any());

        //when
        final CreditBureauDemandeDTO result = creditBureauService.generateCreditBureauReport(creditBureauDemandeDTO, "username");

        //then
        assertThat(result, is(notNullValue()));
        assertEquals(1, result.getReports().size());
    }

    @Test
    public void shouldGenerateCreditBureauReportForPMClient() throws IOException {
        //given
        CreditBureauDemandeDTO creditBureauDemandeDTO = CreditBureauDemandeDTO.builder().typeClient(TypeClient.PM).build();
        CreditBureauDemande creditBureauDemande = CreditBureauDemande.builder().typeClient(TypeClient.PM).dateDemande(LocalDateTime.now()).build();

        doReturn(creditBureauDemande).when(creditBureauDemandeRepository).save(any());
        doReturn(new Report()).when(documentRepository).save(any());
        doReturn(Optional.of(new CbUser())).when(userRepository).findByEmail(any());
        doReturn(Long.valueOf(1)).when(gedService).saveReport(any(), any());
        doAnswer((dto) -> {
            ((RequeteDTO) dto.getArgument(0)).setCanalSource(CANAL_SOURCE);
            return null;
        }).when(resourceHelper).appendCanalSource(any());
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        doAnswer(invocation -> {
            List<String> creditInfoData = ((List<String>)invocation.getArgument(1));
            creditInfoData.add(0, "xml request");
            creditInfoData.add(1, "xml response");
            return new ReponseDTO();
        }).when(creditInfoService).getReportWithErrors(any(), any(), any());

        doReturn(new CreditBureauReportViewAdapter()).when(creditBureauMapper).buildCreditBureauReportViewAdapter(any(), any(), any(), any());
        doReturn(creditBureauDemande).when(creditBureauMapper).mapCreditBureauRequestDtoToCreditBureauDemande(any());
        doReturn(new RequeteDTO()).when(creditBureauMapper).mapCreditBureauRequestToCreditInfoRequest(any(), any());
        doReturn(new CreditBureauDemandeDTO()).when(creditBureauMapper).mapCreditBureauDemandeToDto(any());
        doReturn(new ReportDTO()).when(creditBureauMapper).toDocumentDto(any());
        doReturn(IOUtils.toByteArray(new DefaultResourceLoader().getResource("classpath:/mock/test.pdf").getInputStream())).when(jasperReportService).generatePDFReport(any());

        //when
        final CreditBureauDemandeDTO result = creditBureauService.generateCreditBureauReport(creditBureauDemandeDTO, "username");

        //then
        assertThat(result, is(notNullValue()));
        assertEquals(1, result.getReports().size());
    }

    @Test
    public void shouldGenerateCreditBureauReportForPPPROClient() throws IOException {
        //given
        CreditBureauDemandeDTO creditBureauDemandeDTO = CreditBureauDemandeDTO.builder().typeClient(TypeClient.PP_PRO).build();
        CreditBureauDemande creditBureauDemande = CreditBureauDemande.builder().typeClient(TypeClient.PP_PRO).dateDemande(LocalDateTime.now()).build();

        doReturn(creditBureauDemande).when(creditBureauDemandeRepository).save(any());
        doReturn(new Report()).when(documentRepository).save(any());
        doReturn(Optional.of(new CbUser())).when(userRepository).findByEmail(any());
        doReturn(Long.valueOf(1)).when(gedService).saveReport(any(), any());
        doAnswer((dto) -> {
            ((RequeteDTO) dto.getArgument(0)).setCanalSource(CANAL_SOURCE);
            return null;
        }).when(resourceHelper).appendCanalSource(any());
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        doAnswer(invocation -> {
            List<String> creditInfoData = ((List<String>)invocation.getArgument(1));
            creditInfoData.add(0, "xml request");
            creditInfoData.add(1, "xml response");
            return new ReponseDTO();
        }).when(creditInfoService).getReportWithErrors(any(), any(), any());

        doReturn(new CreditBureauReportViewAdapter()).when(creditBureauMapper).buildCreditBureauReportViewAdapter(any(), any(), any(), any());
        doReturn(creditBureauDemande).when(creditBureauMapper).mapCreditBureauRequestDtoToCreditBureauDemande(any());
        doReturn(new RequeteDTO()).when(creditBureauMapper).mapCreditBureauRequestToCreditInfoRequest(any(), any());
        doReturn(new CreditBureauDemandeDTO()).when(creditBureauMapper).mapCreditBureauDemandeToDto(any());
        doReturn(new ReportDTO()).when(creditBureauMapper).toDocumentDto(any());
        doReturn(IOUtils.toByteArray(new DefaultResourceLoader().getResource("classpath:/mock/test.pdf").getInputStream())).when(jasperReportService).generatePDFReport(any());

        //when
        final CreditBureauDemandeDTO result = creditBureauService.generateCreditBureauReport(creditBureauDemandeDTO, "username");

        //then
        assertThat(result, is(notNullValue()));
        assertEquals(2, result.getReports().size());
    }

    @Test
    public void shouldSearchDemandesForOperatorUser() {
        //given
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);
        doReturn(Optional.of(CbUser.builder().roles(Arrays.asList(Role.builder().code("OPERATOR").build())).build())).when(userRepository).findByEmail(any());
        doReturn(new PageImpl(Arrays.asList(new CreditBureauDemande()))).when(creditBureauDemandeRepository).findByCriteria(any(), any());

        //when
        final Page<CreditBureauDemandeDTO> result = creditBureauService.searchDemandes("username", CbDemandeCriteriaDTO.builder().build());

        //then
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void shouldSearchDemandesForPilotageUser() {
        //given
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);
        doReturn(Optional.of(CbUser.builder().roles(Arrays.asList(Role.builder().code("PILOTAGE").build())).build())).when(userRepository).findByEmail(any());
        doReturn(new PageImpl(Arrays.asList(new CreditBureauDemande(), new CreditBureauDemande()))).when(creditBureauDemandeRepository).findByCriteria(any(), any());

        //when
        final Page<CreditBureauDemandeDTO> result = creditBureauService.searchDemandes("username", CbDemandeCriteriaDTO.builder().build());

        //then
        assertEquals(2, result.getContent().size());
    }

    @Test
    public void shouldGetCreditBureauReportFromGed() {
        //given
        doReturn(Optional.of(Report.builder()
                .creditBureauDemande(CreditBureauDemande.builder()
                        .user(CbUser.builder().email("username@mail.com").build())
                        .build())
                        .idDocubase(50L)
                .build()))
                .when(documentRepository).findByUuid(any());
        doReturn(Optional.of(CbUser.builder().roles(Collections.singletonList(Role.builder().code("PILOTAGE").build())).build())).when(userRepository).findByEmail(any());

        doReturn("b64Doc").when(gedService).getDocument(any());
        doReturn(new ReportDTO()).when(creditBureauMapper).toDocumentDto(any());

        //when
        final ReportDTO result = creditBureauService.getCreditBureauReport("uuid", "username@mail.com");

        //then
        assertEquals("b64Doc", result.getData());
    }

    @Test
    public void shouldGetCreditBureauReportFromXML() throws IOException {
        //given
        ReponseDTO reponseDTO = new ReponseDTO();
        DetailRepDTO detailRepDTO = new DetailRepDTO();
        detailRepDTO.setIdenm(new IdenMDTO());
        reponseDTO.setDetailRep(detailRepDTO);
        doReturn(Optional.of(Report.builder()
                .creditInfoResponse("<REPONSE><DETAILREP><IDENP><coNm>ZARBI</coNm></IDENP></DETAILREP></REPONSE>")
                .creditBureauDemande(CreditBureauDemande.builder()
                        .typeClient(TypeClient.PP)
                        .rc(BigInteger.valueOf(8457499))
                        .dateDemande(LocalDateTime.now())
                        .user(CbUser.builder().email("username@mail.com").build())
                        .build())
                        .idDocubase(null)
                .build()))
                .when(documentRepository).findByUuid(any());
        doReturn(Optional.of(CbUser.builder().roles(Collections.singletonList(Role.builder().code("PILOTAGE").build())).build())).when(userRepository).findByEmail(any());
        doReturn(new RequeteDTO()).when(creditBureauMapper).mapCreditBureauRequestToCreditInfoRequest(any(), any());
        doReturn(reponseDTO)
                .when(creditInfoMapper).mapResponseToResponseDTO(any());
        doReturn(new CreditBureauReportViewAdapter()).when(creditBureauMapper).buildCreditBureauReportViewAdapter(any(), any(), any(), any());
        doReturn(new ReportDTO()).when(creditBureauMapper).toDocumentDto(any());
        doReturn(IOUtils.toByteArray(new DefaultResourceLoader().getResource("classpath:/mock/test.pdf").getInputStream())).when(jasperReportService).generatePDFReport(any());

        //when
        final ReportDTO result = creditBureauService.getCreditBureauReport("uuid", "username@mail.com");

        //then
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void shouldGetDemandeByUuid() {
        //given
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);
        doReturn(Optional.of(new CbUser())).when(userRepository).findByEmail(any());
        doReturn(Optional.of(new CreditBureauDemande())).when(creditBureauDemandeRepository).findFirstByUuidAndCanalSource(any(), any());
        doReturn(CreditBureauDemandeDTO.builder().user(UserDto.builder().email("username@mail.com").build()).build()).when(creditBureauMapper).mapCreditBureauDemandeToDto(any());

        //when
        final CreditBureauDemandeDTO result = creditBureauService.getByUuidAndUsername("uuid", "username@mail.com");

        //then
        assertThat(result, is(notNullValue()));
    }

}
