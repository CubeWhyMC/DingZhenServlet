package fuck.manthe.nmsl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/user/redeem").permitAll()
                        .requestMatchers("/user/forgetPassword").anonymous()
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/dashboard").authenticated()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/webui**", "/webui/**").authenticated()
                        .requestMatchers("/api/v2/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin((form) -> form
                        .loginPage("/user/login")
                        .permitAll()
                )
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .csrf(AbstractHttpConfigurer::disable)
                .logout(conf -> conf
                        .logoutUrl("/user/logout")
                        .permitAll());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
