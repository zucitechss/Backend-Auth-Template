package com.auth.template.utils;

import com.auth.template.requestDTO.UserActivityEvent;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

@Component
public class ActivityLoggingFilter implements Filter {

    @Autowired
    private BlockingQueue<UserActivityEvent> activityQueue;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String username = req.getRemoteUser() != null ? req.getRemoteUser() : "SYSTEM";
        String ip = request.getRemoteAddr();
        String uri = req.getRequestURI();
        String method = req.getMethod();
        String referer = req.getHeader("Referer");

        String details = String.format("URI: %s, Referer: %s", uri, referer);

        UserActivityEvent event = new UserActivityEvent();
        event.setUsername(username);
        event.setAction(method);
        event.setDetails(details);
        event.setIpAddress(ip);
        event.setTimestamp(LocalDateTime.now());

        chain.doFilter(request, response);
        event.setStatusCode(res.getStatus());
        activityQueue.offer(event);
    }
}
