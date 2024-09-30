package main.repositories;

import main.models.CompanyPlatform;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CompanyPlatformRepository extends ReactiveMongoRepository<CompanyPlatform, String> {
    Mono<CompanyPlatform> findCompanyPlatformByIban(String iban);
}
