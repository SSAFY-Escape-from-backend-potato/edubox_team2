package com.backend_potato.edubox_team2.global.jwt;
import com.backend_potato.edubox_team2.domain.users.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private final SecretKey SECRET_KEY;
    private final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    @Value("${spring.auth.jwt.accessExpireMs}")
    private Long accessExpireTime;
    @Value("${spring.auth.jwt.refreshExpireMs}")
    private Long refreshExpireTime;


    public JwtTokenUtil(@Value("${spring.auth.jwt.secretKey}") String secretKey) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user, String type, Long expireTime) {
        return Jwts.builder()
                .claim("type", type)
                .claim("email",user.getEmail())
                .claim("nickname", user.getNickname())
                .claim("role",user.getRole())
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, "access", accessExpireTime);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, "refresh", refreshExpireTime);
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }

    public String getUserNicknameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("nickname", String.class);
    }

    public String getUserRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    public boolean isValidToken(String token){
        Claims claims;
        try{
            claims = parseClaims(token);
            //토큰 만료가 현재 시간 이전이라면 유효한 토큰
            return !claims.getExpiration().before(new Date());
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

}