import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SearchMunicipalityComponent } from './search-municipality.component';
import { WeatherService } from '../../services/weather.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

describe('SearchMunicipalityComponent', () => {
  let component: SearchMunicipalityComponent;
  let fixture: ComponentFixture<SearchMunicipalityComponent>;
  let weatherService: jasmine.SpyObj<WeatherService>;

  beforeEach(async () => {
    const weatherSpy = jasmine.createSpyObj('WeatherService', ['searchMunicipalities']);

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, SearchMunicipalityComponent],
      providers: [{ provide: WeatherService, useValue: weatherSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(SearchMunicipalityComponent);
    component = fixture.componentInstance;
    weatherService = TestBed.inject(WeatherService) as jasmine.SpyObj<WeatherService>;
    fixture.detectChanges();
  });

  it('debería crearse el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería buscar municipios correctamente', () => {
    const mockMunicipios = [
      { id: '28079', nombre: 'Madrid' },
      { id: '08019', nombre: 'Barcelona' }
    ];
    weatherService.searchMunicipalities.and.returnValue(of(mockMunicipios));

    component.searchTerm = 'Madrid';
    component.onSearch();

    expect(weatherService.searchMunicipalities).toHaveBeenCalledWith('Madrid');
    expect(component.municipios.length).toBe(2);
  });

  it('debería manejar errores al buscar municipios', () => {
    weatherService.searchMunicipalities.and.returnValue(throwError(() => new Error('Error API')));

    component.searchTerm = 'Madrid';
    component.onSearch();

    expect(component.municipios).toEqual([]);
  });

  it('debería emitir el municipio seleccionado', () => {
    spyOn(component.municipioSeleccionado, 'emit');
    component.selectedMunicipioId = '28079';
    component.onSelect();
    expect(component.municipioSeleccionado.emit).toHaveBeenCalledWith('28079');
  });
});
