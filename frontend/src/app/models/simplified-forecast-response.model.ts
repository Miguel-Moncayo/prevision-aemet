export interface SimplifiedForecastResponse {
  mediaTemperatura: number;
  unidadTemperatura: string;
  probPrecipitacion: ProbPrecipitation[];
}

export interface ProbPrecipitation {
  probabilidad: number;
  periodo: string;
}
