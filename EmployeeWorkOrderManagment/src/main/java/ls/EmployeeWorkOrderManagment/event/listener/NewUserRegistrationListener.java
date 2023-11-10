package ls.EmployeeWorkOrderManagment.event.listener;

import ls.EmployeeWorkOrderManagment.event.OnRegisterCompleteEvent;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NewUserRegistrationListener implements ApplicationListener<OnRegisterCompleteEvent> {
    @Value("${application.url}")
    private String APP_URL;
    private final UserService userService;
    private final JavaMailSender mailSender;

    public NewUserRegistrationListener(UserService userService, JavaMailSender mailSender) {
        this.userService = userService;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(OnRegisterCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegisterCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createNewVerificationToken(user, token);

        String userEmailAddress = user.getEmail();
        String emailSubject = "New account registration confirmation!";
        String confirmationUrl = APP_URL + event.getAppUrl() + "/registerConfirm?token=" + token;
        String emailMessage = "Your account has been created. Click the link below to confirm your registration.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(userEmailAddress);
        email.setSubject(emailSubject);
        email.setText(emailMessage + "\r\n" + confirmationUrl);
        mailSender.send(email);
    }
}
