package enso.coche;

import java.util.*;
import java.io.*;
public class Usuario {

	private String nombre;
	private String apellido;
	
	private Map<String, List<Cancion>> cancionesPorAlbum;
    private List<String> listaAlbumes;
    private int indiceAlbumActual;
    private int indiceCancionActual;

    public Usuario(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cancionesPorAlbum = new LinkedHashMap<>();
        this.listaAlbumes = new ArrayList<>();
        this.indiceAlbumActual = 0;
        this.indiceCancionActual = 0;
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
	
	@Override
    public String toString() {
        return "Usuario: " + nombre + " " + apellido + ", Álbumes: " + listaAlbumes;
    }
	
	// GETTERS	
	public String getNombre() {
		return nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public Map<String, List<Cancion>> getCancionesPorAlbum() {
		return cancionesPorAlbum;
	}
	public List<String> getListaAlbumes() {
		return listaAlbumes;
	}
	public int getIndiceAlbumActual() {
		return indiceAlbumActual;
	}
	public int getIndiceCancionActual() {
		return indiceCancionActual;
	}
	
	// SETTERS
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public void setCancionesPorAlbum(Map<String, List<Cancion>> cancionesPorAlbum) {
		this.cancionesPorAlbum = cancionesPorAlbum;
	}
	public void setListaAlbumes(List<String> listaAlbumes) {
		this.listaAlbumes = listaAlbumes;
	}
	public void setIndiceAlbumActual(int indiceAlbumActual) {
		this.indiceAlbumActual = indiceAlbumActual;
	}
	public void setIndiceCancionActual(int indiceCancionActual) {
		this.indiceCancionActual = indiceCancionActual;
	}

	// FUNCIONES AUXILIARES
	public void agregarCancion(Cancion cancion) {
        String album = cancion.getAlbum();
        cancionesPorAlbum.putIfAbsent(album, new ArrayList<>());
        cancionesPorAlbum.get(album).add(cancion);

        // Si el álbum es nuevo, lo añadimos a la lista de álbumes
        if (!listaAlbumes.contains(album)) {
            listaAlbumes.add(album);
        }
    }
	
	// FUNCIONES
    public Cancion obtenerSiguienteCancion() {
        if (listaAlbumes.isEmpty()) {
            return null; // No hay canciones
        }

        String albumActual = listaAlbumes.get(indiceAlbumActual);
        List<Cancion> cancionesAlbum = cancionesPorAlbum.get(albumActual);

        // Obtener la canción actual
        Cancion cancion = cancionesAlbum.get(indiceCancionActual);

        // Avanzar al siguiente índice de canción
        indiceCancionActual++;

        // Si llegamos al final del álbum, pasamos al siguiente álbum
        if (indiceCancionActual >= cancionesAlbum.size()) {
            indiceCancionActual = 0;
            indiceAlbumActual++;

            // Si llegamos al final de todos los álbumes, volvemos al primero
            if (indiceAlbumActual >= listaAlbumes.size()) {
                indiceAlbumActual = 0;
            }
        }

        return cancion;
    }

	
	
}
