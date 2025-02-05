package main.services;

import main.models.SecretEntity;
import main.repositories.CompanyPlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyPlatformService {
    private final CompanyPlatformRepository companyPlatformRepository;

    @Autowired
    public CompanyPlatformService(CompanyPlatformRepository companyPlatformRepository) {
        this.companyPlatformRepository = companyPlatformRepository;
    }

    public SecretEntity findByIban(String iban) {
        return companyPlatformRepository.findCompanyPlatformByIban(iban);
    }
}
