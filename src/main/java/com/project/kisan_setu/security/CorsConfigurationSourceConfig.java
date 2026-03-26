//package com.project.kisan_setu.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.cors.CorsConfigurationSource;
//
//import java.util.List;
//
//@Configuration
//public class CorsConfigurationSourceConfig {
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        configuration.setAllowedOriginPatterns(List.of(
//                "http://localhost:3000",
//                "https://*.ngrok-free.dev"
//        ));
//
//        configuration.setAllowedMethods(List.of(
//                "GET", "POST", "PUT", "DELETE", "OPTIONS"
//        ));
//
//        configuration.setAllowedHeaders(List.of("*"));
//
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source =
//                new UrlBasedCorsConfigurationSource();
//
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
//}
package com.project.kisan_setu.security;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration

public class CorsConfigurationSourceConfig {

    @Bean

    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Allow ALL ngrok domains (both .app and .dev)

        configuration.setAllowedOriginPatterns(List.of(

                "http://192.168.29.99:3000",

                "https://*.ngrok-free.app",

                "https://*.ngrok-free.dev"

        ));

        // ✅ Methods

        configuration.setAllowedMethods(List.of(

                "GET", "POST", "PUT", "DELETE", "OPTIONS"

        ));

        // ✅ Headers

        configuration.setAllowedHeaders(List.of("*"));

        // ⚠️ IMPORTANT: keep true only if using cookies/JWT in headers

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =

                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;

    }

}

