package com.example.weather_app.controller;

import com.example.weather_app.dto.MunicipalityResponse;
import com.example.weather_app.dto.SimplifiedForecastResponse;
import com.example.weather_app.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/api")
public class WeatherController {

    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/municipalities")
    public Mono<List<MunicipalityResponse>> searchMunicipalities(@RequestParam String name) {
        log.info("🔎 Buscando municipios con nombre: {}", name);
        return weatherService.searchMunicipalities(name)
                .doOnSuccess(result -> log.info("✅ Municipios encontrados: {}", result.size()))
                .doOnError(e -> log.error("❌ Error buscando municipios: {}", e.getMessage()));
    }

    @GetMapping("/forecast/{municipioId}")
    public Mono<SimplifiedForecastResponse> getDailyForecast(
            @PathVariable String municipioId,
            @RequestParam(value = "unit", required = false) String unit
    ) {
        log.info("📅 Solicitando predicción simplificada para municipioId: {}, unidad: {}", municipioId, unit);
        return weatherService.getDailyForecast(municipioId, unit)
                .doOnSuccess(resp -> log.info("✅ Predicción obtenida correctamente para {}", municipioId))
                .doOnError(e -> log.error("❌ Error obteniendo predicción: {}", e.getMessage()));
    }
}
