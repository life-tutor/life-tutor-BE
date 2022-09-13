package com.example.lifetutor.config.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.lifetutor.config.security.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;

@Component
public final class JwtTokenUtils {

    private static final int SEC = 1;
    private static final int MINUTE = 60 * SEC;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    // JWT 토큰의 유효기간: 3일 (단위: seconds)

    // JWT 토큰의 유효기간: 3일 (단위: milliseconds)
    private static final int JWT_TOKEN_VALID_SEC = MINUTE * 1000;
    private static final long REFRESH_TOKEN_VALID_TIME = DAY * 7 * 1000;   // 1주
    public static final String CLAIM_EXPIRED_DATE = "EXPIRED_DATE";
    public static final String CLAIM_USER_NAME = "USER_NAME";
    public static String JWT_SECRET;

    @Value("${jwt.secret}")
    public void setJwtSecret(String jwtSecret) {
        JWT_SECRET = jwtSecret;
    }

    public static String generateJwtToken(String username) {
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer("sparta")
                    .withClaim(CLAIM_USER_NAME, username)
                     // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, new Date(System.currentTimeMillis() + JWT_TOKEN_VALID_SEC))
                    .sign(generateAlgorithm());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return token;
    }

    public static String generateRefreshToken() {
//        Claims claims = Jwts.claims();
//        claims.put("username", username);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME);
//        Key key = Keys.hmacShaKeyFor("jwt_refresh_!@!@#!@#!@FWQFWDFWFQWF".getBytes(StandardCharsets.UTF_8));

        return JWT.create()
                .withIssuer("sparta-refresh")
                // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                .withClaim(CLAIM_EXPIRED_DATE, new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_TIME))
                .sign(generateAlgorithm());

//                Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(expiration)
//                .signWith(key,SignatureAlgorithm.HS384)
//                .compact();
    }
    private static Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(JWT_SECRET);
    }
    public String resolveAccessToken(HttpServletRequest request) {
        return request.getHeader("ACCESS_TOKEN");
    }
    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader("REFRESH_TOKEN");
    }

    public Claims getClaimsFormToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(JWT_SECRET))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

//    public Claims getClaimsToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(DatatypeConverter.parseBase64Binary(REFRESH_TOKEN_SECRET))
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }

    public boolean isValidAccessToken(String token) {
        System.out.println("isValidToken is : " +token);
        try {
            Claims accessClaims = getClaimsFormToken(token);
            System.out.println("Access expireTime: " + accessClaims.getExpiration());
            System.out.println("Access username: " + accessClaims.get("username"));
            return true;
        } catch (ExpiredJwtException exception) {
            System.out.println("Token Expired UserName : " + exception.getClaims().get("username"));
            return false;
        } catch (JwtException exception) {
            System.out.println("Token Tampered");
            return false;
        } catch (NullPointerException exception) {
            System.out.println("Token is null");
            return false;
        }
    }
//    public boolean isValidRefreshToken(String token) {
//        try {
//            Claims accessClaims = getClaimsToken(token);
//            System.out.println("Access expireTime: " + accessClaims.getExpiration());
//            System.out.println("Access username: " + accessClaims.get("username"));
//            return true;
//        } catch (ExpiredJwtException exception) {
//            System.out.println("Token Expired UserName : " + exception.getClaims().get("username"));
//            return false;
//        } catch (JwtException exception) {
//            System.out.println("Token Tampered");
//            return false;
//        } catch (NullPointerException exception) {
//            System.out.println("Token is null");
//            return false;
//        }
//    }
}
