package com.apigateway.oauth.repository;

import com.apigateway.oauth.model.Person;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PersonRepository extends ReactiveCrudRepository<Person, Long> {

    public Mono<Person> findByPersonId(Long id);

    public Mono<Person> findByUsername(String username);

}
