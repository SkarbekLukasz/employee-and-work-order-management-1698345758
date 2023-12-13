package ls.EmployeeWorkOrderManagment.service;

import jakarta.transaction.Transactional;
import ls.EmployeeWorkOrderManagment.persistence.dao.ResetTokenRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.VerificationTokenRepository;
import ls.EmployeeWorkOrderManagment.persistence.model.token.ResetToken;
import ls.EmployeeWorkOrderManagment.persistence.model.token.Token;
import ls.EmployeeWorkOrderManagment.persistence.model.token.VerificationToken;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.web.error.InvalidTokenException;
import ls.EmployeeWorkOrderManagment.web.error.TokenExpiredException;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
public class TokenService {

    private final ResetTokenRepository resetTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;


    public TokenService(ResetTokenRepository resetTokenRepository, VerificationTokenRepository verificationTokenRepository) {
        this.resetTokenRepository = resetTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    public void createNewVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken confirmUserRegistration(String token) throws InvalidTokenException, TokenExpiredException {
        Optional<VerificationToken> fetchedToken = verificationTokenRepository.findByToken(token);
        if(fetchedToken.isEmpty()) throw new InvalidTokenException("Provided token is invalid.");

        VerificationToken verificationToken = fetchedToken.get();
        long currentTime = Calendar.getInstance().getTime().getTime();
        if(isTokenExpired(verificationToken, currentTime)) throw new TokenExpiredException("Provided token has expired.");

        return verificationToken;
    }

    @Transactional
    public void createNewResetToken(User user, String token) {
        ResetToken resetToken = new ResetToken(token);
        resetToken.setUser(user);
        resetTokenRepository.save(resetToken);
    }

    public ResetToken confirmResetToken(String token) {
        Optional<ResetToken> fetchedToken = resetTokenRepository.findByToken(token);
        if(fetchedToken.isEmpty()) throw new InvalidTokenException("Provided token is invalid");

        ResetToken resetToken = fetchedToken.get();
        long currentTime = Calendar.getInstance().getTime().getTime();
        if(isTokenExpired(resetToken, currentTime)) throw new TokenExpiredException("Provided token has expired.");

        return resetToken;
    }

    private boolean isTokenExpired(Token token, long currentTime) {
        return token.getExpiryDate().getTime() - currentTime <= 0;
    }


}
