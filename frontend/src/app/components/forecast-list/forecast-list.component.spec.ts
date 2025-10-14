import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ForecastListComponent } from './forecast-list.component';
import { WeatherService } from '../../services/weather.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { SimplifiedForecastResponse } from '../../models/simplified-forecast-response.model';
import { CommonModule } from '@angular/common';

describe('ForecastListComponent', () => {
  let component: ForecastListComponent;
  let fixture: ComponentFixture<ForecastListComponent>;
  let weatherService: jasmine.SpyObj<WeatherService>;

  beforeEach(async () => {
    const weatherSpy = jasmine.createSpyObj('WeatherService', ['getDailyForecast']);

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CommonModule, ForecastListComponent],
      providers: [{ provide: WeatherService, useValue: weatherSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(ForecastListComponent);
    component = fixture.componentInstance;
    weatherService = TestBed.inject(WeatherService) as jasmine.SpyObj<WeatherService>;
    fixture.detectChanges();
  });

  it('debería crearse el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería cargar la previsión correctamente', () => {
    const mockForecast: SimplifiedForecastResponse = {
      mediaTemperatura: 18,
      unidadTemperatura: '°C',
      probPrecipitacion: [
        { probabilidad: 40, periodo: '00-12' },
        { probabilidad: 20, periodo: '12-24' }
      ]
    };

    weatherService.getDailyForecast.and.returnValue(of(mockForecast));

    component.municipioId = '28079';
    component.ngOnChanges({
      municipioId: { currentValue: '28079', previousValue: null, firstChange: true, isFirstChange: () => true }
    });

    expect(component.forecast).toEqual(mockForecast);
    expect(component.error).toBeNull();
  });

  it('debería manejar error al cargar la previsión', () => {
    weatherService.getDailyForecast.and.returnValue(throwError(() => new Error('Error de API')));

    component.municipioId = '28079';
    component.ngOnChanges({
      municipioId: { currentValue: '28079', previousValue: null, firstChange: true, isFirstChange: () => true }
    });

    expect(component.forecast).toBeNull();
    expect(component.error).toBe('Error al cargar previsión');
  });
});
