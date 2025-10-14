package com.example.weather_app.client;

import com.example.weather_app.dto.AemetInitialResponse;
import com.example.weather_app.dto.DailyForecastResponse;
import com.example.weather_app.dto.MunicipalityResponse;
import com.example.weather_app.model.WeatherResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.*;

@Component
public class AemetClient {

    private final WebClient webClient;
    private final String baseUrl;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AemetClient(
            @Value("${aemet.base-url}") String baseUrl,
            @Value("${aemet.api-key}") String apiKey
    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;

        try {
            io.netty.handler.ssl.SslContext nettySslContext =
                    io.netty.handler.ssl.SslContextBuilder.forClient()
                            .trustManager(io.netty.handler.ssl.util.InsecureTrustManagerFactory.INSTANCE)
                            .build();

            reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create()
                    .secure(t -> t.sslContext(nettySslContext));

            this.webClient = WebClient.builder()
                    .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                    .build();

            System.out.println("✅ WebClient configurado con SSL relajado para AEMET.");
        } catch (Exception e) {
            throw new RuntimeException("❌ Error configurando SSL para WebClient", e);
        }
    }

    private String buildUrl(String path) {
        if (path == null) return null;
        if (path.startsWith("http")) return path;
        String b = baseUrl == null ? "" : baseUrl.trim();
        if (b.endsWith("/")) b = b.substring(0, b.length() - 1);
        if (!path.startsWith("/")) path = "/" + path;
        return b + path;
    }

    // --------------------------------------------------------------------------------------------
    // MUNICIPIOS
    // --------------------------------------------------------------------------------------------
    public Mono<List<MunicipalityResponse>> getMunicipalitiesByName(String name) {
        String initialUrl = buildUrl("/maestro/municipios") + "?api_key=" + apiKey;
        System.out.println("🌍 Llamando a AEMET (municipios) → " + initialUrl);

        return webClient.get()
                .uri(initialUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AemetInitialResponse.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(2)))
                .flatMap(initialResponse -> {
                    if (initialResponse == null || initialResponse.getDatos() == null) {
                        System.err.println("⚠️ No hay campo 'datos' en la respuesta inicial (municipios)");
                        return Mono.<List<MunicipalityResponse>>just(Collections.emptyList());
                    }

                    String datosUrl = initialResponse.getDatos();
                    System.out.println("➡️ URL de datos municipios AEMET: " + datosUrl);

                    return webClient.get()
                            .uri(URI.create(datosUrl))
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(body -> parseMunicipalities(body, name))
                            .onErrorResume(e -> {
                                System.err.println("❌ Error obteniendo municipios: " + e.getMessage());
                                e.printStackTrace();
                                return Mono.<List<MunicipalityResponse>>just(Collections.emptyList());
                            });
                })
                .onErrorResume(e -> {
                    System.err.println("❌ Error general en getMunicipalitiesByName: " + e.getMessage());
                    e.printStackTrace();
                    return Mono.<List<MunicipalityResponse>>just(Collections.emptyList());
                });
    }

    // --------------------------------------------------------------------------------------------
    // PREVISIÓN DIARIA
    // --------------------------------------------------------------------------------------------
    public Mono<List<DailyForecastResponse>> getDailyForecast(String municipioId) {
        // Normalizar ID: AEMET espera el número sin el prefijo "id"
        String cleanId = municipioId == null ? null : municipioId.replaceAll("^id", "");
        String initialUrl = buildUrl("/prediccion/especifica/municipio/diaria/" + cleanId) + "?api_key=" + apiKey;
        System.out.println("🌤️ Llamando a AEMET (predicción diaria) → " + initialUrl + " (originalId=" + municipioId + ")");

        return webClient.get()
                .uri(initialUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AemetInitialResponse.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(3)))
                .flatMap(initialResponse -> {
                    if (initialResponse == null) {
                        System.err.println("⚠️ Respuesta inicial nula de AEMET");
                        return Mono.<List<DailyForecastResponse>>just(Collections.emptyList());
                    }

                    Integer estado = initialResponse.getEstado();
                    if (estado == null || estado.intValue() != 200 || initialResponse.getDatos() == null) {
                        System.err.println("⚠️ AEMET no tiene datos válidos para " + cleanId +
                                " → Estado=" + estado +
                                ", Descripción=" + initialResponse.getDescripcion());
                        return Mono.<List<DailyForecastResponse>>just(Collections.emptyList());
                    }

                    String datosUrl = initialResponse.getDatos();
                    System.out.println("➡️ URL de datos predicción AEMET: " + datosUrl);

                    return webClient.get()
                            .uri(URI.create(datosUrl))
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(this::parseForecastBody)
                            .onErrorResume(e -> {
                                System.err.println("❌ Error parseando datos de predicción: " + e.getMessage());
                                return Mono.<List<DailyForecastResponse>>just(Collections.emptyList());
                            });
                })
                .onErrorResume(e -> {
                    System.err.println("❌ Error general en getDailyForecast: " + e.getMessage());
                    return Mono.<List<DailyForecastResponse>>just(Collections.emptyList());
                });
    }

    // --------------------------------------------------------------------------------------------
    // PARSEADORES
    // --------------------------------------------------------------------------------------------
    private List<MunicipalityResponse> parseMunicipalities(String body, String name) {
        try {
            if (body == null || body.isEmpty()) {
                System.err.println("⚠️ Cuerpo vacío recibido de AEMET (municipios)");
                return Collections.emptyList();
            }

            //  AEMET usa símbolos extraños en su JSON
            String sanitized = body
                    .replace("║", "")
                    .replace("", "'")
                    .replace("", "\"")
                    .replace("", "\"");

            List<Map<String, Object>> municipios = objectMapper.readValue(sanitized, new TypeReference<>() {});
            List<MunicipalityResponse> filtrados = new ArrayList<>();

            for (Map<String, Object> m : municipios) {
                String id = (String) m.get("id");
                String nombre = (String) m.get("nombre");
                if (nombre != null && nombre.toLowerCase().contains(name.toLowerCase())) {
                    filtrados.add(new MunicipalityResponse(id, nombre));
                }
            }

            System.out.println("✅ Municipios filtrados: " + filtrados.size());
            return filtrados;
        } catch (Exception e) {
            System.err.println("❌ Error parseando municipios: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    private List<DailyForecastResponse> parseForecastBody(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            List<Map<String, Object>> lista = objectMapper.readValue(body, new TypeReference<>() {});
            List<DailyForecastResponse> result = new ArrayList<>();

            if (!lista.isEmpty()) {
                Map<String, Object> root = (Map<String, Object>) lista.get(0);
                Map<String, Object> prediccion = (Map<String, Object>) root.get("prediccion");
                if (prediccion == null) return Collections.emptyList();

                List<Map<String, Object>> dias = (List<Map<String, Object>>) prediccion.get("dia");
                if (dias == null) return Collections.emptyList();

                for (Map<String, Object> dia : dias) {
                    DailyForecastResponse fr = new DailyForecastResponse();
                    fr.setFecha((String) dia.get("fecha"));

                    Map<String, Object> temp = (Map<String, Object>) dia.get("temperatura");
                    if (temp != null) {
                        fr.setTempMin(toInteger(temp.get("minima")));
                        fr.setTempMax(toInteger(temp.get("maxima")));
                    }

                    List<Map<String, Object>> probPrecRaw = (List<Map<String, Object>>) dia.get("probPrecipitacion");
                    List<WeatherResponse.PrecipitationProbability> probList = new ArrayList<>();
                    if (probPrecRaw != null) {
                        for (Map<String, Object> p : probPrecRaw) {
                            WeatherResponse.PrecipitationProbability pp = new WeatherResponse.PrecipitationProbability();
                            Object value = p.get("value");
                            if (value == null) value = p.get("probabilidad");
                            pp.setProbabilidad(toInteger(value));
                            pp.setPeriodo(p.get("periodo") == null ? "" : p.get("periodo").toString());
                            probList.add(pp);
                        }
                    }
                    fr.setProbPrecipitacion(probList);
                    result.add(fr);
                }
            }
            return result;
        } catch (Exception e) {
            System.err.println("❌ Error parseando predicción: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private Integer toInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(o.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
