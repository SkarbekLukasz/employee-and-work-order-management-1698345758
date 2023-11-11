package ls.EmployeeWorkOrderManagment.event.listener;

import ls.EmployeeWorkOrderManagment.event.OnPasswordResetEvent;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final UserService userService;

    public PasswordResetListener(JavaMailSender mailSender, UserService userService, Environment environment) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.environment = environment;
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
        String confirmationUrl = environment.getProperty("application.url") + event.getAppUrl() + "/resetPassword?token=" + token;
        String emailMessage = "Click the link below to reset your account password:";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(userEmailAddress);
        email.setSubject(emailSubject);
        email.setText(emailMessage + "\r\n" + confirmationUrl);
        mailSender.send(email);
    }
}
