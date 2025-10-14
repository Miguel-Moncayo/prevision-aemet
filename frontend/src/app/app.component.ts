import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';

import { SearchMunicipalityComponent } from './components/search-municipality/search-municipality.component';
import { ForecastListComponent } from './components/forecast-list/forecast-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    SearchMunicipalityComponent,
    ForecastListComponent,
    MatFormFieldModule,
    MatSelectModule
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'frontend';
  selectedMunicipioId: string | null = null;
selectedUnit: 'G_CEL' | 'G_FAH' = 'G_CEL';


  onMunicipioSelected(municipioId: string) {
    console.log('📌 Municipio seleccionado en AppComponent:', municipioId);
    this.selectedMunicipioId = municipioId;
  }
}
