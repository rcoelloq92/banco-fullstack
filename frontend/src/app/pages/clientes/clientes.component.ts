import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Cliente, ClienteRequest } from '../../models/cliente.model';
import { RespuestaError } from '../../models/respuesta-error.model';
import { ClienteService } from '../../services/cliente.service';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './clientes.component.html',
  styleUrl: './clientes.component.css'
})
export class ClientesComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly clienteService = inject(ClienteService);

  clientes: Cliente[] = [];
  clienteEnEdicion: Cliente | null = null;
  formularioVisible = false;
  mensajeExito = '';
  mensajeError = '';
  detallesError: string[] = [];

  controlBusqueda = new FormControl('', { nonNullable: true });

  formulario = this.fb.group({
    nombre: ['', Validators.required],
    genero: ['', Validators.required],
    edad: [null as number | null, [Validators.required, Validators.min(18)]],
    identificacion: ['', Validators.required],
    direccion: [''],
    telefono: [''],
    clienteId: ['', Validators.required],
    contrasena: ['', Validators.required],
    estado: [true]
  });

  ngOnInit(): void {
    this.cargarClientes();
  }

  /** Lista filtrada en memoria por nombre, identificación o clienteId. */
  get filtrados(): Cliente[] {
    const texto = this.controlBusqueda.value.trim().toLowerCase();
    if (!texto) {
      return this.clientes;
    }
    return this.clientes.filter(cliente =>
      cliente.nombre.toLowerCase().includes(texto) ||
      cliente.identificacion.toLowerCase().includes(texto) ||
      cliente.clienteId.toLowerCase().includes(texto)
    );
  }

  cargarClientes(): void {
    this.clienteService.listar().subscribe({
      next: clientes => this.clientes = clientes,
      error: err => this.mostrarError(err)
    });
  }

  nuevo(): void {
    this.clienteEnEdicion = null;
    this.reiniciarFormulario();
    this.formularioVisible = true;
    this.limpiarMensajes();
  }

  editar(cliente: Cliente): void {
    this.clienteEnEdicion = cliente;
    this.reiniciarFormulario();
    // La contraseña no viaja en la respuesta del backend: se exige reingresarla.
    this.formulario.patchValue({
      nombre: cliente.nombre,
      genero: cliente.genero,
      edad: cliente.edad,
      identificacion: cliente.identificacion,
      direccion: cliente.direccion,
      telefono: cliente.telefono,
      clienteId: cliente.clienteId,
      estado: cliente.estado
    });
    this.formularioVisible = true;
    this.limpiarMensajes();
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.limpiarMensajes();

    const request = this.formulario.getRawValue() as ClienteRequest;
    const esEdicion = this.clienteEnEdicion !== null;
    const peticion = esEdicion
      ? this.clienteService.actualizar(this.clienteEnEdicion!.id, request)
      : this.clienteService.crear(request);

    peticion.subscribe({
      next: () => {
        this.mensajeExito = esEdicion
          ? 'Cliente actualizado correctamente'
          : 'Cliente creado correctamente';
        this.cerrarFormulario();
        this.cargarClientes();
      },
      error: err => this.mostrarError(err)
    });
  }

  eliminar(cliente: Cliente): void {
    if (!confirm(`¿Desea eliminar al cliente ${cliente.nombre}?`)) {
      return;
    }
    this.limpiarMensajes();
    this.clienteService.eliminar(cliente.id).subscribe({
      next: () => {
        this.mensajeExito = 'Cliente eliminado correctamente';
        this.cargarClientes();
      },
      error: err => this.mostrarError(err)
    });
  }

  cancelar(): void {
    this.cerrarFormulario();
    this.limpiarMensajes();
  }

  campoInvalido(nombre: string): boolean {
    const control = this.formulario.get(nombre);
    return !!control && control.touched && control.invalid;
  }

  tieneError(nombre: string, error: string): boolean {
    return !!this.formulario.get(nombre)?.hasError(error);
  }

  private reiniciarFormulario(): void {
    this.formulario.reset({
      nombre: '',
      genero: '',
      edad: null,
      identificacion: '',
      direccion: '',
      telefono: '',
      clienteId: '',
      contrasena: '',
      estado: true
    });
  }

  private cerrarFormulario(): void {
    this.formularioVisible = false;
    this.clienteEnEdicion = null;
  }

  private limpiarMensajes(): void {
    this.mensajeExito = '';
    this.mensajeError = '';
    this.detallesError = [];
  }

  private mostrarError(err: { error?: RespuestaError }): void {
    this.mensajeError = err.error?.mensaje ?? 'Ocurrió un error inesperado';
    this.detallesError = err.error?.detalles ?? [];
  }
}
