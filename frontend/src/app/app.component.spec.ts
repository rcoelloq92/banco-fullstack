import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [provideRouter([])]
    }).compileComponents();
  });

  it('debe crearse', () => {
    const fixture = TestBed.createComponent(AppComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('debe mostrar el logo del banco y las opciones del menú', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const html: HTMLElement = fixture.nativeElement;

    expect(html.querySelector('.logo')?.textContent).toContain('BANCO');

    const opciones = Array.from(html.querySelectorAll('.menu-lateral a'))
      .map(enlace => enlace.textContent?.trim());
    expect(opciones).toEqual(['Clientes', 'Cuentas', 'Movimientos', 'Reportes']);
  });
});
