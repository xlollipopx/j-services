package com.apigateway.oauth.repository;

import com.apigateway.oauth.model.AuthRequestHolder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
//TODO replace with real DB repository
public class AuthRequestHolderRepository {

    private ConcurrentHashMap<UUID, AuthRequestHolder> holder
            = new ConcurrentHashMap<>();

    public Mono<AuthRequestHolder> findByid(UUID id) {
       return Mono.justOrEmpty(holder.get(id));
    }

    public Mono<AuthRequestHolder> save(AuthRequestHolder authRequestHolder)  {
      return Mono.justOrEmpty(holder.put(authRequestHolder.getUuid(), authRequestHolder));
    }

    public Mono<Void> remove(UUID id) {
        holder.remove(id);
        return Mono.empty();
    }






}