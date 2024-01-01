package ls.EmployeeWorkOrderManagment.mail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class MailFactoryUnitTest {

    @Mock
    JavaMailSender javaMailSender;

    @InjectMocks
    MailFactory mailFactory;

    @Test
    void shouldReturnConfiguredMailMessage() {
        //given
        String address = "test@test.com";
        String subject = "Test";
        String content = "Unit testing method";

        //when
        SimpleMailMessage message = mailFactory.prepareEmailMessage(address, subject, content);

        //then
        Assertions.assertNotNull(message);
        Assertions.assertEquals(subject, message.getSubject());
        Assertions.assertEquals(content, message.getText());
        Assertions.assertEquals(address, message.getTo()[0]);
    }
}
