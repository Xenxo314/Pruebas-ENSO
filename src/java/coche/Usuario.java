package coche;

import java.util.*;
import java.io.*;
public class Usuario {

	private String nombre;
	private String apellido;
    private List<Album> listaAlbumes;
    private int indiceAlbumActual;
    private int indiceCancionActual;
    
    private int numCancionesUsuario;

    public Usuario(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.listaAlbumes = new ArrayList<Album>();
        this.indiceAlbumActual = 0;
        this.indiceCancionActual = 0;
        this.numCancionesUsuario = 0;
    }

	// GETTERS	
	public String getNombre() {
		return nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public List<Album> getListaAlbumes() {
		return listaAlbumes;
	}

	
	
	
	
	

	@Override
	public String toString() {
		return "Usuario [nombre=" + nombre + ", apellido=" + apellido + "]";
	}

	

	@Override
	public int hashCode() {
		return Objects.hash(apellido, nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(apellido, other.apellido) && Objects.equals(nombre, other.nombre);
	}

	// FUNCIONES AUXILIARES	
	public void agregarCancion(Cancion cancion) {
		cancion.getAlbum().agregarCancion(cancion);
		numCancionesUsuario++;
	}
	public Album buscarAlbum(String nombreAlbum) {
        for (Album album : listaAlbumes) {
            if (album.getNombre().equals(nombreAlbum)) {
                return album;
            }
        }
        return null;
    }
	
	public int getNumeroCanciones() {
	    int totalCanciones = 0;

	    for (Album album : listaAlbumes) {
	        totalCanciones += album.getListaCanciones().size();
	    }

	    return totalCanciones;
	}

	
	public void agregarAlbum(Album album) {
        listaAlbumes.add(album);
    }
	
	// FUNCIONES
	public Cancion obtenerSiguienteCancion() {
	    if (listaAlbumes.isEmpty()) return null;
	    
	    if (numCancionesUsuario == 0) return null;
	   
	    while (true) {
	        Album albumActual = listaAlbumes.get(indiceAlbumActual);
	        List<Cancion> cancionesAlbum = albumActual.getListaCanciones();
	        // Si el álbum actual tiene canciones disponibles
	        if (!cancionesAlbum.isEmpty()) {
	            // Si aún quedan canciones en el álbum actual
	            if (indiceCancionActual < cancionesAlbum.size()) {

		        	
	                Cancion cancion = cancionesAlbum.get(indiceCancionActual);
	                
	                indiceCancionActual++;

	                // Si alcanzamos el final del álbum, pasamos al siguiente álbum
	                if (indiceCancionActual >= cancionesAlbum.size()) {
	                    indiceCancionActual = 0;
	                    indiceAlbumActual++;

	                    // Si hemos recorrido todos los álbumes, volvemos al primero
	                    if (indiceAlbumActual >= listaAlbumes.size()) {
	                        indiceAlbumActual = 0;
	                    }
	                }
	                return cancion;
	            }
	        }
	        
	        // Si el álbum actual no tiene canciones, pasamos al siguiente álbum
	        indiceAlbumActual++;
	        indiceCancionActual = 0;
	        
	        

	        // Si llegamos al final de los álbumes, volvemos al primero
	        if (indiceAlbumActual >= listaAlbumes.size()) {
	            indiceAlbumActual = 0;
	        }
	    }
	}
}
