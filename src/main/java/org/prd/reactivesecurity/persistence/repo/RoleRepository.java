package org.prd.reactivesecurity.persistence.repo;

import org.prd.reactivesecurity.persistence.entity.Role;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleRepository extends ReactiveCrudRepository<Role, String> {
    Mono<Role> findByName(String name);
}