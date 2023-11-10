package ls.EmployeeWorkOrderManagment.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {
    @GetMapping("/")
    public String getHomepage() {
        return "index";
    }

    @GetMapping("/error")
    public String getErrorPage(Model model) {
        return "message";
    }
}
