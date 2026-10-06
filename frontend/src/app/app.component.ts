import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {

  readonly opcionesMenu = [
    { ruta: '/clientes', etiqueta: 'Clientes' },
    { ruta: '/cuentas', etiqueta: 'Cuentas' },
    { ruta: '/movimientos', etiqueta: 'Movimientos' },
    { ruta: '/reportes', etiqueta: 'Reportes' }
  ];
}
