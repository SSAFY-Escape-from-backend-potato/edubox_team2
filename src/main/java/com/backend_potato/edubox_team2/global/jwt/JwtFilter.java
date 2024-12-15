package com.backend_potato.edubox_team2.global.jwt;

import com.backend_potato.edubox_team2.domain.users.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private JwtTokenUtil jwtTokenUtil;
    private final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    public JwtFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String accessToken = resolveToken(request);

        if(accessToken != null && jwtTokenUtil.isValidToken(accessToken)){
            String userEmail = jwtTokenUtil.getUserEmailFromToken(accessToken);
            String nickname = jwtTokenUtil.getUserNicknameFromToken(accessToken);
            String role = jwtTokenUtil.getUserRoleFromToken(accessToken);

            User user = User.builder()
                    .email(userEmail)
                    .nickname(nickname)
                    .build();


            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, null, AuthorityUtils.createAuthorityList(role)
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }else{
            logger.info("유효하지 않은 access 토큰입니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("access-token");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
