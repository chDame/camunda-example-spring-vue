package io.camunda.example.worker;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import io.camunda.example.service.MailContentBuilderService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;


@Component
@EnableZeebeClient
public class EmailWorker {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailContentBuilderService mailContentBuilderService;

    @Value("${email.from}")
    private String from;
    
    private void sendMail(MimeMessagePreparator messagePreparator, JobClient client, ActivatedJob job) {
    	mailSender.send(messagePreparator); 
    	client.newCompleteCommand(job.getKey())
        .send()
        .exceptionally((throwable -> {
        	throw new RuntimeException("Could not complete job", throwable);
        }));
    }
    
    @ZeebeWorker(type = "sendMeetingReport")
    public void sendMeetingReport(JobClient client, ActivatedJob job) throws Exception {
    	Map<String, Object> variables = job.getVariablesAsMap();
    	MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom(from);
            messageHelper.setTo((String) variables.get("mailUser"));
            messageHelper.setSubject((String) variables.get("mailSubject"));
            messageHelper.setText(mailContentBuilderService.buildMeetingReportMail(variables, Locale.ENGLISH), true);
            String reportName = (String) variables.get("generatedReport");
            messageHelper.addAttachment(reportName, new File(reportName));
    	};

        new Thread(() -> sendMail(messagePreparator, client, job)).start();
    }

}
