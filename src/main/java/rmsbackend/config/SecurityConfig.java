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
import rmsbackend.enums.PermissionName;
import rmsbackend.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Authority names are the PermissionName codes seeded by DataSeedConfig.
    private static final String ORDER_VIEW = PermissionName.ORDER_VIEW.name();
    private static final String ORDER_CREATE = PermissionName.ORDER_CREATE.name();
    private static final String ORDER_UPDATE = PermissionName.ORDER_UPDATE.name();
    private static final String ORDER_DELETE = PermissionName.ORDER_DELETE.name();
    private static final String MENU_VIEW = PermissionName.MENU_VIEW.name();
    private static final String MENU_MANAGE = PermissionName.MENU_MANAGE.name();
    private static final String TABLE_VIEW = PermissionName.TABLE_VIEW.name();
    private static final String TABLE_MANAGE = PermissionName.TABLE_MANAGE.name();
    private static final String CUSTOMER_VIEW = PermissionName.CUSTOMER_VIEW.name();
    private static final String USER_MANAGE = PermissionName.USER_MANAGE.name();
    private static final String ROLE_MANAGE = PermissionName.ROLE_MANAGE.name();

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

                        // ---- user and role administration ----
                        .requestMatchers("/api/v1/users/**").hasAuthority(USER_MANAGE)
                        .requestMatchers("/api/v1/roles/**", "/api/v1/permissions/**").hasAuthority(ROLE_MANAGE)

                        // ---- orders ----
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAuthority(ORDER_VIEW)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/orders/**").hasAuthority(ORDER_UPDATE)
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders/**").hasAuthority(ORDER_CREATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasAuthority(ORDER_DELETE)

                        // ---- menu and tables: *_VIEW can read, *_MANAGE can change ----
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/items/**", "/api/v1/categories/**", "/api/v1/ingredients/**",
                                "/api/v1/item-details/**")
                                .hasAuthority(MENU_VIEW)
                        .requestMatchers(
                                "/api/v1/items/**", "/api/v1/categories/**", "/api/v1/ingredients/**",
                                "/api/v1/item-details/**")
                                .hasAuthority(MENU_MANAGE)
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/**").hasAuthority(TABLE_VIEW)
                        .requestMatchers("/api/v1/tables/**").hasAuthority(TABLE_MANAGE)

                        // ---- customers ----
                        .requestMatchers("/api/customers/**").hasAuthority(CUSTOMER_VIEW)

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
