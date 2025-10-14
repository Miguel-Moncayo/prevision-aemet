package com.example.weather_app.model;

import java.util.List;

public class WeatherResponse {

    private double mediaTemperatura;
    private String unidadTemperatura;
    private List<PrecipitationProbability> probPrecipitacion;

    // Getters y setters
    public double getMediaTemperatura() {
        return mediaTemperatura;
    }

    public void setMediaTemperatura(double mediaTemperatura) {
        this.mediaTemperatura = mediaTemperatura;
    }

    public String getUnidadTemperatura() {
        return unidadTemperatura;
    }

    public void setUnidadTemperatura(String unidadTemperatura) {
        this.unidadTemperatura = unidadTemperatura;
    }

    public List<PrecipitationProbability> getProbPrecipitacion() {
        return probPrecipitacion;
    }

    public void setProbPrecipitacion(List<PrecipitationProbability> probPrecipitacion) {
        this.probPrecipitacion = probPrecipitacion;
    }

    // Clase interna
    public static class PrecipitationProbability {
        private int probabilidad;
        private String periodo;

        public int getProbabilidad() {
            return probabilidad;
        }

        public void setProbabilidad(int probabilidad) {
            this.probabilidad = probabilidad;
        }

        public String getPeriodo() {
            return periodo;
        }

        public void setPeriodo(String periodo) {
            this.periodo = periodo;
        }
    }
}
