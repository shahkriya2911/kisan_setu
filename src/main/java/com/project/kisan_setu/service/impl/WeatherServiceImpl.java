package com.project.kisan_setu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.kisan_setu.client.WeatherApiClient;
import com.project.kisan_setu.dto.ResponseDto.WeatherResponseDto;
import com.project.kisan_setu.service.WeatherService;
import org.springframework.stereotype.Service;

@Service
public class WeatherServiceImpl implements WeatherService {
    private final WeatherApiClient weatherApiClient;

//    JSON into java objects
    private final ObjectMapper objectMapper;

    public WeatherServiceImpl(WeatherApiClient weatherApiClient, ObjectMapper objectMapper) {
        this.weatherApiClient = weatherApiClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public WeatherResponseDto getWeather(String city) {
        try{
            String response = weatherApiClient.getWeather(city);
            JsonNode node = objectMapper.readTree(response);
            WeatherResponseDto dto = new WeatherResponseDto();
            dto.setCity(node.get("name").asText());
            dto.setHumidity(node.get("main").get("humidity").asInt());
            dto.setTemperature(node.get("main").get("temp").asDouble());
            dto.setDescription(node.get("weather").get(0).get("description").asText());
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Weather API failed");
        }

    }
}
