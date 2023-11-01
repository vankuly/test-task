package com.nortal.testtask.filter;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class HeaderFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String acceptHeader = exchange.getRequest().getHeaders().getFirst("Accept");
        if (acceptHeader == null || !acceptHeader.equals("application/json")) {
            ResponseStatusException errorResponse = new ResponseStatusException(
                    HttpStatusCode.valueOf(406), "Accept header must be 'application/json'"
            );
            return Mono.error(errorResponse.fillInStackTrace());
        }
        return chain.filter(exchange);
    }
}