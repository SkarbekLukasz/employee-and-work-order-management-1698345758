package ls.EmployeeWorkOrderManagment.event.listener;

import ls.EmployeeWorkOrderManagment.event.OnPasswordResetEvent;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {
    @Value("${application.url}")
    private String APP_URL;

    private final JavaMailSender mailSender;
    private final UserService userService;

    public PasswordResetListener(JavaMailSender mailSender, UserService userService) {
        this.mailSender = mailSender;
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(OnPasswordResetEvent event) {
        this.resetPassword(event);
    }

    private void resetPassword(OnPasswordResetEvent event) {
        User user = event.getUserAccount();
        String token = UUID.randomUUID().toString();
        System.out.println(token);
        System.out.println(token);
        System.out.println(token);
        userService.createNewResetToken(user, token);

        String userEmailAddress = user.getEmail();
        String emailSubject = "Password reset!";
        String confirmationUrl = APP_URL + event.getAppUrl() + "/resetPassword?token=" + token;
        String emailMessage = "Click the link below to reset your account password:";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(userEmailAddress);
        email.setSubject(emailSubject);
        email.setText(emailMessage + "\r\n" + confirmationUrl);
        mailSender.send(email);
    }
}
