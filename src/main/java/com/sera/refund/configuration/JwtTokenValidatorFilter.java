package com.sera.refund.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sera.refund.common.TokenProvider;
import com.sera.refund.common.response.CommonResponse;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtTokenValidatorFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;


    // 인증이 필요없는 요청에 대해서는 필터를 거치지 않도록 설정
    private static final List<String> AUTH_WHITELIST = List.of(
            "/h2-console",
            "/h2-console/**",
            "/szs/signup",
            "/szs/login"
    );


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();
        return AUTH_WHITELIST.stream().anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token == null) {
            setErrorResponse(response, ErrorCode.TOKEN_NOT_FOUND);
            return;
        }
        try {
            tokenProvider.validateToken(token);
        } catch (BaseException e) {
            setErrorResponse(response, e.getErrorCode());
            return;
        }
        String userId = tokenProvider.extractUserId(token);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode tokenNotFound) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(CommonResponse.fail(tokenNotFound)));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (!StringUtils.hasText(bearerToken)) return null;
        if (!bearerToken.startsWith("Bearer ")) return null;
        return bearerToken.substring(7);
    }
}
