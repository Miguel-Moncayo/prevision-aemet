package com.example.weather_app.dto;

import com.example.weather_app.model.WeatherResponse;
import java.util.List;

public class DailyForecastResponse {
    private String fecha;
    private Integer tempMin;
    private Integer tempMax;
    private List<WeatherResponse.PrecipitationProbability> probPrecipitacion;
    private List<EstadoCielo> estadoCielo;

    // Constructor vacío (IMPORTANTE para AemetClient)
    public DailyForecastResponse() {}

    // Constructor completo (opcional)
    public DailyForecastResponse(String fecha, Integer tempMin, Integer tempMax,
                                 List<WeatherResponse.PrecipitationProbability> probPrecipitacion,
                                 List<EstadoCielo> estadoCielo) {
        this.fecha = fecha;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.probPrecipitacion = probPrecipitacion;
        this.estadoCielo = estadoCielo;
    }

    // Getters / Setters
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public Integer getTempMin() { return tempMin; }
    public void setTempMin(Integer tempMin) { this.tempMin = tempMin; }

    public Integer getTempMax() { return tempMax; }
    public void setTempMax(Integer tempMax) { this.tempMax = tempMax; }

    public List<WeatherResponse.PrecipitationProbability> getProbPrecipitacion() { return probPrecipitacion; }
    public void setProbPrecipitacion(List<WeatherResponse.PrecipitationProbability> probPrecipitacion) {
        this.probPrecipitacion = probPrecipitacion;
    }

    public List<EstadoCielo> getEstadoCielo() { return estadoCielo; }
    public void setEstadoCielo(List<EstadoCielo> estadoCielo) { this.estadoCielo = estadoCielo; }

    // Clase interna para representar estado del cielo
    public static class EstadoCielo {
        private String descripcion;
        private String periodo;

        public EstadoCielo() {}

        public EstadoCielo(String descripcion, String periodo) {
            this.descripcion = descripcion;
            this.periodo = periodo;
        }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public String getPeriodo() { return periodo; }
        public void setPeriodo(String periodo) { this.periodo = periodo; }
    }
}
