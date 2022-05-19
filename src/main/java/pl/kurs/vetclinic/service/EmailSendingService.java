package pl.kurs.vetclinic.service;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import pl.kurs.vetclinic.model.entity.Visit;
import pl.kurs.vetclinic.service.interfaces.EmailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static pl.kurs.vetclinic.config.AppConstants.DATE_TIME_FORMATTER;

@Service
public class EmailSendingService implements EmailSender {

    @Value("classpath:/mail-templates/baczek-logo.png")
    private Resource resourceFile;
    private final JavaMailSender javaMailSender;
    private final FreeMarkerConfigurer freemarkerConfigurer;

    public EmailSendingService(JavaMailSender javaMailSender, FreeMarkerConfigurer freemarkerConfigurer) {
        this.javaMailSender = javaMailSender;
        this.freemarkerConfigurer = freemarkerConfigurer;
    }

    @Async
    public void sendVisitBookedEmail(Visit visit) {
        String email = visit.getPatient().getEmail();
        String subject = "Powiadomienie o rezerwacji wizyty.";
        Map<String, Object> templateModel = getEmailContent(visit);
        try {
            Template freemarkerTemplate = freemarkerConfigurer.getConfiguration().getTemplate("visit-booked-email-template.ftl");
            String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, templateModel);
            sendHtmlMessage(email, subject, htmlBody);
        } catch (IOException | MessagingException | TemplateException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendVisitReminderEmail(Visit visit) {
        String email = visit.getPatient().getEmail();
        String subject = "Przypomnienie o wizycie.";
        Map<String, Object> templateModel = getEmailContent(visit);
        try {
            Template freemarkerTemplate = freemarkerConfigurer.getConfiguration().getTemplate("visit-reminder-email-template.ftl");
            String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, templateModel);
            sendHtmlMessage(email, subject, htmlBody);
        } catch (IOException | MessagingException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> getEmailContent(Visit visit) {
        LocalDateTime date = visit.getDate();
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("date", date.format(DATE_TIME_FORMATTER).substring(0, 10));
        templateModel.put("time", date.format(DATE_TIME_FORMATTER).substring(11, 16));
        templateModel.put("name", visit.getPatient().getFirstName() + " " + visit.getPatient().getLastName());
        templateModel.put("id", visit.getId());
        templateModel.put("spec", visit.getDoctor().getMedType().getDescription());
        templateModel.put("doctor", visit.getDoctor().getFirstName() + " " + visit.getDoctor().getLastName());
        return templateModel;
    }

    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.addInline("baczek-logo.png", resourceFile);
        javaMailSender.send(message);
    }

}
