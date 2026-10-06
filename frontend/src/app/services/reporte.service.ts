import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Reporte } from '../models/reporte.model';

@Injectable({ providedIn: 'root' })
export class ReporteService {

  private readonly url = '/api/reportes';

  constructor(private http: HttpClient) {}

  estadoCuenta(clienteId: number, fechaInicio: string, fechaFin: string): Observable<Reporte> {
    return this.http.get<Reporte>(this.url, {
      params: new HttpParams()
        .set('clienteId', clienteId)
        .set('fechaInicio', fechaInicio)
        .set('fechaFin', fechaFin)
    });
  }

  estadoCuentaPdf(clienteId: number, fechaInicio: string, fechaFin: string): Observable<Reporte> {
    return this.http.get<Reporte>(this.url, {
      params: new HttpParams()
        .set('clienteId', clienteId)
        .set('fechaInicio', fechaInicio)
        .set('fechaFin', fechaFin)
        .set('formato', 'pdf')
    });
  }
}
