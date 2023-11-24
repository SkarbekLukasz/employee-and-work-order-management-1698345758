package ls.EmployeeWorkOrderManagment.event.listener;

import ls.EmployeeWorkOrderManagment.event.OnPasswordResetEvent;
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
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {
    private final Environment environment;
    private final JavaMailSender mailSender;
    private final TokenService tokenService;
    private final MailFactory mailFactory;
    private final String EMAIL_MESSAGE = "Click the link below to reset your account password:";
    private final String EMAIL_SUBJECT = "Password reset!";

    public PasswordResetListener(JavaMailSender mailSender, Environment environment, TokenService tokenService, MailFactory mailFactory) {
        this.mailSender = mailSender;
        this.environment = environment;
        this.tokenService = tokenService;
        this.mailFactory = mailFactory;
    }

    @Override
    public void onApplicationEvent(OnPasswordResetEvent event) {
        this.resetPassword(event);
    }

    private void resetPassword(OnPasswordResetEvent event) {
        User user = event.getUserAccount();
        String token = UUID.randomUUID().toString();

        tokenService.createNewResetToken(user, token);

        String userEmailAddress = user.getEmail();
        String confirmationUrl = environment.getProperty("application.url") + event.getAppUrl() + "/resetPassword?token=" + token;
        String emailContent = EMAIL_MESSAGE + "\r\n" + confirmationUrl;

        SimpleMailMessage email = mailFactory.prepareEmailMessage(userEmailAddress, EMAIL_SUBJECT, emailContent);
        mailSender.send(email);
    }
}
