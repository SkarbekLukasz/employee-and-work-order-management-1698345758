package ls.EmployeeWorkOrderManagment.event.listener;

import ls.EmployeeWorkOrderManagment.event.OnRegisterCompleteEvent;
import ls.EmployeeWorkOrderManagment.mail.MailFactory;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.service.TokenService;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NewUserRegistrationListener implements ApplicationListener<OnRegisterCompleteEvent> {
    private final Environment environment;
    private final TokenService tokenService;
    private final JavaMailSender mailSender;
    private final MailFactory mailFactory;
    private final String EMAIL_SUBJECT = "New account registration confirmation!";
    private final String EMAIL_MESSAGE = "Your account has been created. Click the link below to confirm your registration.";

    public NewUserRegistrationListener(JavaMailSender mailSender, Environment environment, TokenService tokenService, MailFactory mailFactory) {
        this.mailSender = mailSender;
        this.environment = environment;
        this.tokenService = tokenService;
        this.mailFactory = mailFactory;
    }

    @Override
    public void onApplicationEvent(OnRegisterCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegisterCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        tokenService.createNewVerificationToken(user, token);

        String userEmailAddress = user.getEmail();
        String confirmationUrl = environment.getProperty("application.url") + event.getAppUrl() + "/registerConfirm?token=" + token;
        String emailContent = EMAIL_MESSAGE + "\r\n" + confirmationUrl;

        SimpleMailMessage email = mailFactory.prepareEmailMessage(userEmailAddress, EMAIL_SUBJECT, emailContent);
        mailSender.send(email);
    }
}
