package com.apigateway.oauth;

import com.apigateway.oauth.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.apigateway.oauth.model.Person;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;


    public Mono<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public Mono<Person> findByUsername(String username) {
        return personRepository.findByUsername(username);
    }

    public Mono<Person> save(Person person) {
        return personRepository.save(person);
    }

    public Mono<Void> remove(Long id) {
        Mono<Person> p = personRepository.findByPersonId(id);
        return p.map(personRepository::delete).then();
    }
}
