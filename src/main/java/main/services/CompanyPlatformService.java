package main.services;

import main.models.CompanyPlatform;
import main.repositories.CompanyPlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CompanyPlatformService {
    private final CompanyPlatformRepository companyPlatformRepository;

    @Autowired
    public CompanyPlatformService(CompanyPlatformRepository companyPlatformRepository) {
        this.companyPlatformRepository = companyPlatformRepository;
    }

    public Mono<CompanyPlatform> findByIban(String iban) {
        return companyPlatformRepository.findCompanyPlatformByIban(iban);
    }
}
