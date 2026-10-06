import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Cuenta } from '../../models/cuenta.model';
import { Movimiento, MovimientoRequest, TipoMovimiento } from '../../models/movimiento.model';
import { RespuestaError } from '../../models/respuesta-error.model';
import { CuentaService } from '../../services/cuenta.service';
import { MovimientoService } from '../../services/movimiento.service';

@Component({
  selector: 'app-movimientos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './movimientos.component.html',
  styleUrl: './movimientos.component.css'
})
export class MovimientosComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly movimientoService = inject(MovimientoService);
  private readonly cuentaService = inject(CuentaService);

  movimientos: Movimiento[] = [];
  cuentas: Cuenta[] = [];
  filtro = '';
  mostrarFormulario = false;
  mensajeExito = '';
  mensajeError = '';

  formulario = this.fb.group({
    numeroCuenta: ['', Validators.required],
    tipoMovimiento: [null as TipoMovimiento | null, Validators.required],
    valor: [null as number | null, [Validators.required, Validators.min(0.01)]]
  });

  ngOnInit(): void {
    this.cargarMovimientos();
    this.cargarCuentas();
  }

  get movimientosFiltrados(): Movimiento[] {
    const termino = this.filtro.trim().toLowerCase();
    if (!termino) {
      return this.movimientos;
    }
    return this.movimientos.filter(m =>
      m.numeroCuenta.toLowerCase().includes(termino) ||
      m.clienteNombre.toLowerCase().includes(termino)
    );
  }

  alBuscar(evento: Event): void {
    this.filtro = (evento.target as HTMLInputElement).value;
  }

  nuevo(): void {
    this.formulario.reset({ numeroCuenta: '', tipoMovimiento: null, valor: null });
    this.mensajeExito = '';
    this.mensajeError = '';
    this.mostrarFormulario = true;
  }

  cancelar(): void {
    this.mostrarFormulario = false;
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const valores = this.formulario.getRawValue();
    const request: MovimientoRequest = {
      numeroCuenta: valores.numeroCuenta ?? '',
      tipoMovimiento: valores.tipoMovimiento,
      // Siempre positivo: el backend aplica el signo según el tipo
      valor: Math.abs(Number(valores.valor))
    };
    this.movimientoService.registrar(request).subscribe({
      next: () => {
        this.mensajeExito = 'Movimiento registrado correctamente';
        this.mensajeError = '';
        this.mostrarFormulario = false;
        this.cargarMovimientos();
      },
      error: (err: HttpErrorResponse) => {
        const cuerpo: RespuestaError | undefined = err.error;
        this.mensajeError = cuerpo?.mensaje ?? 'No se pudo registrar el movimiento';
        this.mensajeExito = '';
      }
    });
  }

  eliminar(movimiento: Movimiento): void {
    if (!confirm(`¿Está seguro de eliminar el movimiento de la cuenta ${movimiento.numeroCuenta}?`)) {
      return;
    }
    this.movimientoService.eliminar(movimiento.id).subscribe({
      next: () => {
        this.mensajeExito = '';
        this.mensajeError = '';
        this.cargarMovimientos();
      },
      error: (err: HttpErrorResponse) => {
        const cuerpo: RespuestaError | undefined = err.error;
        this.mensajeError = cuerpo?.mensaje ?? 'No se pudo eliminar el movimiento';
      }
    });
  }

  campoInvalido(nombre: string): boolean {
    const control = this.formulario.get(nombre);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  etiquetaTipo(tipo: TipoMovimiento): string {
    return tipo === 'CREDITO' ? 'Depósito' : 'Retiro';
  }

  private cargarMovimientos(): void {
    this.movimientoService.listar().subscribe({
      next: movimientos => this.movimientos = movimientos,
      error: () => this.mensajeError = 'No se pudo cargar la lista de movimientos'
    });
  }

  private cargarCuentas(): void {
    this.cuentaService.listar().subscribe({
      next: cuentas => this.cuentas = cuentas,
      error: () => this.mensajeError = 'No se pudo cargar la lista de cuentas'
    });
  }
}
