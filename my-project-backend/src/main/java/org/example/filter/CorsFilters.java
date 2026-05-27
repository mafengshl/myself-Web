package org.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.utils.Const;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(Const.ORDER_CORS)
public class CorsFilters extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.addCorsHeaders(request, response);
        
        // 处理 OPTIONS 请求（预检请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        chain.doFilter(request, response);
    }

    private void addCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        
        // 允许所有本地开发端口
        if (origin != null && (origin.startsWith("http://localhost") || origin.startsWith("http://127.0.0.1"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            // 生产环境域名白名单
            List<String> allowedOrigins = Arrays.asList(
                    "http://8.137.151.252",
                    "http://8.137.151.252:8080",
                    "http://47.108.194.31:8080",
                    "http://47.108.194.31",
                    "https://47.108.194.31:8080",
                    "https://47.108.194.31",
                    "http://www.godplace.icu",
                    "http://www.godplace.icu:8080",
                    "https://www.godplace.icu",
                    "https://www.godplace.icu:8080"
            );
            if (allowedOrigins.contains(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
            }
        }

        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}
