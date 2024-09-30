package main.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetFlightDataStarter {
    @Autowired
    private GetFlightData getFlightData;

    @PostConstruct
    public void startTask(){
//        getFlightData.getData();
    }
}
