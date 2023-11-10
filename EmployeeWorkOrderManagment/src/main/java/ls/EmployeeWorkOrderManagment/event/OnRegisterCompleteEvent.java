package ls.EmployeeWorkOrderManagment.event;

import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import org.springframework.context.ApplicationEvent;

public class OnRegisterCompleteEvent extends ApplicationEvent {

    private String appUrl;
    private User user;
    public OnRegisterCompleteEvent(User user, String contextPath) {
        super(user);
        this.user = user;
        this.appUrl = contextPath;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
