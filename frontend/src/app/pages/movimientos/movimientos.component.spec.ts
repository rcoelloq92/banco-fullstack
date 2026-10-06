import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Cuenta } from '../../models/cuenta.model';
import { Movimiento } from '../../models/movimiento.model';
import { MovimientosComponent } from './movimientos.component';

describe('MovimientosComponent', () => {

  let fixture: ComponentFixture<MovimientosComponent>;
  let componente: MovimientosComponent;
  let httpMock: HttpTestingController;

  const movimientosMock: Movimiento[] = [
    {
      id: 1,
      fecha: '2026-02-10T10:30:00',
      tipoMovimiento: 'CREDITO',
      valor: 600,
      saldo: 700,
      numeroCuenta: '478758',
      clienteNombre: 'Jose Lema'
    },
    {
      id: 2,
      fecha: '2026-02-10T11:00:00',
      tipoMovimiento: 'DEBITO',
      valor: -575,
      saldo: 125,
      numeroCuenta: '478758',
      clienteNombre: 'Jose Lema'
    }
  ];

  const cuentasMock: Cuenta[] = [
    {
      id: 1,
      numeroCuenta: '478758',
      tipoCuenta: 'AHORRO',
      saldoInicial: 2000,
      estado: true,
      clienteId: 1,
      clienteNombre: 'Jose Lema'
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MovimientosComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(MovimientosComponent);
    componente = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  /** Dispara ngOnInit y responde las dos cargas iniciales. */
  function iniciarComponente(): void {
    fixture.detectChanges();
    httpMock.expectOne('/api/movimientos').flush(movimientosMock);
    httpMock.expectOne('/api/cuentas').flush(cuentasMock);
    fixture.detectChanges();
  }

  it('debe cargar la lista inicial de movimientos y cuentas', () => {
    iniciarComponente();

    expect(componente.movimientos).toEqual(movimientosMock);
    expect(componente.cuentas).toEqual(cuentasMock);

    const filas = fixture.nativeElement.querySelectorAll('.tabla tbody tr');
    expect(filas.length).toBe(2);
    expect(filas[0].textContent).toContain('Jose Lema');
    expect(filas[0].textContent).toContain('478758');
  });

  it('debe enviar el POST al registrar un movimiento válido y recargar la lista', () => {
    iniciarComponente();

    componente.nuevo();
    componente.formulario.setValue({
      numeroCuenta: '478758',
      tipoMovimiento: 'DEBITO',
      valor: 575
    });
    componente.guardar();

    const peticion = httpMock.expectOne('/api/movimientos');
    expect(peticion.request.method).toBe('POST');
    expect(peticion.request.body).toEqual({
      numeroCuenta: '478758',
      tipoMovimiento: 'DEBITO',
      valor: 575
    });
    peticion.flush(movimientosMock[1]);

    // Tras registrar se recarga el listado
    httpMock.expectOne('/api/movimientos').flush(movimientosMock);
    fixture.detectChanges();

    expect(componente.mensajeExito).toBe('Movimiento registrado correctamente');
  });

  it('debe mostrar el mensaje de negocio del backend cuando el POST responde 400', () => {
    iniciarComponente();

    componente.nuevo();
    componente.formulario.setValue({
      numeroCuenta: '478758',
      tipoMovimiento: 'DEBITO',
      valor: 1000
    });
    componente.guardar();

    httpMock.expectOne('/api/movimientos').flush(
      { mensaje: 'Saldo no disponible', detalles: [] },
      { status: 400, statusText: 'Bad Request' }
    );
    fixture.detectChanges();

    expect(componente.mensajeError).toBe('Saldo no disponible');
    const alerta = fixture.nativeElement.querySelector('.mensaje-error');
    expect(alerta).toBeTruthy();
    expect(alerta.textContent).toContain('Saldo no disponible');
  });
});
