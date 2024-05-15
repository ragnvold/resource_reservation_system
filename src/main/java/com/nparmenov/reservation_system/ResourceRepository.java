package com.nparmenov.reservation_system;

import reactor.core.publisher.Mono;

public class ResourceRepository {
    public ResourceRepository() {}
    
    public Mono<Resource> findResourceById(String id) {
        Resource testRes = new Resource("1", "test_name");
        return Mono.just(testRes);
    }
}