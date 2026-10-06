import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Cliente } from '../../models/cliente.model';
import { Reporte } from '../../models/reporte.model';
import { ClienteService } from '../../services/cliente.service';
import { ReporteService } from '../../services/reporte.service';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reportes.component.html',
  styleUrl: './reportes.component.css'
})
export class ReportesComponent implements OnInit {

  clientes: Cliente[] = [];
  reporte: Reporte | null = null;
  mensajeError = '';
  cargando = false;

  filtros = this.fb.group({
    clienteId: ['', Validators.required],
    fechaInicio: ['', Validators.required],
    fechaFin: ['', Validators.required]
  });

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private reporteService: ReporteService
  ) {}

  ngOnInit(): void {
    this.clienteService.listar().subscribe({
      next: clientes => this.clientes = clientes,
      error: () => this.mensajeError = 'No se pudieron cargar los clientes'
    });
  }

  buscar(): void {
    if (!this.validarFiltros()) {
      return;
    }
    const { clienteId, fechaInicio, fechaFin } = this.filtros.getRawValue();
    this.cargando = true;
    this.reporteService.estadoCuenta(Number(clienteId), fechaInicio!, fechaFin!).subscribe({
      next: reporte => {
        this.reporte = reporte;
        this.cargando = false;
      },
      error: () => {
        this.mensajeError = 'No se pudo generar el reporte';
        this.cargando = false;
      }
    });
  }

  descargarPdf(): void {
    if (!this.validarFiltros()) {
      return;
    }
    const { clienteId, fechaInicio, fechaFin } = this.filtros.getRawValue();
    this.cargando = true;
    this.reporteService.estadoCuentaPdf(Number(clienteId), fechaInicio!, fechaFin!).subscribe({
      next: reporte => {
        this.cargando = false;
        if (!reporte.pdfBase64) {
          this.mensajeError = 'El reporte no incluye el PDF';
          return;
        }
        this.descargarArchivo(reporte.pdfBase64);
      },
      error: () => {
        this.mensajeError = 'No se pudo descargar el PDF';
        this.cargando = false;
      }
    });
  }

  private validarFiltros(): boolean {
    this.mensajeError = '';
    if (this.filtros.invalid) {
      this.mensajeError = 'Debe seleccionar cliente, fecha inicio y fecha fin';
      return false;
    }
    const { fechaInicio, fechaFin } = this.filtros.getRawValue();
    if (fechaFin! < fechaInicio!) {
      this.mensajeError = 'La fecha fin debe ser mayor o igual a la fecha inicio';
      return false;
    }
    return true;
  }

  private descargarArchivo(pdfBase64: string): void {
    const binario = atob(pdfBase64);
    const bytes = new Uint8Array(binario.length);
    for (let i = 0; i < binario.length; i++) {
      bytes[i] = binario.charCodeAt(i);
    }
    const blob = new Blob([bytes], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);
    const enlace = document.createElement('a');
    enlace.href = url;
    enlace.download = 'estado-cuenta.pdf';
    enlace.click();
    URL.revokeObjectURL(url);
  }
}
