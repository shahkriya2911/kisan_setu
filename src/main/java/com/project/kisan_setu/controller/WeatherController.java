package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ResponseDto.WeatherResponseDto;
import com.project.kisan_setu.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/weather")
@Tag(name = "Weather Management", description = "Endpoints for weather related resources")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{city}")
    @Operation(summary = "Get weather by city method", description = "Used by user to get weather details for a city")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weather fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public WeatherResponseDto getWeather(
            @Parameter(description = "City name request", required = true)
            @PathVariable String city){
        return weatherService.getWeather(city);
    }
}
