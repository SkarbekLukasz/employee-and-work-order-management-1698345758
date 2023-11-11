package ls.EmployeeWorkOrderManagment.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Value("${REMEMBER_ME")
    private String rememberKey;

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public SecurityConfiguration(UserDetailsService userDetailsService, CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.sessionManagement((session) -> session.invalidSessionUrl("/invalid-session.html"));
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll())
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(false)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/").permitAll())
                .rememberMe(remember -> remember
                        .rememberMeParameter("remember-me")
                        .key(rememberKey));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
