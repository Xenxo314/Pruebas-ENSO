package coche;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
public class Coche {

	private ArrayList<Usuario> pasajeros;
	private ArrayList<Cancion> canciones;
	
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
	
	@Override
	public String toString() {
		return "Coche [pasajeros=" + pasajeros + ", canciones=" + canciones + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(canciones, pasajeros);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coche other = (Coche) obj;
		return Objects.equals(canciones, other.canciones) && Objects.equals(pasajeros, other.pasajeros);
	}

	// MÉTODOS AUXILIARES
	public Usuario buscarUsuario(String nombre, String apellido) {
	    for (Usuario usuario : pasajeros) {
	        if (usuario.getNombre().equals(nombre) && usuario.getApellido().equals(apellido)) {
	            return usuario;
	        }
	    }
	    return null;
	}

	
	public void agregarUsuario(Usuario usuario) {
        pasajeros.add(usuario);  
    }
	
	// MÉTODOS
	////////////////////////////////////////////////////////////////
	public void leer_csv(String rutaArchivo) throws IOException, NumberFormatException {
	    String linea;
	    String separador = ",";

	    try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
	        br.readLine(); // Primera línea del csv
	        while ((linea = br.readLine()) != null) {
	            String[] valores = linea.split(separador);

	            if (valores.length != 7) {
	                throw new IOException("Formato incorrecto: se esperaban 7 campos por línea.");
	            }

	            try {
	                int id = Integer.parseInt(valores[0]);
	                String nombreUsuario = valores[1];
	                String apellidoUsuario = valores[2];
	                String nombreArtista = valores[3];
	                String nombreAlbum = valores[4];
	                String titulo = valores[5];
	                String duracion = valores[6];

	                String[] partes = duracion.split(":");
	                if (partes.length != 2) {
	                    throw new NumberFormatException("Formato de duración incorrecto: " + duracion);
	                }

	                int minutos = Integer.parseInt(partes[0]);
	                int segundos = Integer.parseInt(partes[1]);
	                int duracionEnSegundos = minutos * 60 + segundos;

	                // Buscar o crear el usuario
	                Usuario usuario = buscarUsuario(nombreUsuario, apellidoUsuario);
	                if (usuario == null) {
	                    usuario = new Usuario(nombreUsuario, apellidoUsuario);
	                    agregarUsuario(usuario);
	                }

	                // Crear o buscar Artista
	                Artista artista = new Artista(nombreArtista);

	                // Crear o buscar Album
	                Album album = usuario.buscarAlbum(nombreAlbum);
	                if (album == null) {
	                    album = new Album(nombreAlbum, artista);
	                    usuario.agregarAlbum(album);
	                }

	                // Crear canción
	                Cancion cancion = new Cancion(id, titulo, album, artista, duracionEnSegundos);
	                usuario.agregarCancion(cancion);

	            } catch (NumberFormatException e) {
	                throw new NumberFormatException("Error al convertir un campo numérico o duración: " + e.getMessage());
	            }
	        }
	    }
	}

	
 /**
 * Reproduce una canción de cada usuario de forma cíclica.
 * Si un usuario no tiene canciones, se omite.
 * 
 * @return Lista de canciones reproducidas en una vuelta completa.
 */
    public void reproducirCancionDeCadaUsuario() {

        for (Usuario usuario : pasajeros) {
            Cancion cancion = usuario.obtenerSiguienteCancion();
            if (cancion != null) {
                canciones.add(cancion);
            }
        }
    }
    
    /**
     * Reproduce canciones de los usuarios hasta alcanzar un tiempo máximo.
     * 
     * @param tiempoMaximoSegundos Tiempo máximo de reproducción en segundos.
     * @return Lista de canciones reproducidas hasta alcanzar el tiempo máximo.
     */
    public void reproducirHastaTiempo(int tiempoMaximoSegundos) {
        int tiempoAcumulado = 0;
        int cancionesVisitadas = 0;

        // Calcular el total de canciones en todos los usuarios
        int totalCanciones = 0;
        
        for (Usuario pasajero : pasajeros) {
        	totalCanciones += pasajero.getNumeroCanciones();
        }
        
        while (cancionesVisitadas < totalCanciones) {

            for (Usuario usuario : pasajeros) {
                Cancion cancion = usuario.obtenerSiguienteCancion();

                if (cancion == null) {
                    continue;  // Saltar usuarios sin canciones y si ya se ha añadido la canción a la playlist
                }

                cancionesVisitadas++;
                int nuevaDuracion = tiempoAcumulado + cancion.getDuracion();

                if (nuevaDuracion <= tiempoMaximoSegundos) {
                    canciones.add(cancion);
                    tiempoAcumulado = nuevaDuracion;
                }
            }

        }
    }

    ////////////////////////////////////////////////////////////////

	public void randomizarPlayList(){
		Collections.shuffle(canciones);
	}

    ////////////////////////////////////////////////////////////////
    
	public void randomizarArtista(Artista autor) {
		
		// Filtrar canciones que no corresponden al artista
	    canciones.removeIf(cancion -> !cancion.getArtista().equals(autor));
		Collections.shuffle(canciones);
		
	}
	
	////////////////////////////////////////////////////////////////
	
	/**
	 * Crea una playlist con los usuarios especificados y canciones con duración menor a los minutos dados.
	 * 
	 * @param nombresUsuarios Lista de nombres y apellidos de los usuarios (Ej: "Juan Pérez").
	 * @param duracionMaxMinutos Duración máxima de las canciones en minutos.
	 */
	public void crearPlaylistPersonalizada(List<String> nombresUsuarios, int duracionMaxMinutos) {
	    int duracionMaxSegundos = duracionMaxMinutos * 60;

	    for (String nombreCompleto : nombresUsuarios) {
	    	
	        String[] partes = nombreCompleto.split(" ");
	        String nombre = partes[0];
	        String apellido = partes.length > 1 ? partes[1] : "";

	        Usuario usuario = buscarUsuario(nombre, apellido);

	        if (usuario != null) {
	            for (Album album : usuario.getListaAlbumes()) {
	                for (Cancion cancion : album.getListaCanciones()) {
	                    if (cancion.getDuracion() <= duracionMaxSegundos && !canciones.contains(cancion)) {
	                    	canciones.add(cancion);
	                    }
	                }
	            }
	        }
	    }
	}



}
