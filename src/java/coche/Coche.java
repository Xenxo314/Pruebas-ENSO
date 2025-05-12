package coche;

import java.util.*;
import java.io.*;
public class Coche {

	private ArrayList<Usuario> pasajeros;
	private ArrayList<Cancion> canciones;
	
	// Constructor con argumentos
	public Coche(ArrayList<Usuario> pasajeros, ArrayList<Cancion> canciones) {
		super();
		this.pasajeros = pasajeros;
		this.canciones = canciones;
	}
	
	// Constructor  sin argumentos
	public Coche() {
		pasajeros = new ArrayList<Usuario>();
		canciones = new ArrayList<Cancion>();
	}
	
	// GETTERS
	public ArrayList<Usuario> getPasajeros() {
        return pasajeros;
    }
	public ArrayList<Cancion> getCanciones() {
		return canciones;
	}

	// SETTERS
	public void setCanciones(ArrayList<Cancion> canciones) {
		this.canciones = canciones;
	}
	public void setPasajeros(ArrayList<Usuario> pasajeros) {
		this.pasajeros = pasajeros;
	}

	// MÉTODOS AUXILIARES
	private Usuario buscarUsuario(String nombreUsuario) {
        for (Usuario usuario : pasajeros) {
            if (usuario.getNombre().equals(nombreUsuario)) {
                return usuario;
            }
        }
        return null;
    }
	
	public void agregarUsuario(Usuario usuario) {
        pasajeros.add(usuario);
    }
	
	// MÉTODOS
	public void leer_csv(String rutaArchivo) {
        String linea;
        String separador = ",";
        
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
        	br.readLine(); // Primera línea del csv
            while ((linea = br.readLine()) != null) {
                String[] valores = linea.split(separador);
                if (valores.length == 7) {
                	int id = Integer.parseInt(valores[0]);
                    String nombreUsuario = valores[1];
                    String apellidoUsuario = valores[2];
                    String artista = valores[3];
                    String album = valores[4];
                    String titulo = valores[5];
                    String duracion = valores[6];
                    
                    String[] partes = duracion.split(":");
                    int minutos = Integer.parseInt(partes[0]);
                    int segundos = Integer.parseInt(partes[1]);
                    int duracionEnSegundos = minutos * 60 + segundos;

                    // Crear canción
                    Cancion cancion = new Cancion(id, titulo, album, artista, duracionEnSegundos);
                    
                    //System.out.println(cancion);

                    // Buscar o crear el usuario
                    Usuario usuario = buscarUsuario(nombreUsuario);
                    if (usuario == null) {
                        usuario = new Usuario(nombreUsuario, apellidoUsuario);
                        agregarUsuario(usuario);
                    }
                    
                    //System.out.println(usuario);

                    // Agregar la canción al usuario
                    usuario.agregarCancion(cancion);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Error al convertir la duración a un entero.");
        }
    }
	
 /**
 * Reproduce una canción de cada usuario de forma cíclica.
 * Si un usuario no tiene canciones, se omite.
 * 
 * @return Lista de canciones reproducidas en una vuelta completa.
 */
    public List<Cancion> reproducirCancionDeCadaUsuario() {
        List<Cancion> cancionesReproducidas = new ArrayList<>();

        for (Usuario usuario : pasajeros) {
            Cancion cancion = usuario.obtenerSiguienteCancion();
            if (cancion != null) {
                cancionesReproducidas.add(cancion);
            }
        }

        return cancionesReproducidas;
    }
    
    /**
     * Reproduce canciones de los usuarios hasta alcanzar un tiempo máximo.
     * 
     * @param tiempoMaximoSegundos Tiempo máximo de reproducción en segundos.
     * @return Lista de canciones reproducidas hasta alcanzar el tiempo máximo.
     */
    public List<Cancion> reproducirHastaTiempo(int tiempoMaximoSegundos) {
        List<Cancion> cancionesReproducidas = new ArrayList<>();
        int tiempoAcumulado = 0;

        while (tiempoAcumulado < tiempoMaximoSegundos) {
            List<Cancion> cancionesVuelta = reproducirCancionDeCadaUsuario();

            for (Cancion cancion : cancionesVuelta) {
            	cancionesReproducidas.add(cancion);
                tiempoAcumulado += cancion.getDuracion();
                if (tiempoAcumulado <= tiempoMaximoSegundos) {
                    continue;
                } else {
                    // Si la canción actual supera el tiempo disponible, salimos
                    return cancionesReproducidas;
                }
            }
        }

        return cancionesReproducidas;
    }
	
	
}
