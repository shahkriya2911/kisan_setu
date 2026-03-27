package com.project.kisan_setu.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WeatherApiClient {

    // used for calling external apis
    private final RestTemplate restTemplate;

    public WeatherApiClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.base-url}")
    private String baseUrl;

    public String getWeather(String city) {
        String url = baseUrl+"/data/2.5/weather?q="+city+"&appid="+apiKey+"&units=metric";
        return restTemplate.getForObject(url,String.class);
    }
}
