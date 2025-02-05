package main.config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import io.github.cdimascio.dotenv.Dotenv;
import main.services.CompanyPlatformService;
import main.models.SecretEntity;
import main.services.utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PayPalConfig {
    private static final Logger log = LoggerFactory.getLogger(PayPalConfig.class);

    private final Dotenv dotenv = Dotenv.load();

    private String mode = dotenv.get("PAYPAL_MODE");

    @Autowired
    private CompanyPlatformService companyPlatformService;

    @Bean
    public Map<String, String> paypalSdkConfig(){
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", mode);
        return configMap;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential(){
        String clientId = getClientIdFromDatabase();
        String clientSecret = getClientSecretFromDatabase();
        return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
    }

    private String getClientIdFromDatabase() {
        SecretEntity secretEntity = companyPlatformService.findByIban("ROSKY1");
        if (secretEntity != null) {
            try {
                String decryptedClientId = utility.decrypt(secretEntity.getClientId());
                log.info("Decrypted PayPal Client ID: {}", decryptedClientId);
                return decryptedClientId;
            } catch (Exception e) {
                log.error("Error decrypting client ID", e);
                log.debug("Encrypted Client ID: {}", new String(secretEntity.getClientId()));
                throw new RuntimeException("Error decrypting client ID", e);
            }
        }
        throw new RuntimeException("Client ID not found for IBAN: ROSKY1");
    }

    private String getClientSecretFromDatabase() {
        SecretEntity secretEntity = companyPlatformService.findByIban("ROSKY1");
        if (secretEntity != null) {
            try {
                String decryptedClientSecret = utility.decrypt(secretEntity.getClientSecret());
                log.info("Decrypted PayPal Client Secret: {}", decryptedClientSecret);
                return decryptedClientSecret;
            } catch (Exception e) {
                log.error("Error decrypting client secret", e);
                log.debug("Encrypted Client Secret: {}", new String(secretEntity.getClientSecret()));
                throw new RuntimeException("Error decrypting client secret", e);
            }
        }
        throw new RuntimeException("Client Secret not found for IBAN: ROSKY1");
    }

    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
        context.setConfigurationMap(paypalSdkConfig());
        return context;
    }
}
