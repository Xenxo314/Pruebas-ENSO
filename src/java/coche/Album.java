package coche;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Album {

	private Artista artista;
	private String nombre;
	 private List<Cancion> listaCanciones;

	public Album(String nombre, Artista artista) {
		super();
		this.artista = artista;
		this.nombre = nombre;
		listaCanciones = new ArrayList<Cancion>();
	}

	@Override
	public int hashCode() {
		return Objects.hash(artista, nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Album other = (Album) obj;
		return Objects.equals(artista, other.artista) && Objects.equals(listaCanciones, other.listaCanciones)
				&& Objects.equals(nombre, other.nombre);
	}

	public String getNombre() {
		return nombre;
	}


	public List<Cancion> getListaCanciones() {
		return listaCanciones;
	}
	
	// MÉTODOS AUXILIARES
	public void agregarCancion (Cancion cancion) {
		listaCanciones.add(cancion);
		
	}
	
	




	

	
	
	
}
