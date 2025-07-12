package team9.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import team9.demo.util.security.JwtAuthenticationEntryPoint;
import team9.demo.util.security.JwtAuthenticationFilter;
import team9.demo.util.security.SilentAccessDeniedHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint entryPoint; // JwtAuthenticationEntryPoint
    private final SilentAccessDeniedHandler silentAccessDeniedHandler; // SilentAccessDeniedHandler

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/create/send",
                                "/api/auth/create/verify",
                                "/api/auth/reset/send",
                                "/api/auth/refresh",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/auth/reset/verify",
                                "/docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(silentAccessDeniedHandler)
                )
                .csrf(csrf -> csrf.disable())     // ✅ 새 방식
                .cors(cors -> {
                });                // ✅ 새 방식 (기본 설정 사용 시)

        return http.build();
    }
}
