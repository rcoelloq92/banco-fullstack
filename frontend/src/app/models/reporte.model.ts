/**
 * Las filas del reporte replican los nombres de propiedades que entrega la
 * API en el estado de cuenta ("Fecha", "Cliente", etc.).
 */
export interface MovimientoReporte {
  'Fecha': string;
  'Cliente': string;
  'Numero Cuenta': string;
  'Tipo': string;
  'Saldo Inicial': number;
  'Estado': boolean;
  'Movimiento': number;
  'Saldo Disponible': number;
}

export interface CuentaReporte {
  numeroCuenta: string;
  tipoCuenta: string;
  saldoInicial: number;
  estado: boolean;
  totalCreditos: number;
  totalDebitos: number;
  saldoDisponible: number;
}

export interface Reporte {
  cliente: string;
  fechaInicio: string;
  fechaFin: string;
  cuentas: CuentaReporte[];
  movimientos: MovimientoReporte[];
  pdfBase64?: string;
}
