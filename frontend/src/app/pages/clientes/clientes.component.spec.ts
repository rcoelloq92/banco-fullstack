import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Cliente } from '../../models/cliente.model';
import { ClientesComponent } from './clientes.component';

const CLIENTES: Cliente[] = [
  {
    id: 1,
    clienteId: 'jlema',
    nombre: 'Jose Lema',
    genero: 'Masculino',
    edad: 32,
    identificacion: '0912345678',
    direccion: 'Otavalo s/n y principal',
    telefono: '098254785',
    estado: true
  },
  {
    id: 2,
    clienteId: 'mmontalvo',
    nombre: 'Marianela Montalvo',
    genero: 'Femenino',
    edad: 28,
    identificacion: '0998765432',
    direccion: 'Amazonas y NNUU',
    telefono: '097548965',
    estado: true
  }
];

describe('ClientesComponent', () => {
  let fixture: ComponentFixture<ClientesComponent>;
  let componente: ClientesComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientesComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(ClientesComponent);
    componente = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  function cargarLista(clientes: Cliente[]): void {
    fixture.detectChanges();
    const peticion = httpMock.expectOne('/api/clientes');
    expect(peticion.request.method).toBe('GET');
    peticion.flush(clientes);
    fixture.detectChanges();
  }

  it('carga y pinta la lista de clientes al iniciar', () => {
    cargarLista(CLIENTES);

    const filas: NodeListOf<HTMLTableRowElement> =
      fixture.nativeElement.querySelectorAll('tbody tr');

    expect(filas.length).toBe(2);
    expect(filas[0].textContent).toContain('Jose Lema');
    expect(filas[1].textContent).toContain('Marianela Montalvo');
  });

  it('el filtro de búsqueda reduce los resultados en memoria', () => {
    cargarLista(CLIENTES);

    componente.controlBusqueda.setValue('MARIANELA');
    fixture.detectChanges();

    const filas: NodeListOf<HTMLTableRowElement> =
      fixture.nativeElement.querySelectorAll('tbody tr');

    expect(filas.length).toBe(1);
    expect(filas[0].textContent).toContain('Marianela Montalvo');
  });

  it('no envía la petición de creación cuando el formulario es inválido', () => {
    cargarLista([]);

    componente.nuevo();
    componente.guardar();

    httpMock.expectNone({ method: 'POST', url: '/api/clientes' });
    expect(componente.formulario.invalid).toBe(true);
    expect(componente.formularioVisible).toBe(true);
  });
});
