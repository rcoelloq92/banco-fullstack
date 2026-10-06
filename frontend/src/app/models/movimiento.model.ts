export type TipoMovimiento = 'CREDITO' | 'DEBITO';

export interface Movimiento {
  id: number;
  fecha: string;
  tipoMovimiento: TipoMovimiento;
  valor: number;
  saldo: number;
  numeroCuenta: string;
  clienteNombre: string;
}

export interface MovimientoRequest {
  numeroCuenta: string;
  tipoMovimiento: TipoMovimiento | null;
  valor: number | null;
}
