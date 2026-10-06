import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Movimiento, MovimientoRequest } from '../models/movimiento.model';

@Injectable({ providedIn: 'root' })
export class MovimientoService {

  private readonly url = '/api/movimientos';

  constructor(private http: HttpClient) {}

  listar(): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(this.url);
  }

  registrar(request: MovimientoRequest): Observable<Movimiento> {
    return this.http.post<Movimiento>(this.url, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
