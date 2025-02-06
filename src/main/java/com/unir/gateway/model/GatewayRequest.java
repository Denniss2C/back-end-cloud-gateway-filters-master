package com.unir.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.server.ServerWebExchange;

/**
 * This class represents a request that is being processed by the gateway.
 * It contains information about the target HTTP method, query parameters, body, exchange, and headers of the request.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GatewayRequest {

    private HttpMethod targetMethod;

    private LinkedMultiValueMap<String, String> queryParams;

    private Object body;

    @JsonIgnore
    private ServerWebExchange exchange;

    @JsonIgnore
    private HttpHeaders headers;
}