package com.example.weather_app.dto;

import java.util.List;

public class SimplifiedForecastResponse {
    private double mediaTemperatura;
    private String unidadTemperatura; 
    private List<ProbPrecipitation> probPrecipitacion;

    public SimplifiedForecastResponse() {}

    public SimplifiedForecastResponse(double mediaTemperatura, String unidadTemperatura,
                                      List<ProbPrecipitation> probPrecipitacion) {
        this.mediaTemperatura = mediaTemperatura;
        this.unidadTemperatura = unidadTemperatura;
        this.probPrecipitacion = probPrecipitacion;
    }

    public double getMediaTemperatura() { return mediaTemperatura; }
    public void setMediaTemperatura(double mediaTemperatura) { this.mediaTemperatura = mediaTemperatura; }

    public String getUnidadTemperatura() { return unidadTemperatura; }
    public void setUnidadTemperatura(String unidadTemperatura) { this.unidadTemperatura = unidadTemperatura; }

    public List<ProbPrecipitation> getProbPrecipitacion() { return probPrecipitacion; }
    public void setProbPrecipitacion(List<ProbPrecipitation> probPrecipitacion) { this.probPrecipitacion = probPrecipitacion; }

    public static class ProbPrecipitation {
        private Integer probabilidad;
        private String periodo;

        public ProbPrecipitation() {}

        public ProbPrecipitation(Integer probabilidad, String periodo) {
            this.probabilidad = probabilidad;
            this.periodo = periodo;
        }

        public Integer getProbabilidad() { return probabilidad; }
        public void setProbabilidad(Integer probabilidad) { this.probabilidad = probabilidad; }

        public String getPeriodo() { return periodo; }
        public void setPeriodo(String periodo) { this.periodo = periodo; }
    }
}
