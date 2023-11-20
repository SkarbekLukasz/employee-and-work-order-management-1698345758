package ls.EmployeeWorkOrderManagment.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class MailFactory {

    public SimpleMailMessage prepareEmailMessage(String toAddress, String subject, String content) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toAddress);
        email.setSubject(subject);
        email.setText(content);

        return email;
    }
}
