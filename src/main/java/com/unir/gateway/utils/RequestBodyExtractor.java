package com.unir.gateway.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unir.gateway.model.GatewayRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.util.Strings;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is responsible for extracting the body of the request and converting it into a GatewayRequest object.
 * It uses the ObjectMapper to convert the raw request body into a GatewayRequest object.
 * It also sets the headers of the request, removing the Content-Length header and setting the Transfer-Encoding header to "chunked".
 */
@Component
@RequiredArgsConstructor
public class RequestBodyExtractor {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public GatewayRequest getRequest(ServerWebExchange exchange, DataBuffer buffer) {
        DataBufferUtils.retain(buffer);
        Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(buffer.split(buffer.readableByteCount())));
        String rawRequest = getRawRequest(cachedFlux);
        DataBufferUtils.release(buffer);
        GatewayRequest request = objectMapper.readValue(rawRequest, GatewayRequest.class);
        request.setExchange(exchange);

        // Set headers -- Needed: https://github.com/spring-cloud/spring-cloud-gateway/issues/894
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        headers.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
        request.setHeaders(headers);
        return request;
    }

    private String getRawRequest(Flux<DataBuffer> body) {
        AtomicReference<String> rawRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            rawRef.set(Strings.fromUTF8ByteArray(bytes));
        });
        return rawRef.get();
    }
}