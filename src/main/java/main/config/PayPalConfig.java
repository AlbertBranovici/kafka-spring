package main.config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PayPalConfig {
    private final Dotenv dotenv = Dotenv.load();

//    @Value("${paypal.client.id}")
    private String clientId = dotenv.get("PAYPAL_CLIENT_ID");

//    @Value("${paypal.client.secret}")
    private String clientSecret = dotenv.get("PAYPAL_CLIENT_SECRET");

//    @Value("${paypal.mode}")
    private String mode = dotenv.get("PAYPAL_MODE");

    @Bean
    public Map<String, String> paypalSdkConfig(){
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode",mode);
        return configMap;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential(){
        return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
        context.setConfigurationMap(paypalSdkConfig());
        return context;
    }

}
