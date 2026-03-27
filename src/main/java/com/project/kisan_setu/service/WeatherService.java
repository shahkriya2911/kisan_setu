package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ResponseDto.WeatherResponseDto;

public interface WeatherService {
    WeatherResponseDto getWeather(String city);
}
