package com.samsepiol.library.application.mongo;

import com.samsepiol.library.application.mongo.models.Product;
import com.samsepiol.library.application.mongo.repo.ProductRepository;
import com.samsepiol.library.cache.Cache;
import com.samsepiol.library.mongo.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/library")
@RequiredArgsConstructor
public class LibraryController {
    private final ProductRepository productRepository;
    private final Repository repository;

    private final Cache<String, String> cache;

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productRepository.insert(product));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productRepository.getById(id));
    }

    @PutMapping("/products")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productRepository.update(product.getId(), product.getName()));
    }

    @GetMapping("/mongo/health")
    public ResponseEntity<Boolean> mongoHealth() {
        return ResponseEntity.ok(repository.isHealthy());
    }

    @GetMapping("/redis/health")
    public ResponseEntity<Boolean> redisHealth() {
        return ResponseEntity.ok(cache.isHealthy());
    }

}
