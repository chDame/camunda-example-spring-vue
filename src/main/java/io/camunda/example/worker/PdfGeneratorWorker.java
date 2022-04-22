package io.camunda.example.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.xdocreport.core.XDocReportException;
import io.camunda.example.docxpdf.DocxPdfUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@Component
@EnableZeebeClient
public class PdfGeneratorWorker {

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
	
	@ZeebeWorker(type = "generateMeetingReport", autoComplete = true)
	public Map<String, String> generateMeetingReport(final ActivatedJob job) throws Exception {
	    Map<String, Object> var = job.getVariablesAsMap();
	    Map<String, Object> templateVar = new HashMap<>();
	   	String today = sdf.format(new Date());
	    templateVar.put("project", var.get("meetingType")+" Meeting report");
	    templateVar.put("date", today);
	    templateVar.put("attendees", var.get("neededParticipants"));
	    templateVar.put("notes", var.get("notes"));
	    String target = var.get("meetingType")+"MeetingReport_"+today+".pdf";
	    DocxPdfUtils.generatePdf("Meeting report.docx", target, templateVar);
			
	    return Map.of("generatedReport", target);
	}
	
}
