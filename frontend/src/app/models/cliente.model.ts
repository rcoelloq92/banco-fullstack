export interface Cliente {
  id: number;
  clienteId: string;
  nombre: string;
  genero: string;
  edad: number;
  identificacion: string;
  direccion: string;
  telefono: string;
  estado: boolean;
}

export interface ClienteRequest {
  nombre: string;
  genero: string;
  edad: number | null;
  identificacion: string;
  direccion: string;
  telefono: string;
  clienteId: string;
  contrasena: string;
  estado: boolean;
}
