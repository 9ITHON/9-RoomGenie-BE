package team9.demo.util.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import team9.demo.error.AuthorizationException;
import team9.demo.error.ErrorCode;
import team9.demo.model.user.UserId;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final RequestMappingHandlerMapping handlerMapping;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, RequestMappingHandlerMapping handlerMapping) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = resolveToken(request);
            jwtTokenUtil.validateToken(token);
            UserId userId = jwtTokenUtil.getUserIdFromToken(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.setAttribute("userId", userId);
        } catch (Exception e) {
            request.setAttribute("Exception", e);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/create/send") ||
                path.startsWith("/api/auth/create/verify") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/api/auth/reset/send") ||
                path.startsWith("/api/auth/reset/verify") ||
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/logout") ||
                path.startsWith("/ws-stomp") ||
                path.startsWith("/docs");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return jwtTokenUtil.cleanedToken(bearerToken);
        }
        throw new AuthorizationException(ErrorCode.INVALID_TOKEN);
    }
}
