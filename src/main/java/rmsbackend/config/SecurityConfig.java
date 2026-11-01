package rmsbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String MANAGER = "MANAGER";
    private static final String WAITER = "WAITER";
    private static final String KITCHEN = "KITCHEN";
    private static final String CASHIER = "CASHIER";

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
                // Rules are evaluated top-down; the first match wins, so keep specific rules first.
                .authorizeHttpRequests(auth -> auth
                        // ---- public ----
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/logout",
                                "/api/v1/tables/scan/**"
                        ).permitAll()
                        // customer-facing QR ordering: browse menu and place an order
                        .requestMatchers(HttpMethod.GET, "/api/v1/items", "/api/v1/categories").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/forget-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/reset-password").permitAll()

                        // ---- any authenticated user ----
                        .requestMatchers("/api/v1/auth/**").authenticated()
                        .requestMatchers("/api/v1/users/me", "/api/v1/users/me/**").authenticated()
                        // self-or-admin is enforced by @PreAuthorize on the controller method
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/*/change-password").authenticated()

                        // ---- ADMIN only ----
                        .requestMatchers("/api/v1/users/**", "/api/v1/roles/**").hasRole(ADMIN)

                        // ---- orders ----
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/**")
                                .hasAnyRole(ADMIN, MANAGER, WAITER, KITCHEN, CASHIER)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/orders/**")
                                .hasAnyRole(ADMIN, MANAGER, WAITER, KITCHEN, CASHIER)
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders/**")
                                .hasAnyRole(ADMIN, MANAGER, WAITER, CASHIER)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**")
                                .hasAnyRole(ADMIN, MANAGER, WAITER)

                        // ---- menu and tables: staff can read, ADMIN/MANAGER can change ----
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/items/**", "/api/v1/categories/**", "/api/v1/ingredients/**",
                                "/api/v1/item-details/**", "/api/v1/tables/**")
                                .hasAnyRole(ADMIN, MANAGER, WAITER, KITCHEN, CASHIER)
                        .requestMatchers(
                                "/api/v1/items/**", "/api/v1/categories/**", "/api/v1/ingredients/**",
                                "/api/v1/item-details/**", "/api/v1/tables/**")
                                .hasAnyRole(ADMIN, MANAGER)

                        // ---- customers ----
                        .requestMatchers("/api/customers/**").hasAnyRole(ADMIN, MANAGER, WAITER, CASHIER)

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
