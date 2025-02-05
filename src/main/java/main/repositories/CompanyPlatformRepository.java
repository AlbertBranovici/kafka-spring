package main.repositories;

import main.models.SecretEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyPlatformRepository extends JpaRepository<SecretEntity, String> {
    SecretEntity findCompanyPlatformByIban(String iban);
}
