package rmsbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.JSONRespond;
import rmsbackend.security.jwt.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/logout",
                                "/api/v1/items",
                                "/api/v1/categories",
                                "/api/v1/tables/scan/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/forget-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/reset-password").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(objectMapper.writeValueAsString(
                                    JSONRespond.error(StatusCode.UNAUTHORIZED, "Authentication is required.")
                            ));
                        })
                        .accessDeniedHandler((request, response, exception) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(objectMapper.writeValueAsString(
                                    JSONRespond.error(StatusCode.FORBIDDEN, "Access is denied.")
                            ));
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
