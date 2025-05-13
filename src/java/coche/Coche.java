package coche;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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
	private Usuario buscarUsuario(String nombre, String apellido) {
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
                    String nombreArtista = valores[3];
                    String nombreAlbum = valores[4];
                    String titulo = valores[5];
                    String duracion = valores[6];
                    
                    String[] partes = duracion.split(":");
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
        
        while (true) {
        	for (Usuario usuario : pasajeros) {
                Cancion cancion = usuario.obtenerSiguienteCancion();
                int nuevaDuracion = tiempoAcumulado + cancion.getDuracion();
                if (cancion != null && nuevaDuracion < tiempoMaximoSegundos) {
                    canciones.add(cancion);
                    tiempoAcumulado = nuevaDuracion;
                }
                else
                	return;
            }
        }
    }

	
    ////////////////////////////////////////////////////////////////
    
	public void randomizarPlayList(){
		Collections.shuffle(canciones);
	}

    ////////////////////////////////////////////////////////////////
    
	
	
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
	                    if (cancion.getDuracion() < duracionMaxSegundos) {
	                    	canciones.add(cancion);
	                    }
	                }
	            }
	        }
	    }
	}



}
