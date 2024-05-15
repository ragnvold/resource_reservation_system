package com.nparmenov.reservation_system;

import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class ResourceWebClient {
    WebClient client = WebClient.create("http://localhost:8080");

    Mono<Resource> resourceMono = client.get()
        .uri("/employees/{id}", "1")
        .retrieve()
        .bodyToMono(Resource.class);

    public ResourceWebClient() {
        resourceMono.subscribe(System.out::println);
    }
}
