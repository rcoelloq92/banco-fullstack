export type TipoCuenta = 'AHORRO' | 'CORRIENTE';

export interface Cuenta {
  id: number;
  numeroCuenta: string;
  tipoCuenta: TipoCuenta;
  saldoInicial: number;
  estado: boolean;
  clienteId: number;
  clienteNombre: string;
}

export interface CuentaRequest {
  numeroCuenta: string;
  tipoCuenta: TipoCuenta | null;
  saldoInicial: number | null;
  estado: boolean;
  clienteId: number | null;
}
