package main.models;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "app")
public class CompanyPlatform {
    @Id
    private String id;
    private String iban;
    private String clientId;
    private String ClientSecret;

    public CompanyPlatform() {
    }

    public CompanyPlatform(String clientSecret, String clientId, String iban, String id) {
        this.ClientSecret = clientSecret;
        this.clientId = clientId;
        this.iban = iban;
        this.id = id;
    }

    public String getClientSecret() {
        return ClientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.ClientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
