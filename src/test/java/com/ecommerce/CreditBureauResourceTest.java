package ma.sg.df.creditbureau.resource;

import localhost.cbv2_wsdl.Cbv2PortType;
import lombok.SneakyThrows;
import ma.sg.df.creditbureau.client.GedApiClient;
import ma.sg.df.creditbureau.domain.TypeClient;
import ma.sg.df.creditbureau.dto.CreditBureauDemandeDTO;
import ma.sg.df.creditbureau.dto.PMIdentDto;
import ma.sg.df.creditbureau.dto.PPIdentDto;
import ma.sg.df.creditbureau.dto.ReferentielDTO;
import ma.sg.df.creditbureau.dto.ged.DataDTO;
import ma.sg.df.creditbureau.dto.ged.GetDocumentResponse;
import ma.sg.df.creditbureau.dto.ged.GetDocumentResponseDTO;
import ma.sg.df.creditbureau.dto.ged.StoreDocumentResponseDTO;
import ma.sg.df.loan.dto.creditinfo.request.RequeteDTO;
import ma.sg.df.loan.resource.helper.ResourceHelper;
import ma.sg.df.loan.service.AuditService;
import ma.sg.df.loan.util.JSONUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigInteger;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class CreditBureauResourceTest {

    private static final String CREDIT_BUREAU_ENDPOINT = "/credit-bureau";
    private static final String CANAL_SOURCE = "credit-bureau-bff";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GedApiClient gedApiClient;

    @MockBean(name = "creditInfoReportProxy")
    private Cbv2PortType portType;

    @MockBean(name = "creditInfoReportPlusProxy")
    private Cbv2PortType portTypePlus;

    @MockBean
    private ResourceHelper resourceHelper;

    @MockBean
    private AuditService auditService;


    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldCreateCreditBureauDemandeMultiError() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildValidCreditBureauDemandeDto();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        given(portType.process(any()))
                .willReturn(IOUtils.toString(new ClassPathResource("/mock/Response_PP_err.xml").getInputStream()));
        doAnswer((dto) -> {
            ((RequeteDTO) dto.getArgument(0)).setCanalSource(CANAL_SOURCE);
            return null;
        }).when(resourceHelper).appendCanalSource(any());
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        given(gedApiClient.getDocument(any(), any(), any()))
                .willReturn(ResponseEntity.ok(GetDocumentResponseDTO.builder().build()));

        given(gedApiClient.storeDocument(any(), any()))
                .willReturn(ResponseEntity.ok(StoreDocumentResponseDTO.builder()
                        .data(DataDTO.builder().idDocument(1L).build()).build()));

        doNothing().when(auditService).audit(any());


        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .header("x-user", "operator1User@mail.com")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getResponse().getStatus());
    }

    @Test
    @SneakyThrows
    public void shouldReturnUnauthorized() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildInvalidCreditBureauDemandeRequeteDTO();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnBadRequest() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildInvalidCreditBureauDemandeRequeteDTO();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnBadRequestGivenIdentSegmentMissing() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildInvalidCreditBureauDemandeRequeteDTO2();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnBadRequestCauseTypeClientIdentSegmentMismatch() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildInvalidCreditBureauDemandeRequeteDTO3();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnBadRequestGivenEmptyIdentSegment() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildInvalidCreditBureauDemandeRequeteDTO4();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnBadRequestGivenReferentialInvalid() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildInvalidCreditBureauDemandeRequeteDTO5();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnBadRequestGivenDateCreationInvalid() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildInvalidCreditBureauDemandeRequeteDTO7();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnBadRequestGivenVilleTribunalMissing() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildInvalidCreditBureauDemandeRequeteDTO8();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnBadRequestForPPProGivenVilleTribunalMissing() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildInvalidCreditBureauDemandeRequeteDTO9();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnUNAUTHORIZEDGivenCanalSourceMissing() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildValidCreditBureauDemandeDto();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .header("x-user", "operator1User@mail.com")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldCreateCreditBureauDemande() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildValidCreditBureauDemandeDto();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        given(portType.process(any()))
                .willReturn(IOUtils.toString(new ClassPathResource("/mock/Response_PP.xml").getInputStream()));
        doAnswer((dto) -> {
            ((RequeteDTO) dto.getArgument(0)).setCanalSource(CANAL_SOURCE);
            return null;
        }).when(resourceHelper).appendCanalSource(any());
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        given(gedApiClient.getDocument(any(), any(), any()))
                .willReturn(ResponseEntity.ok(GetDocumentResponseDTO.builder().build()));

        given(gedApiClient.storeDocument(any(), any()))
                .willReturn(ResponseEntity.ok(StoreDocumentResponseDTO.builder()
                        .data(DataDTO.builder().idDocument(1L).build()).build()));

        doNothing().when(auditService).audit(any());

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .header("x-user", "operator1User@mail.com")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldCreateCreditBureauDemandePlus() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildValidCreditBureauDemandeDtoPlus();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        given(portTypePlus.process(any()))
                .willReturn(IOUtils.toString(new ClassPathResource("/mock/ResponseReportPlus_PP.xml").getInputStream()));
        doAnswer((dto) -> {
            ((RequeteDTO) dto.getArgument(0)).setCanalSource(CANAL_SOURCE);
            return null;
        }).when(resourceHelper).appendCanalSource(any());
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        given(gedApiClient.getDocument(any(), any(), any()))
                .willReturn(ResponseEntity.ok(GetDocumentResponseDTO.builder().build()));

        given(gedApiClient.storeDocument(any(), any()))
                .willReturn(ResponseEntity.ok(StoreDocumentResponseDTO.builder()
                        .data(DataDTO.builder().idDocument(1L).build()).build()));

        doNothing().when(auditService).audit(any());

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .header("x-user", "operator1User@mail.com")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnBadRequestGivenDemandeDoesntExists() {
        // given

        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(CREDIT_BUREAU_ENDPOINT + "/demandes/dummy")
                .accept(MediaType.APPLICATION_JSON)
                .header("x-user", "operator2User");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnUnauthorizedWhenTryingToGetDemandeCreatedByDifferentUser() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildValidCreditBureauDemandeDto();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        given(portType.process(any()))
                .willReturn(IOUtils.toString(new ClassPathResource("/mock/Response_PP.xml").getInputStream()));
        doAnswer((dto) -> {
            ((RequeteDTO) dto.getArgument(0)).setCanalSource(CANAL_SOURCE);
            return null;
        }).when(resourceHelper).appendCanalSource(any());
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        given(gedApiClient.getDocument(any(), any(), any()))
                .willReturn(ResponseEntity.ok(GetDocumentResponseDTO.builder().build()));

        given(gedApiClient.storeDocument(any(), any()))
                .willReturn(ResponseEntity.ok(StoreDocumentResponseDTO.builder()
                        .data(DataDTO.builder().idDocument(1L).build()).build()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("x-user", "operator1User@mail.com");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        CreditBureauDemandeDTO response = JSONUtil.mapFromJson(result.getResponse().getContentAsString(), CreditBureauDemandeDTO.class);

        // When
        requestBuilder = MockMvcRequestBuilders.get(CREDIT_BUREAU_ENDPOINT + "/demandes/" + response.getUuid())
                .accept(MediaType.APPLICATION_JSON)
                .header("x-user", "operator2User@mail.com");

        result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnDemande() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildValidCreditBureauDemandeDto();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        given(portType.process(any()))
                .willReturn(IOUtils.toString(new ClassPathResource("/mock/Response_PP.xml").getInputStream()));
        doAnswer((dto) -> {
            ((RequeteDTO) dto.getArgument(0)).setCanalSource(CANAL_SOURCE);
            return null;
        }).when(resourceHelper).appendCanalSource(any());
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        given(gedApiClient.getDocument(any(), any(), any()))
                .willReturn(ResponseEntity.ok(GetDocumentResponseDTO.builder().build()));

        given(gedApiClient.storeDocument(any(), any()))
                .willReturn(ResponseEntity.ok(StoreDocumentResponseDTO.builder()
                        .data(DataDTO.builder().idDocument(1L).build()).build()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("x-user", "operator1User@mail.com");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        CreditBureauDemandeDTO response = JSONUtil.mapFromJson(result.getResponse().getContentAsString(), CreditBureauDemandeDTO.class);

        // When
        requestBuilder = MockMvcRequestBuilders.get(CREDIT_BUREAU_ENDPOINT + "/demandes/" + response.getUuid())
                .accept(MediaType.APPLICATION_JSON)
                .header("x-user", "operator1User@mail.com");

        result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnListDemandes() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildValidCreditBureauDemandeDto();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        given(portType.process(any()))
                .willReturn(IOUtils.toString(new ClassPathResource("/mock/Response_PP.xml").getInputStream()));
        doAnswer((dto) -> {
            ((RequeteDTO) dto.getArgument(0)).setCanalSource(CANAL_SOURCE);
            return null;
        }).when(resourceHelper).appendCanalSource(any());
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        given(gedApiClient.getDocument(any(), any(), any()))
                .willReturn(ResponseEntity.ok(GetDocumentResponseDTO.builder().build()));

        given(gedApiClient.storeDocument(any(), any()))
                .willReturn(ResponseEntity.ok(StoreDocumentResponseDTO.builder()
                        .data(DataDTO.builder().idDocument(1L).build()).build()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("x-user", "operator1User@mail.com");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        CreditBureauDemandeDTO response = JSONUtil.mapFromJson(result.getResponse().getContentAsString(), CreditBureauDemandeDTO.class);

        // When
        requestBuilder = MockMvcRequestBuilders.get(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .accept(MediaType.APPLICATION_JSON)
                .header("x-user", "operator1User@mail.com");

        result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnBadRequestGivenReportDoesntExists() {
        // given

        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(CREDIT_BUREAU_ENDPOINT + "/reports/dummy")
                .accept(MediaType.APPLICATION_JSON)
                .header("x-user", "operator2User");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(authorities = {"API_CORE_CREDIT_BUREAU"})
    @SneakyThrows
    public void shouldReturnReport() {
        // given
        CreditBureauDemandeDTO requeteDTO = buildValidCreditBureauDemandeDto();
        String inputJson = JSONUtil.mapToJson(requeteDTO);

        given(portType.process(any()))
                .willReturn(IOUtils.toString(new ClassPathResource("/mock/Response_PP.xml").getInputStream()));
        doAnswer((dto) -> {
            ((RequeteDTO) dto.getArgument(0)).setCanalSource(CANAL_SOURCE);
            return null;
        }).when(resourceHelper).appendCanalSource(any());
        given(resourceHelper.getCanalSource()).willReturn(CANAL_SOURCE);

        given(gedApiClient.getDocument(any(), any(), any()))
                .willReturn(ResponseEntity.ok(GetDocumentResponseDTO.builder().getDocumentResponse(GetDocumentResponse.builder().document(new String[]{"b64Docccccccccccdd"}).build()).build()));

        given(gedApiClient.storeDocument(any(), any()))
                .willReturn(ResponseEntity.ok(StoreDocumentResponseDTO.builder()
                        .data(DataDTO.builder().idDocument(1L).build()).build()));

        doNothing().when(auditService).audit(any());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(CREDIT_BUREAU_ENDPOINT + "/demandes")
                .content(inputJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("x-user", "operator1User@mail.com");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        CreditBureauDemandeDTO response = JSONUtil.mapFromJson(result.getResponse().getContentAsString(), CreditBureauDemandeDTO.class);

        // When
        requestBuilder = MockMvcRequestBuilders.get(CREDIT_BUREAU_ENDPOINT + "/reports/" + response.getReports().get(0).getUuid())
                .accept(MediaType.APPLICATION_JSON).header("x-user", "operator1User@mail.com");

        result = mockMvc.perform(requestBuilder).andReturn();

        // Then
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    private CreditBureauDemandeDTO buildInvalidCreditBureauDemandeRequeteDTO() {
        return CreditBureauDemandeDTO.builder().build();
    }

    private CreditBureauDemandeDTO buildInvalidCreditBureauDemandeRequeteDTO2() {

        ReferentielDTO oc = new ReferentielDTO("1", "oc");
        ReferentielDTO tc = new ReferentielDTO("1", "tc");
        ReferentielDTO rr = new ReferentielDTO("1", "rr");

        return CreditBureauDemandeDTO.builder().dateFinancement(LocalDate.now()).montant(10000d).objetCredit(oc)
                .typeCredit(tc).raisonRequete(rr).typeClient(TypeClient.PP).typeContrat(tc).build();
    }

    private CreditBureauDemandeDTO buildInvalidCreditBureauDemandeRequeteDTO3() {

        ReferentielDTO oc = new ReferentielDTO("1", "oc");
        ReferentielDTO tc = new ReferentielDTO("1", "tc");
        ReferentielDTO rr = new ReferentielDTO("1", "rr");

        ReferentielDTO vs = new ReferentielDTO("vs", "vs");
        ReferentielDTO vt = new ReferentielDTO("vt", "vt");
        ReferentielDTO tr = new ReferentielDTO("Rapport Plus", "13.5");


        PMIdentDto pmIden = PMIdentDto.builder().dateCreation(LocalDate.now()).raisonSociale("rs")
                .rc(BigInteger.valueOf(1L)).villeSiege(vs).villeTribunal(vt).build();

        return CreditBureauDemandeDTO.builder().dateFinancement(LocalDate.now()).montant(10000d).objetCredit(oc)
                .typeCredit(tc).raisonRequete(rr).typeClient(TypeClient.PP).typeContrat(tc).pmIdent(pmIden).typeRapport(tr).build();
    }

    private CreditBureauDemandeDTO buildInvalidCreditBureauDemandeRequeteDTO4() {

        ReferentielDTO oc = new ReferentielDTO("1", "oc");
        ReferentielDTO tc = new ReferentielDTO("1", "tc");
        ReferentielDTO tcr = new ReferentielDTO("1", "tcr");
        ReferentielDTO rr = new ReferentielDTO("1", "rr");
        ReferentielDTO tr = new ReferentielDTO("Rapport Normal", "13.5");

        PPIdentDto ppIdentDto = PPIdentDto.builder().build();

        return CreditBureauDemandeDTO.builder().dateFinancement(LocalDate.now()).montant(10000d).typeCredit(tcr)
                .objetCredit(oc).raisonRequete(rr).typeClient(TypeClient.PP).typeContrat(tc).ppIdent(ppIdentDto).typeRapport(tr)
                .build();
    }

    private CreditBureauDemandeDTO buildInvalidCreditBureauDemandeRequeteDTO5() {

        ReferentielDTO oc = new ReferentielDTO("1", "1");
        ReferentielDTO tc = new ReferentielDTO("1", "1");
        ReferentielDTO tcr = new ReferentielDTO("1", "1");
        ReferentielDTO rr = new ReferentielDTO("1", "1");
        ReferentielDTO tr = new ReferentielDTO("Rapport Normal", "13.5");

        PPIdentDto ppIdentDto = PPIdentDto.builder().build();

        return CreditBureauDemandeDTO.builder().dateFinancement(LocalDate.now()).montant(10000d).typeCredit(tcr)
                .objetCredit(oc).raisonRequete(rr).typeClient(TypeClient.PP).typeContrat(tc).ppIdent(ppIdentDto).typeRapport(tr)
                .build();
    }

    private CreditBureauDemandeDTO buildValidCreditBureauDemandeDto() {

        ReferentielDTO oc = new ReferentielDTO("oc", "oc");
        ReferentielDTO tc = new ReferentielDTO("1", "tc");
        ReferentielDTO tcr = new ReferentielDTO("tcr", "tcr");
        ReferentielDTO rr = new ReferentielDTO("rr", "rr");
        ReferentielDTO tr = new ReferentielDTO("Rapport Normal", "13.5");

        ReferentielDTO npi = new ReferentielDTO("npi", "npi");
        ReferentielDTO sexe = new ReferentielDTO("M", "M");
        ReferentielDTO vr = new ReferentielDTO("vr", "vr");
        PPIdentDto ppIdentDto = PPIdentDto.builder().dateNaissance(LocalDate.now().minusYears(20))
                .naturePieceIdentite(npi).nom("nom").prenom("prenom").numeroPieceIdentite("npi").sexe(sexe)
                .villeResidence(vr).build();

        return CreditBureauDemandeDTO.builder().dateFinancement(LocalDate.now()).montant(10000d).typeCredit(tcr)
                .objetCredit(oc).raisonRequete(rr).typeClient(TypeClient.PP).typeContrat(tc).ppIdent(ppIdentDto)
                .typeRapport(tr)
                .build();
    }

    private CreditBureauDemandeDTO buildValidCreditBureauDemandeDtoPlus() {

        ReferentielDTO oc = new ReferentielDTO("oc", "oc");
        ReferentielDTO tc = new ReferentielDTO("1", "tc");
        ReferentielDTO tcr = new ReferentielDTO("tcr", "tcr");
        ReferentielDTO rr = new ReferentielDTO("rr", "rr");
        ReferentielDTO tr = new ReferentielDTO("Rapport Plus", "15");

        ReferentielDTO npi = new ReferentielDTO("npi", "npi");
        ReferentielDTO sexe = new ReferentielDTO("M", "M");
        ReferentielDTO vr = new ReferentielDTO("vr", "vr");
        PPIdentDto ppIdentDto = PPIdentDto.builder().dateNaissance(LocalDate.now().minusYears(20))
                .naturePieceIdentite(npi).nom("nom").prenom("prenom").numeroPieceIdentite("npi").sexe(sexe)
                .villeResidence(vr).build();

        return CreditBureauDemandeDTO.builder().dateFinancement(LocalDate.now()).montant(10000d).typeCredit(tcr)
                .objetCredit(oc).raisonRequete(rr).typeClient(TypeClient.PP).typeContrat(tc).ppIdent(ppIdentDto)
                .typeRapport(tr)
                .build();
    }

    private CreditBureauDemandeDTO buildInvalidCreditBureauDemandeRequeteDTO7() {

        ReferentielDTO oc = new ReferentielDTO("oc", "oc");
        ReferentielDTO tc = new ReferentielDTO("1", "tc");
        ReferentielDTO tcr = new ReferentielDTO("tcr", "tcr");
        ReferentielDTO rr = new ReferentielDTO("rr", "rr");
        ReferentielDTO tr = new ReferentielDTO("Rapport Normal", "13.5");


        ReferentielDTO vs = new ReferentielDTO("vs", "vs");
        ReferentielDTO vt = new ReferentielDTO("vt", "vt");

        PMIdentDto pmIden = PMIdentDto.builder().dateCreation(LocalDate.now().plusDays(3)).raisonSociale("rs")
                .rc(BigInteger.valueOf(1L)).villeSiege(vs).villeTribunal(vt).build();

        return CreditBureauDemandeDTO.builder().dateFinancement(LocalDate.now()).montant(10000d).typeCredit(tcr)
                .objetCredit(oc).raisonRequete(rr).typeClient(TypeClient.PM).typeContrat(tc).pmIdent(pmIden).typeRapport(tr).build();
    }

    private CreditBureauDemandeDTO buildInvalidCreditBureauDemandeRequeteDTO8() {

        ReferentielDTO oc = new ReferentielDTO("oc", "oc");
        ReferentielDTO tc = new ReferentielDTO("1", "tc");
        ReferentielDTO tcr = new ReferentielDTO("tcr", "tcr");
        ReferentielDTO rr = new ReferentielDTO("rr", "rr");
        ReferentielDTO tr = new ReferentielDTO("Rapport Normal", "13.5");


        ReferentielDTO vs = new ReferentielDTO("vs", "vs");

        PMIdentDto pmIden = PMIdentDto.builder().dateCreation(LocalDate.now()).raisonSociale("rs")
                .rc(BigInteger.valueOf(1L)).villeSiege(vs).build();

        return CreditBureauDemandeDTO.builder().dateFinancement(LocalDate.now()).montant(10000d).typeCredit(tcr)
                .objetCredit(oc).raisonRequete(rr).typeClient(TypeClient.PM).typeContrat(tc).pmIdent(pmIden).typeRapport(tr).build();
    }

    private CreditBureauDemandeDTO buildInvalidCreditBureauDemandeRequeteDTO9() {

        ReferentielDTO npi = new ReferentielDTO("npi", "npi");
        ReferentielDTO sexe = new ReferentielDTO("M", "M");
        ReferentielDTO vr = new ReferentielDTO("vr", "vr");
        ReferentielDTO oc = new ReferentielDTO("oc", "oc");
        ReferentielDTO tc = new ReferentielDTO("1", "tc");
        ReferentielDTO tcr = new ReferentielDTO("tcr", "tcr");
        ReferentielDTO rr = new ReferentielDTO("rr", "rr");
        ReferentielDTO vs = new ReferentielDTO("vs", "vs");
        ReferentielDTO tr = new ReferentielDTO("Rapport Normal", "13.5");


        PPIdentDto ppIdentDto = PPIdentDto.builder().dateNaissance(LocalDate.now().minusYears(20))
                .naturePieceIdentite(npi).nom("nom").prenom("prenom").numeroPieceIdentite("npi").sexe(sexe)
                .villeResidence(vr).build();
        PMIdentDto pmIdentDto = PMIdentDto.builder().dateCreation(LocalDate.now()).raisonSociale("rs").rc(BigInteger.valueOf(1L))
                .villeSiege(vs).build();

        return CreditBureauDemandeDTO.builder().dateFinancement(LocalDate.now()).montant(10000d).typeCredit(tcr)
                .objetCredit(oc).raisonRequete(rr).typeClient(TypeClient.PP_PRO).typeContrat(tc).typeRapport(tr)
                .ppIdent(ppIdentDto)
                .pmIdent(pmIdentDto)
                .build();
    }

}
