package com.continuum.cms.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.continuum.cms.config.CMSServiceConfig;
import com.continuum.cms.entity.User;
import com.continuum.cms.util.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final String jwtSigningKey;
    private final CMSServiceConfig cmsServiceConfig;

    public JwtService(@Value("${token.signing.key}") String jwtSigningKey, CMSServiceConfig cmsServiceConfig) {
        this.jwtSigningKey = jwtSigningKey;
        this.cmsServiceConfig = cmsServiceConfig;
    }



    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public String extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", String.class));
    }


    public boolean isTokenValid(String token, User userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && isTokenExpired(token, userDetails);
    }

    public String generateToken(UserDetails userDetails, Date expiryTime, String scope) {
        return JWT.create().withSubject(userDetails.getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(expiryTime)
                .withClaim("roles", scope)
                .sign(Algorithm.HMAC256(jwtSigningKey));
    }

    public long extractIssuedAt(String token){
        return JWT.decode(token).getIssuedAt().getTime();
    }

    public String generateJWTToken(User user, Date expiryTime, String scope) {
        return JWT.create().withSubject(user.getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(expiryTime)
                .withClaim("roles", scope)
                .sign(Algorithm.HMAC256(jwtSigningKey));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private boolean isTokenExpired(String token, User userDetails) {
        return extractExpiration(token).equals(DateUtils.addHours(DateUtil.conversion(userDetails.getLastLogin()), cmsServiceConfig.getTokenExpiryTime()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        return new SecretKeySpec(jwtSigningKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }
}
