package ls.EmployeeWorkOrderManagment.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class CloudinaryConfig {

    private final Environment environment;

    public CloudinaryConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public Cloudinary cloudinary() {
        Cloudinary cloudinary = new Cloudinary(environment.getProperty("cloudinary_url"));
        cloudinary.config.secure = true;
        return cloudinary;
    }
}
