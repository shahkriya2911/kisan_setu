package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ResponseDto.WeatherResponseDto;
import com.project.kisan_setu.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{city}")
    public WeatherResponseDto getWeather(@PathVariable String city){
        return weatherService.getWeather(city);
    }
}
