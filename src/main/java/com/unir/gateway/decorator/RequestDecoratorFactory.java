package com.unir.gateway.decorator;

import static jakarta.ws.rs.HttpMethod.DELETE;
import static jakarta.ws.rs.HttpMethod.GET;
import static jakarta.ws.rs.HttpMethod.PATCH;
import static jakarta.ws.rs.HttpMethod.POST;
import static jakarta.ws.rs.HttpMethod.PUT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unir.gateway.model.GatewayRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for creating decorators for the GatewayRequest object.
 * It uses the ObjectMapper to convert the raw request body into a GatewayRequest object.
 * Depending on the HTTP method of the request, it creates a different decorator.
 */
@Component
@RequiredArgsConstructor
public class RequestDecoratorFactory {

    private final ObjectMapper objectMapper;


        public ServerHttpRequestDecorator getDecorator(GatewayRequest request) {
            return switch (request.getTargetMethod().name().toUpperCase()) {
                case GET -> new GetRequestDecorator(request);//No se envía objectMapper porque GET no debería tener body
                case POST -> new PostRequestDecorator(request, objectMapper);
                case PUT -> new PutRequestDecorator(request, objectMapper);
                case PATCH -> new PatchRequestDecorator(request, objectMapper);
                case DELETE -> new DeleteRequestDecorator(request);//No se envía objectMapper porque DELETE no debería tener body
                default -> throw new IllegalArgumentException("Invalid http method");
            };
        }
}
