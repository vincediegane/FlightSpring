package ma.sg.df.creditbureau.service;

import ma.sg.df.creditbureau.dto.CreditBureauReportViewAdapter;
import ma.sg.df.loan.dto.creditinfo.response.*;
import ma.sg.df.loan.exception.TechnicalException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Service
public class JasperReportService {

	public byte[] generatePDFReport(CreditBureauReportViewAdapter creditBureauReportViewAdapter) {

		try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {

			JasperReport jasperReport = loadJasperTpl();

			Map<String, Object> parameters = initReportParams(creditBureauReportViewAdapter);

			final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
					Collections.singletonList(creditBureauReportViewAdapter));

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			exportAsPdf(jasperPrint, fos);


			return fos.toByteArray();

		} catch (Exception e) {
			throw new TechnicalException(e.getMessage(), e);
		}

	}

	private Map<String, Object> initReportParams(CreditBureauReportViewAdapter creditBureauReportViewAdapter) throws IOException {
		final Map<String, Object> parameters = new HashMap<>();

		parameters.put( JRParameter.REPORT_LOCALE, Locale.FRENCH );

		parameters.put("typeClient", creditBureauReportViewAdapter.getTypeClient().name());
		parameters.put("buNm", Optional.ofNullable(creditBureauReportViewAdapter.getCreditInfoResponse()).map(ReponseDTO::getDetailRep).map(DetailRepDTO::getIdenm).map(IdenMDTO::getBuNm).orElse(null));
		parameters.put("coNm", Optional.ofNullable(creditBureauReportViewAdapter.getCreditInfoResponse()).map(ReponseDTO::getDetailRep).map(DetailRepDTO::getIdenP).map(IdenPDTO::getCoNm).orElse(null));
		parameters.put("coFnm", Optional.ofNullable(creditBureauReportViewAdapter.getCreditInfoResponse()).map(ReponseDTO::getDetailRep).map(DetailRepDTO::getIdenP).map(IdenPDTO::getCoFnm).orElse(null));

		//
		parameters.put("dashboard_subreport_location", getClass().getResourceAsStream("/reports/dashboard.jasper"));
		parameters.put("demande_en_cours_subreport_location",
				getClass().getResourceAsStream("/reports/demande_en_cours.jasper"));
		parameters.put("informations_entreprise_subreport_location",
				getClass().getResourceAsStream("/reports/informations_entreprise.jasper"));
		parameters.put("historique_informations_subreport_location",
				getClass().getResourceAsStream("/reports/historique_informations.jasper"));
		parameters.put("liste_contrats_subreport_location",
				getClass().getResourceAsStream("/reports/liste_contrats.jasper"));
		parameters.put("details_contrat_subreport_location",
				getClass().getResourceAsStream("/reports/details_contrat.jasper"));

		// Detail Contrat and historiques paiments loaded from within jasper as inputstreams

		parameters.put("requetes_precedentes_subreport_location",
				getClass().getResourceAsStream("/reports/requetes_precedentes.jasper"));
		parameters.put("sommaire_subreport_location",
				getClass().getResourceAsStream("/reports/sommaire.jasper"));
		parameters.put("10_dernieres_requetes_subreport_location",
				getClass().getResourceAsStream("/reports/10_dernieres_requetes.jasper"));
		parameters.put("identification_subreport_location",
				getClass().getResourceAsStream("/reports/identification.jasper"));
		parameters.put("entete_rapport_subreport_location",
				getClass().getResourceAsStream("/reports/entete_rapport.jasper"));
		// add cip parameter:
		parameters.put("cip_subreport_location",
				getClass().getResourceAsStream("/reports/cip.jasper"));

		BufferedImage imageCoin = ImageIO.read(getClass().getResourceAsStream("/reports/img/coin.png"));
		BufferedImage imageCoinFill = ImageIO.read(getClass().getResourceAsStream("/reports/img/coinFill.png"));
		BufferedImage imageCheck = ImageIO.read(getClass().getResourceAsStream("/reports/img/check.png"));
		BufferedImage imageRedArrow = ImageIO.read(getClass().getResourceAsStream("/reports/img/redArrow.png"));
		BufferedImage imageGreenArrow = ImageIO.read(getClass().getResourceAsStream("/reports/img/greenArrow.png"));
		BufferedImage imageYellowArrow = ImageIO.read(getClass().getResourceAsStream("/reports/img/yellowArrow.png"));

		parameters.put("img_coin_stream", imageCoin);
		parameters.put("img_coinFill_stream", imageCoinFill);
		parameters.put("img_check_stream", imageCheck);
		parameters.put("img_redArrow_stream", imageRedArrow);
		parameters.put("img_greenArrow_stream", imageGreenArrow);
		parameters.put("img_yellowArrow_stream", imageYellowArrow);

		// Use report data source for subreports ??
		parameters.put("dashboard_datasource", new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(
				Collections.singletonList(creditBureauReportViewAdapter)));
		parameters.put("demande_en_cours_datasource", new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(
				Collections.singletonList(creditBureauReportViewAdapter)));
		parameters.put("informations_entreprise_datasource",
				new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(
						Collections.singletonList(creditBureauReportViewAdapter)));
		parameters.put("historique_informations_datasource",
				new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(
						Collections.singletonList(creditBureauReportViewAdapter)));
		parameters.put("liste_contrats_datasource", new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(
				Collections.singletonList(creditBureauReportViewAdapter)));
		parameters.put("details_contrat_datasource", new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(
				Collections.singletonList(creditBureauReportViewAdapter)));
		parameters.put("detail_contrat_datasource", new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(
				Collections.singletonList(creditBureauReportViewAdapter)));
		parameters.put("historique_paiements_datasource",
				new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(
						Collections.singletonList(creditBureauReportViewAdapter)));
		parameters.put("requetes_precedentes_datasource", new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(
				Collections.singletonList(creditBureauReportViewAdapter)));
		parameters.put("identification_datasource", new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(
				Collections.singletonList(creditBureauReportViewAdapter)));
		parameters.put("entete_rapport_datasource", new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(
				Collections.singletonList(creditBureauReportViewAdapter)));
		// CIP datasource
		parameters.put("cip_datasource", new JRBeanCollectionDataSource(
				Collections.singletonList(creditBureauReportViewAdapter)));
		return parameters;
	}

	private JasperReport loadJasperTpl() throws JRException {

		// Get Template from DB/storage server ??
		String simulationCompiledTpl = "/reports/rapport_solvabilite.jasper";

		InputStream jasperFile = getClass().getResourceAsStream(simulationCompiledTpl);
		return (JasperReport) JRLoader.loadObject(jasperFile);
	}

	private void exportAsPdf(JasperPrint jasperPrint, OutputStream pdfOutputStream) throws JRException {
		JRPdfExporter exporter = new JRPdfExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfOutputStream));

		SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);

		SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
		exportConfig.setMetadataAuthor("sgma");
		exportConfig.setEncrypted(true);
		exportConfig.setAllowedPermissionsHint("PRINTING");

		exporter.setConfiguration(reportConfig);
		exporter.setConfiguration(exportConfig);

		exporter.exportReport();
	}

}
