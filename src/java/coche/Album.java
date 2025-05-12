package coche;

import java.util.Objects;

public class Album {

	private Artista artista;
	private String nombre;

	public Album() {
		super();
		artista = new Artista();
		nombre = new String();
	}

	public Album(Artista artista, String nombre) {
		super();
		this.artista = artista;
		this.nombre = nombre;
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
		return Objects.equals(artista, other.artista) && Objects.equals(nombre, other.nombre);
	}

	@Override
	public String toString() {
		return "Album [artista=" + artista + ", nombre=" + nombre + "]";
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

	

	
	
	
}
