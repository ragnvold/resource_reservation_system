package com.nparmenov.reservation_system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceRepository resourceRepository = new ResourceRepository();

    @GetMapping("/{id}")
    public Mono<Resource> getResourceById(@PathVariable String id) {
        return resourceRepository.findResourceById(id);
    }
}