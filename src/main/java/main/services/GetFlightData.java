package main.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.models.dto.FlightDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GetFlightData {

    private static final Logger log = LoggerFactory.getLogger(GetFlightData.class.getName());

    private final WebClient client;
//    private final KafkaProducerService producer;
    private final FlightService flightService;

    public GetFlightData(ObjectMapper objectMapper, FlightService flightService) {
        this.client = WebClient.builder()
                .baseUrl("https://aviation-edge.com/v2/public/timetable")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .jackson2JsonDecoder(new org.springframework.http.codec.json.Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON)))
                .build();
        this.flightService = flightService;
//        this.producer = producer;
    }
//
    @Async
    public void getData() {
        for (int i=0;i<1;i++) {
            try {
                log.info("Getting changes");


                // Send GET request and extract response body as ApiResponse object
                Mono<List<FlightDTO>> response = client.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("iataCode", "OTP")
                                .queryParam("type", "departure")
                                .queryParam("key", "APIKEY")
                                .build())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<FlightDTO>>() {})
                        .onErrorResume(WebClientResponseException.class, e -> {
                            log.error("Error during API call", e);
                            return Mono.empty(); // Return an empty Mono on error
                        });

                // Subscribe to the Mono and process the response
                response.subscribe(flightList -> {
                    if (flightList != null && !flightList.isEmpty()) {
                       for(FlightDTO flight : flightList) {
//                           flightService.saveFlight(flight);
                           if(flight.getCodeshared() != null){
                               log.info("Saved flight {}", flight);
                               flightService.saveFlight(flight);
                           }
                           else{
                               log.info("skipped");
                           }

//                           log.info("Flight number: ", flight.getCodeshared().getFlight().getNumber());


//                           producer.sendMessage(flight);
                       }
                    } else {
                        log.warn("Invalid API response or query section is null.");
                    }
                });

//                Mono<String> rawResponse = client.get()
//                        .uri(uriBuilder -> uriBuilder
//                                .queryParam("action", "query")
//                                .queryParam("format", "json")
//                                .queryParam("list", "recentchanges")
//                                .queryParam("rcprop", "title|ids|sizes|flags|user")
//                                .build())
//                        .retrieve()
//                        .bodyToMono(String.class);
//
//                rawResponse.subscribe(json -> {
//                    log.info("Raw JSON response: " + json);
//                });

                // Sleep for 5 seconds before making the next request
                Thread.sleep(5000);

            } catch (InterruptedException e) {
                log.error("Background task interrupted", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("An error occurred while fetching data", e);
            }
        }

    }

}
