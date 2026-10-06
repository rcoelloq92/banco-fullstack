import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Cliente } from '../../models/cliente.model';
import { Cuenta, CuentaRequest, TipoCuenta } from '../../models/cuenta.model';
import { RespuestaError } from '../../models/respuesta-error.model';
import { ClienteService } from '../../services/cliente.service';
import { CuentaService } from '../../services/cuenta.service';

@Component({
  selector: 'app-cuentas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cuentas.component.html',
  styleUrl: './cuentas.component.css'
})
export class CuentasComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly cuentaService = inject(CuentaService);
  private readonly clienteService = inject(ClienteService);

  cuentas: Cuenta[] = [];
  clientes: Cliente[] = [];
  filtro = '';
  mostrarFormulario = false;
  idEnEdicion: number | null = null;
  mensajeExito = '';
  mensajeError = '';
  detallesError: string[] = [];

  readonly formulario = this.fb.group({
    numeroCuenta: this.fb.nonNullable.control('', Validators.required),
    tipoCuenta: this.fb.control<TipoCuenta | null>(null, Validators.required),
    saldoInicial: this.fb.control<number | null>(null, [Validators.required, Validators.min(0)]),
    estado: this.fb.nonNullable.control(true),
    clienteId: this.fb.control<number | null>(null, Validators.required)
  });

  ngOnInit(): void {
    this.cargarCuentas();
    this.clienteService.listar().subscribe({
      next: clientes => this.clientes = clientes,
      error: (error: HttpErrorResponse) => this.registrarError(error)
    });
  }

  /* Listado filtrado por número de cuenta o nombre del cliente */
  get cuentasFiltradas(): Cuenta[] {
    const termino = this.filtro.trim().toLowerCase();
    if (!termino) {
      return this.cuentas;
    }
    return this.cuentas.filter(cuenta =>
      cuenta.numeroCuenta.toLowerCase().includes(termino) ||
      cuenta.clienteNombre.toLowerCase().includes(termino));
  }

  nueva(): void {
    this.limpiarMensajes();
    this.idEnEdicion = null;
    this.formulario.reset();
    this.mostrarFormulario = true;
  }

  editar(cuenta: Cuenta): void {
    this.limpiarMensajes();
    this.idEnEdicion = cuenta.id;
    this.formulario.setValue({
      numeroCuenta: cuenta.numeroCuenta,
      tipoCuenta: cuenta.tipoCuenta,
      saldoInicial: cuenta.saldoInicial,
      estado: cuenta.estado,
      clienteId: cuenta.clienteId
    });
    this.mostrarFormulario = true;
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.limpiarMensajes();
    const request: CuentaRequest = this.formulario.getRawValue();
    const peticion = this.idEnEdicion === null
      ? this.cuentaService.crear(request)
      : this.cuentaService.actualizar(this.idEnEdicion, request);

    peticion.subscribe({
      next: () => {
        this.mensajeExito = this.idEnEdicion === null
          ? 'Cuenta creada correctamente'
          : 'Cuenta actualizada correctamente';
        this.cancelar();
        this.cargarCuentas();
      },
      error: (error: HttpErrorResponse) => this.registrarError(error)
    });
  }

  eliminar(cuenta: Cuenta): void {
    if (!confirm(`¿Está seguro de eliminar la cuenta ${cuenta.numeroCuenta}?`)) {
      return;
    }
    this.limpiarMensajes();
    this.cuentaService.eliminar(cuenta.id).subscribe({
      next: () => {
        this.mensajeExito = 'Cuenta eliminada correctamente';
        this.cargarCuentas();
      },
      error: (error: HttpErrorResponse) => this.registrarError(error)
    });
  }

  cancelar(): void {
    this.mostrarFormulario = false;
    this.idEnEdicion = null;
    this.formulario.reset();
  }

  campoInvalido(nombre: string): boolean {
    const control = this.formulario.get(nombre);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  private cargarCuentas(): void {
    this.cuentaService.listar().subscribe({
      next: cuentas => this.cuentas = cuentas,
      error: (error: HttpErrorResponse) => this.registrarError(error)
    });
  }

  /* El backend responde con RespuestaError (mensaje + detalles) en los fallos de negocio */
  private registrarError(error: HttpErrorResponse): void {
    const cuerpo = error.error as RespuestaError | null;
    this.mensajeError = cuerpo?.mensaje || 'Ocurrió un error al procesar la solicitud';
    this.detallesError = cuerpo?.detalles ?? [];
  }

  private limpiarMensajes(): void {
    this.mensajeExito = '';
    this.mensajeError = '';
    this.detallesError = [];
  }
}
