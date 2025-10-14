import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WeatherService } from '../../services/weather.service';
import { SimplifiedForecastResponse } from '../../models/simplified-forecast-response.model';

@Component({
  selector: 'app-forecast-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './forecast-list.component.html',
  styleUrls: ['./forecast-list.component.scss']
})
export class ForecastListComponent implements OnChanges {
  @Input() municipioId: string | null = null;
  @Input() unit: 'G_CEL' | 'G_FAH' = 'G_CEL';

  forecast: SimplifiedForecastResponse | null = null;
  loading = false;
  error: string | null = null;

  constructor(private weatherService: WeatherService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['municipioId'] && this.municipioId) {
      this.loadForecast();
    }
  }

  /**
   * Carga la previsión del municipio seleccionado.
   */
  private loadForecast(): void {
    this.loading = true;
    this.error = null;
    this.forecast = null;

    this.weatherService.getDailyForecast(this.municipioId!, this.unit).subscribe({
      next: (data) => {
        this.forecast = data;
        this.loading = false;

        // Si la respuesta es vacía (mediaTemperatura=0 y sin precipitaciones)
        if (
          (!data || data.mediaTemperatura === 0) &&
          (!data.probPrecipitacion || data.probPrecipitacion.length === 0)
        ) {
          this.error = 'No hay previsión disponible para este municipio.';
        }
      },
      error: (err) => {
        console.error('❌ Error cargando previsión', err);
        this.error = 'Error al cargar previsión.';
        this.loading = false;
      }
    });
  }
}
