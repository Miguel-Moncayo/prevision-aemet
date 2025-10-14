import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';

import { WeatherService } from '../../services/weather.service';
import { MunicipalityResponse } from '../../models/municipality-response.model';

@Component({
  selector: 'app-search-municipality',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatOptionModule
  ],
  templateUrl: './search-municipality.component.html',
  styleUrls: ['./search-municipality.component.scss']
})
export class SearchMunicipalityComponent {
  // Modelo del input
  searchTerm: string = '';

  // Resultados
  municipios: MunicipalityResponse[] = [];

  // ID seleccionado en el select
  selectedMunicipioId: string | null = null;

  @Output() municipioSeleccionado = new EventEmitter<string>();

  constructor(private weatherService: WeatherService) {}

  // Pulsar "Buscar" o Enter ejecuta esto
  onSearch(): void {
    console.log('🔎 Ejecutando onSearch con término:', this.searchTerm);

    const q = (this.searchTerm || '').trim();
    if (!q) {
      console.warn('⚠️ searchTerm vacío, no se busca nada');
      this.municipios = [];
      this.selectedMunicipioId = null;
      return;
    }

    // Llamada al backend (AEMET API via Spring Boot)
    this.weatherService.searchMunicipalities(q).subscribe({
      next: (data) => {
        console.log('📡 Respuesta cruda del backend:', data);
        this.municipios = Array.isArray(data) ? data : [];
        console.log('✅ Municipios encontrados:', this.municipios);
      },
      error: (err) => {
        console.error('❌ Error buscando municipios', err);
        this.municipios = [];
      }
    });
  }

  onSelect(): void {
    if (this.selectedMunicipioId) {
      this.municipioSeleccionado.emit(this.selectedMunicipioId);
      console.log('➡️ Municipio seleccionado emitido:', this.selectedMunicipioId);
    }
  }
}
