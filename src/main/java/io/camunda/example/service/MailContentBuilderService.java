package io.camunda.example.service;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailContentBuilderService {

    @Autowired
    private TemplateEngine templateEngine;

    public String buildMeetingReportMail(Map<String, Object> variables, Locale locale) {
        Context context = new Context();
        for(Map.Entry<String, Object> entry : variables.entrySet()) {
        	context.setVariable(entry.getKey(), entry.getValue());
        }
        
        return templateEngine.process(variables.get("mailTemplate")+"-" + locale.getLanguage(), context);
    }

}