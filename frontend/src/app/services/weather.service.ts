import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SimplifiedForecastResponse } from '../models/simplified-forecast-response.model';
import { MunicipalityResponse } from '../models/municipality-response.model';

@Injectable({
  providedIn: 'root'
})
export class WeatherService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  /**
   * Busca municipios por nombre. Si hay error o respuesta vacía, devuelve lista vacía.
   */
  searchMunicipalities(name: string): Observable<MunicipalityResponse[]> {
    if (!name || name.trim().length === 0) {
      return of([]);
    }

    const params = new HttpParams().set('name', name.trim());

    return this.http.get<MunicipalityResponse[]>(`${this.apiUrl}/municipalities`, { params })
      .pipe(
        catchError((err: HttpErrorResponse) => {
          console.error('❌ Error en searchMunicipalities:', err.message);
          return of([]);
        })
      );
  }

  /**
   * Obtiene la previsión diaria simplificada para un municipio dado.
   * Si hay error o respuesta inválida, devuelve un objeto por defecto.
   */
  getDailyForecast(municipioId: string, unit: string): Observable<SimplifiedForecastResponse> {
    if (!municipioId) {
      return of({
        mediaTemperatura: 0,
        unidadTemperatura: unit === 'G_FAH' ? '°F' : '°C',
        probPrecipitacion: []
      });
    }

    const params = new HttpParams().set('unit', unit);

    return this.http.get<SimplifiedForecastResponse>(`${this.apiUrl}/forecast/${municipioId}`, { params })
      .pipe(
        catchError((err: HttpErrorResponse) => {
          console.error(`❌ Error obteniendo previsión para ${municipioId}:`, err.message);
          return of({
            mediaTemperatura: 0,
            unidadTemperatura: unit === 'G_FAH' ? '°F' : '°C',
            probPrecipitacion: []
          });
        })
      );
  }
}
