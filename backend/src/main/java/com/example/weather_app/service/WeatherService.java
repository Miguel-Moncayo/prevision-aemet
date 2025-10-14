package com.example.weather_app.service;

import com.example.weather_app.client.AemetClient;
import com.example.weather_app.dto.DailyForecastResponse;
import com.example.weather_app.dto.MunicipalityResponse;
import com.example.weather_app.dto.SimplifiedForecastResponse;
import com.example.weather_app.dto.SimplifiedForecastResponse.ProbPrecipitation;
import com.example.weather_app.model.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);
    private final AemetClient aemetClient;

    public WeatherService(AemetClient aemetClient) {
        this.aemetClient = aemetClient;
    }

    /**
     * Busca municipios por nombre delegando en  AEMET.
     */
    public Mono<List<MunicipalityResponse>> searchMunicipalities(String name) {
        log.debug("➡️ Delegando búsqueda de municipios a AemetClient con name={}", name);
        return aemetClient.getMunicipalitiesByName(name);
    }

    /**
     * Devuelve la predicción simplificada para el día siguiente.
     * Si AEMET no tiene datos o hay error, devuelve una respuesta vacía con código 200.
     */
    public Mono<SimplifiedForecastResponse> getDailyForecast(String municipioId, String unit) {
        final String unidad = (unit == null || unit.isBlank()) ? "G_CEL" : unit;

        return aemetClient.getDailyForecast(municipioId)
                .flatMap(list -> {
                    if (list == null || list.isEmpty()) {
                        log.warn("⚠️ No se encontraron previsiones para el municipio {}. Devolviendo respuesta vacía.", municipioId);
                        return Mono.just(crearRespuestaVacia(unidad));
                    }

                    DailyForecastResponse day = list.get(0);
                    Integer min = day.getTempMin();
                    Integer max = day.getTempMax();
                    double mediaC = 0.0;

                    if (min != null && max != null) {
                        mediaC = (min + max) / 2.0;
                    } else if (min != null) {
                        mediaC = min;
                    } else if (max != null) {
                        mediaC = max;
                    }

                    double mediaFinal = mediaC;
                    if ("G_FAH".equalsIgnoreCase(unidad)) {
                        mediaFinal = mediaC * 9.0 / 5.0 + 32.0;
                    }

                    List<ProbPrecipitation> probs = new ArrayList<>();
                    List<WeatherResponse.PrecipitationProbability> rawProbs = day.getProbPrecipitacion();

                    if (rawProbs != null) {
                        for (WeatherResponse.PrecipitationProbability p : rawProbs) {
                            Integer prob = p.getProbabilidad();
                            String periodo = p.getPeriodo();
                            probs.add(new ProbPrecipitation(prob, periodo));
                        }
                    }

                    SimplifiedForecastResponse resp = new SimplifiedForecastResponse(mediaFinal, unidad.equals("G_FAH") ? "°F" : "°C", probs);
                    return Mono.just(resp);
                })
                .onErrorResume(e -> {
                    log.error("❌ Error obteniendo predicción para municipio {}: {}", municipioId, e.getMessage());
                    return Mono.just(crearRespuestaVacia(unidad));
                });
    }

    /**
     *  respuesta vacía por defecto para casos de error o falta de datos.
     */
    private SimplifiedForecastResponse crearRespuestaVacia(String unidad) {
        SimplifiedForecastResponse vacia = new SimplifiedForecastResponse();
        vacia.setMediaTemperatura(0);
        vacia.setUnidadTemperatura(unidad.equalsIgnoreCase("G_FAH") ? "°F" : "°C");
        vacia.setProbPrecipitacion(new ArrayList<>());
        return vacia;
    }
}
