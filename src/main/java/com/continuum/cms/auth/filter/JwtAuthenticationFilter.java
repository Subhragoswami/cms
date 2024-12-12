package com.continuum.cms.auth.filter;

import com.continuum.cms.dao.UserLoginSessionDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private List<String> whitelistURLs;
    private final UserService userService;
    private final UserLoginSessionDao userLoginSessionDao;

    @Value("${whitelisted.endpoints}")
    public void setWhitelistURLs( List<String> whitelistURLs) {
        this.whitelistURLs = whitelistURLs;
    }
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        try {

            String correlationId = request.getHeader("X-Correlation-ID");
            if (StringUtils.isEmpty(correlationId)) {
                correlationId = UUID.randomUUID().toString();
            }
            ThreadContext.put("correlationId", correlationId);

            final String authHeader = request.getHeader("Authorization");
            String requestUri = request.getRequestURI();

            if (whiteListingCheck(request, response, filterChain, authHeader, requestUri)) return;

            mandatoryCheck(authHeader, requestUri);

            if (loginAPICall(request, response, filterChain, authHeader)) return;

            authentication(request, authHeader);

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
            ThreadContext.remove("correlationId");
        }
    }

    private void authentication(HttpServletRequest request, String authHeader) {
        final String jwt = authHeader.substring(7);
        final String userName = jwtService.extractUserName(jwt);
        if (StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<User> userDetails = userService.getUserByUserName(userName);
            userLoginSessionDao.getActiveLoginSessionByUserName(userDetails.get().getUsername())
                    .orElseThrow(() -> new JwtException("Token is invalid."));
            if (userDetails.isPresent() && jwtService.isTokenValid(jwt, userDetails.get())) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails.get().getUsername(), userDetails.get().getPassword(), userDetails.get().getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            } else {
                throw new JwtException("Token is invalid.");
            }
        }
    }

    private static boolean loginAPICall(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String authHeader) throws IOException, ServletException {
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private static void mandatoryCheck(String authHeader, String requestUri) {
        if ((StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) && !StringUtils.endsWith(requestUri, "/login")){
            throw new JwtException("Token is required.");
        }
    }

    private boolean whiteListingCheck(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String authHeader, String requestUri) throws IOException, ServletException {
        if ((StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer "))){
            String withoutContext = requestUri.replaceFirst(request.getContextPath(),"");
            for(String whitelistURL : whitelistURLs){
                 if(withoutContext.startsWith(whitelistURL)){
                     filterChain.doFilter(request, response);
                     return true;
                 }
            }
        }
        if ((StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) && request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }
}
