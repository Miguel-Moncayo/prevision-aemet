import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { WeatherService } from './services/weather.service';
import { SearchMunicipalityComponent } from './components/search-municipality/search-municipality.component';
import { ForecastListComponent } from './components/forecast-list/forecast-list.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        NoopAnimationsModule,
        AppComponent,
        SearchMunicipalityComponent,
        ForecastListComponent
      ],
      providers: [WeatherService]
    }).compileComponents();
  });

  it('debería crearse la aplicación', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('debería renderizar el título en un h1', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('🌤️ Previsión AEMET');
  });

  it('debería actualizar el municipio seleccionado cuando se emite el evento', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    app.onMunicipioSelected('28079');
    expect(app.selectedMunicipioId).toBe('28079');
  });
});
