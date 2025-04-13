//package com.example.ReceiptServer.filter;
//
//import io.github.bucket4j.Bucket;
//import io.github.bucket4j.Bucket4j;
//import io.github.bucket4j.Refill;
//import io.github.bucket4j.Bandwidth;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.time.Duration;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.Map;
//
//@Component
//public class RateLimitingFilter extends OncePerRequestFilter {
//
//    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
//
//    private Bucket createNewBucket() {
//        // Allow 5 requests per minute per client
//        Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
//        return Bucket4j.builder().addLimit(limit).build();
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        if (request.getRequestURI().equals("/auth/refresh-access")) {
//            String clientIp = request.getRemoteAddr();
//            Bucket bucket = buckets.computeIfAbsent(clientIp, ip -> createNewBucket());
//            if (bucket.tryConsume(1)) {
//                filterChain.doFilter(request, response);
//            } else {
//                response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
//                response.getWriter().write("Too many requests; please try again later.");
//                return;
//            }
//        } else {
//            filterChain.doFilter(request, response);
//        }
//    }
//}