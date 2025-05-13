package coche;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Album {

	private Artista artista;
	private String nombre;
	 private List<Cancion> listaCanciones;

	public Album() {
		super();
		artista = new Artista();
		nombre = new String();
		listaCanciones = new ArrayList<Cancion>();
	}

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

	public Artista getArtista() {
		return artista;
	}

	public void setArtista(Artista artista) {
		this.artista = artista;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Cancion> getListaCanciones() {
		return listaCanciones;
	}

	public void setListaCanciones(List<Cancion> listaCanciones) {
		this.listaCanciones = listaCanciones;
	}
	
	// MÉTODOS AUXILIARES
	public void agregarCancion (Cancion cancion) {
		listaCanciones.add(cancion);
		
	}
	
	




	

	
	
	
}
