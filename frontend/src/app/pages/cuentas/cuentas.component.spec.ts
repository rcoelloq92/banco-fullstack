import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Cliente } from '../../models/cliente.model';
import { Cuenta } from '../../models/cuenta.model';
import { CuentasComponent } from './cuentas.component';

const CUENTAS: Cuenta[] = [
  { id: 1, numeroCuenta: '478758', tipoCuenta: 'AHORRO', saldoInicial: 2000, estado: true, clienteId: 1, clienteNombre: 'Jose Lema' },
  { id: 2, numeroCuenta: '225487', tipoCuenta: 'CORRIENTE', saldoInicial: 100, estado: true, clienteId: 2, clienteNombre: 'Marianela Montalvo' }
];

const CLIENTES: Cliente[] = [
  { id: 1, clienteId: 'jlema', nombre: 'Jose Lema', genero: 'M', edad: 35, identificacion: '0998547851', direccion: 'Otavalo sn y principal', telefono: '098254785', estado: true },
  { id: 2, clienteId: 'mmontalvo', nombre: 'Marianela Montalvo', genero: 'F', edad: 30, identificacion: '0975489654', direccion: 'Amazonas y NNUU', telefono: '097548965', estado: true }
];

describe('CuentasComponent', () => {
  let fixture: ComponentFixture<CuentasComponent>;
  let http: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CuentasComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(CuentasComponent);
    fixture.detectChanges();
  });

  afterEach(() => http.verify());

  function responderCargaInicial(): void {
    http.expectOne('/api/cuentas').flush(CUENTAS);
    http.expectOne('/api/clientes').flush(CLIENTES);
    fixture.detectChanges();
  }

  it('al iniciar consulta cuentas y clientes y pinta la tabla', () => {
    responderCargaInicial();

    const filas = fixture.nativeElement.querySelectorAll('.tabla tbody tr');
    expect(filas.length).toBe(2);
    expect(filas[0].textContent).toContain('478758');
    expect(filas[0].textContent).toContain('Ahorro');
    expect(filas[0].textContent).toContain('Jose Lema');
    expect(filas[1].textContent).toContain('Corriente');
    expect(filas[1].textContent).toContain('Marianela Montalvo');
  });

  it('la búsqueda filtra por número de cuenta o nombre del cliente', () => {
    responderCargaInicial();

    const busqueda: HTMLInputElement = fixture.nativeElement.querySelector('.campo-busqueda');
    busqueda.value = 'marianela';
    busqueda.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    let filas = fixture.nativeElement.querySelectorAll('.tabla tbody tr');
    expect(filas.length).toBe(1);
    expect(filas[0].textContent).toContain('225487');

    busqueda.value = '478758';
    busqueda.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    filas = fixture.nativeElement.querySelectorAll('.tabla tbody tr');
    expect(filas.length).toBe(1);
    expect(filas[0].textContent).toContain('Jose Lema');
  });

  it('guardar con formulario válido emite POST /api/cuentas y recarga el listado', () => {
    responderCargaInicial();
    const componente = fixture.componentInstance;

    componente.nueva();
    componente.formulario.setValue({
      numeroCuenta: '585545',
      tipoCuenta: 'CORRIENTE',
      saldoInicial: 1000,
      estado: true,
      clienteId: 1
    });
    componente.guardar();

    const peticion = http.expectOne('/api/cuentas');
    expect(peticion.request.method).toBe('POST');
    expect(peticion.request.body).toEqual({
      numeroCuenta: '585545',
      tipoCuenta: 'CORRIENTE',
      saldoInicial: 1000,
      estado: true,
      clienteId: 1
    });
    const creada: Cuenta = { id: 3, numeroCuenta: '585545', tipoCuenta: 'CORRIENTE', saldoInicial: 1000, estado: true, clienteId: 1, clienteNombre: 'Jose Lema' };
    peticion.flush(creada);

    const recarga = http.expectOne('/api/cuentas');
    expect(recarga.request.method).toBe('GET');
    recarga.flush([...CUENTAS, creada]);
    fixture.detectChanges();

    expect(componente.mensajeExito).toBe('Cuenta creada correctamente');
    const filas = fixture.nativeElement.querySelectorAll('.tabla tbody tr');
    expect(filas.length).toBe(3);
    expect(filas[2].textContent).toContain('585545');
  });
});
