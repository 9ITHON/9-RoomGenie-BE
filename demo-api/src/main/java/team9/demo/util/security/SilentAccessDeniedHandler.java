package team9.demo.util.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class SilentAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.warn("Access denied on {}: {}", request.getRequestURI(), accessDeniedException.getMessage());

        if (!response.isCommitted()) {
            response.setStatus(HttpServletResponse.SC_OK); // ❗ 일부 API 클라이언트를 위해 200 반환
            response.getWriter().write("");
            response.getWriter().flush();
        }
    }
}

