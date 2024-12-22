package com.backend_potato.edubox_team2.global.config;

import com.backend_potato.edubox_team2.global.jwt.JwtFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.disable()) // CORS 설정 (필요에 따라 설정)
                .csrf(csrf -> csrf.disable())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)// CSRF 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger 관련 경로는 인증 없이 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui.html/**"
                        ).permitAll()
                        // 회원가입과 로그인 경로도 인증 없이 허용
                        .requestMatchers(
                                "/api/user/login",
                                "/api/user/signup",
                                "/api/user/verify-code",
                                "/api/user/send-verification",
                                "/api/user/verify-link",
                                "/api/user/oauth/**"
                        ).permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // JWT 필터 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(Customizer.withDefaults());;

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            config.setExposedHeaders(Collections.singletonList("Authorization"));
            config.setMaxAge(3600L);
            return config;
        };
    }
}
