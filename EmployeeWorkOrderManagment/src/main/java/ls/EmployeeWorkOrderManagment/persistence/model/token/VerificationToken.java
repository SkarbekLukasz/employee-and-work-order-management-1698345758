package ls.EmployeeWorkOrderManagment.persistence.model.token;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken implements Token {

    private static final int EXPIRATION_TIME = 60 * 24;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "token_val", nullable = false)
    private String token;
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    public VerificationToken(String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION_TIME);
    }

    @Override
    public Date calculateExpiryDate(int expiryTimeInMinutes) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Timestamp(cal.getTime().getTime()));
            cal.add(Calendar.MINUTE, expiryTimeInMinutes);
            return new Date(cal.getTime().getTime());
    }


}
