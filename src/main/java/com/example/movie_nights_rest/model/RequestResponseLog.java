package com.example.movie_nights_rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestResponseLog {
    @Id
    private String id;

    private HttpTrace.Principal principal;

    @Builder.Default
    private HashMap<String, Object> request = new HashMap<>();

    @Builder.Default
    private HashMap<String, Object> response = new HashMap<>();
    private HttpTrace.Session session;
    private Long timestamp;
    private Long timeTaken;

    private String requestMethod;
    private Map<String, List<String>> requestHeaders;
    private String requestRemoteAddress;
    private URI requestURI;

    public RequestResponseLog(HttpTrace httpTrace) {
        this.principal = httpTrace.getPrincipal();

        this.request = new HashMap<>();
        this.request.put("method", httpTrace.getRequest().getMethod());
        this.request.put("headers", httpTrace.getRequest().getHeaders());
        this.request.put("remoteAddress", httpTrace.getRequest().getRemoteAddress());
        this.request.put("uri", httpTrace.getRequest().getUri());

        this.response = new HashMap<>();
        this.response.put("headers", httpTrace.getResponse().getHeaders());
        this.response.put("status", httpTrace.getResponse().getStatus());

        this.session = httpTrace.getSession();

        this.timestamp = httpTrace.getTimestamp().toEpochMilli();
        this.timeTaken = httpTrace.getTimeTaken();
    }

    public HttpTrace toHttpTrace() {

        var request = new HttpTrace.Request(
                (String) this.request.get("method"),
                (URI) this.request.get("URI"),
                (Map<String, List<String>>) this.request.get("headers"),
                (String) this.request.get("remoteAddress"));

        var response = new HttpTrace.Response((int) this.response.get("status"),
                (Map<String, List<String>>) this.response.get("headers"));


        return new HttpTrace(request, response, Instant.ofEpochMilli(this.timestamp), principal, session, timeTaken);
    }
}
