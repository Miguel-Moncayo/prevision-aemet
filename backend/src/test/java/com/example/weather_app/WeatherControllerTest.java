package com.example.weather_app;

import com.example.weather_app.controller.WeatherController;
import com.example.weather_app.dto.MunicipalityResponse;
import com.example.weather_app.dto.SimplifiedForecastResponse;
import com.example.weather_app.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeatherControllerTest {

    @Test
    void getDailyForecastShouldReturnSimplifiedForecast() {
        // Mock del servicio
        WeatherService svc = Mockito.mock(WeatherService.class);

        // Preparar respuesta simplificada de ejemplo
        SimplifiedForecastResponse.ProbPrecipitation prob = new SimplifiedForecastResponse.ProbPrecipitation(10, "00-06");
        SimplifiedForecastResponse expected = new SimplifiedForecastResponse(12.5, "G_CEL", List.of(prob));

        // Stub del servicio 
        Mockito.when(svc.getDailyForecast("28079", "G_CEL")).thenReturn(Mono.just(expected));

        // Crear controlador con el mock
        WeatherController controller = new WeatherController(svc);

        // Ejecutar y comprobar
        SimplifiedForecastResponse out = controller.getDailyForecast("28079", "G_CEL").block();
        assertNotNull(out);
        assertEquals(12.5, out.getMediaTemperatura());
        assertEquals("G_CEL", out.getUnidadTemperatura());
        assertNotNull(out.getProbPrecipitacion());
        assertEquals(1, out.getProbPrecipitacion().size());
        assertEquals(10, out.getProbPrecipitacion().get(0).getProbabilidad());
    }

    @Test
    void searchMunicipalitiesShouldReturnList() {
        WeatherService svc = Mockito.mock(WeatherService.class);
        MunicipalityResponse m = new MunicipalityResponse("28079", "Madrid");
        Mockito.when(svc.searchMunicipalities("madrid")).thenReturn(Mono.just(List.of(m)));

        WeatherController controller = new WeatherController(svc);
        List<MunicipalityResponse> out = controller.searchMunicipalities("madrid").block();

        assertNotNull(out);
        assertEquals(1, out.size());
        // Ajusta este getter si el DTO tiene otro nombre de método (getNombre / getName)
        assertEquals("Madrid", out.get(0).getNombre());
    }
}
