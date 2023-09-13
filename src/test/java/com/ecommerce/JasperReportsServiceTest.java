package ma.sg.df.creditbureau.service;


import ma.sg.df.creditbureau.domain.TypeClient;
import ma.sg.df.creditbureau.dto.CreditBureauReportViewAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class JasperReportsServiceTest {

    @InjectMocks
    private JasperReportService jasperReportService;



    @Test
    public void shouldGenerateReport() {
        //given

        //when
        final byte[] result = jasperReportService.generatePDFReport(CreditBureauReportViewAdapter.builder().typeClient(TypeClient.PP).build());

        //then
        assertThat(result, is(notNullValue()));
    }

}
