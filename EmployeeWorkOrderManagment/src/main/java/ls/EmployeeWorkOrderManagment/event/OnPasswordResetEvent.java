package ls.EmployeeWorkOrderManagment.event;

import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import org.springframework.context.ApplicationEvent;

public class OnPasswordResetEvent extends ApplicationEvent {
    private String email;
    private User userAccount;
    private String appUrl;
    public OnPasswordResetEvent(User userAccount, String email, String contextPath) {
        super(userAccount);
        this.userAccount = userAccount;
        this.email = email;
        this.appUrl = contextPath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(User userAccount) {
        this.userAccount = userAccount;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }
}
