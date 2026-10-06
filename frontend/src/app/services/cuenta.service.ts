import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Cuenta, CuentaRequest } from '../models/cuenta.model';

@Injectable({ providedIn: 'root' })
export class CuentaService {

  private readonly url = '/api/cuentas';

  constructor(private http: HttpClient) {}

  listar(): Observable<Cuenta[]> {
    return this.http.get<Cuenta[]>(this.url);
  }

  obtener(id: number): Observable<Cuenta> {
    return this.http.get<Cuenta>(`${this.url}/${id}`);
  }

  crear(request: CuentaRequest): Observable<Cuenta> {
    return this.http.post<Cuenta>(this.url, request);
  }

  actualizar(id: number, request: CuentaRequest): Observable<Cuenta> {
    return this.http.put<Cuenta>(`${this.url}/${id}`, request);
  }

  actualizarParcial(id: number, cambios: Partial<CuentaRequest>): Observable<Cuenta> {
    return this.http.patch<Cuenta>(`${this.url}/${id}`, cambios);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
