import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Cliente } from '../../models/cliente.model';
import { Reporte } from '../../models/reporte.model';
import { ReportesComponent } from './reportes.component';

const clientesMock: Cliente[] = [
  {
    id: 1, clienteId: 'jlema', nombre: 'Jose Lema', genero: 'M', edad: 34,
    identificacion: '0912345678', direccion: 'Otavalo sn y principal',
    telefono: '098254785', estado: true
  },
  {
    id: 2, clienteId: 'mmontalvo', nombre: 'Marianela Montalvo', genero: 'F', edad: 28,
    identificacion: '0998765432', direccion: 'Amazonas y NNUU',
    telefono: '097548965', estado: true
  }
];

const reporteMock: Reporte = {
  cliente: 'Jose Lema',
  fechaInicio: '2022-02-01',
  fechaFin: '2022-02-28',
  cuentas: [
    {
      numeroCuenta: '478758', tipoCuenta: 'Ahorro', saldoInicial: 2000,
      estado: true, totalCreditos: 600, totalDebitos: 575, saldoDisponible: 2025
    }
  ],
  movimientos: [
    {
      'Fecha': '10/2/2022',
      'Cliente': 'Jose Lema',
      'Numero Cuenta': '478758',
      'Tipo': 'Ahorro',
      'Saldo Inicial': 2000,
      'Estado': true,
      'Movimiento': -575,
      'Saldo Disponible': 1425
    }
  ]
};

describe('ReportesComponent', () => {
  let fixture: ComponentFixture<ReportesComponent>;
  let componente: ReportesComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportesComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(ReportesComponent);
    componente = fixture.componentInstance;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe cargar el combo de clientes al iniciar', () => {
    fixture.detectChanges();

    httpMock.expectOne('/api/clientes').flush(clientesMock);
    fixture.detectChanges();

    const opciones = Array.from(
      fixture.nativeElement.querySelectorAll('select option')
    ).map(opcion => (opcion as HTMLOptionElement).textContent?.trim());

    expect(opciones).toContain('Jose Lema');
    expect(opciones).toContain('Marianela Montalvo');
  });

  it('debe consultar el estado de cuenta con los filtros y pintar las tablas', () => {
    fixture.detectChanges();
    httpMock.expectOne('/api/clientes').flush(clientesMock);
    fixture.detectChanges();

    componente.filtros.setValue({
      clienteId: '1',
      fechaInicio: '2022-02-01',
      fechaFin: '2022-02-28'
    });
    componente.buscar();

    const peticion = httpMock.expectOne(req => req.url === '/api/reportes');
    expect(peticion.request.method).toBe('GET');
    expect(peticion.request.params.get('clienteId')).toBe('1');
    expect(peticion.request.params.get('fechaInicio')).toBe('2022-02-01');
    expect(peticion.request.params.get('fechaFin')).toBe('2022-02-28');

    peticion.flush(reporteMock);
    fixture.detectChanges();

    const tablas = fixture.nativeElement.querySelectorAll('table.tabla');
    expect(tablas.length).toBe(2);
    expect(tablas[0].textContent).toContain('478758');
    expect(tablas[0].textContent).toContain('2,025.00');
    expect(tablas[1].textContent).toContain('Jose Lema');
    expect(tablas[1].textContent).toContain('-575.00');
    expect(tablas[1].textContent).toContain('1,425.00');
  });

  it('no debe llamar al servicio cuando las fechas vienen invertidas', () => {
    fixture.detectChanges();
    httpMock.expectOne('/api/clientes').flush(clientesMock);
    fixture.detectChanges();

    componente.filtros.setValue({
      clienteId: '1',
      fechaInicio: '2022-02-28',
      fechaFin: '2022-02-01'
    });
    componente.buscar();
    fixture.detectChanges();

    httpMock.expectNone(req => req.url === '/api/reportes');
    const mensaje = fixture.nativeElement.querySelector('.mensaje-error');
    expect(mensaje?.textContent).toContain('La fecha fin debe ser mayor o igual a la fecha inicio');
  });
});
