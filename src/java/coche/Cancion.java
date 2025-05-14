package coche;

import java.util.Objects;

public class Cancion {

	private int id;
	private String titulo;
	private Album album;
	private Artista artista;
	private int duracion;
	
	public Cancion(int id, String titulo, Album album, Artista artista, int duracion) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.album = album;
		this.artista = artista;
		this.duracion = duracion;
	}
	
	@Override
    public String toString() {
        return "Id: " + id + "Título: " + titulo + ", Álbum: " + album + ", Artista: " + artista + ", Duración: " + duracion + "s";
    }


	@Override
	public int hashCode() {
		return Objects.hash(album, titulo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cancion other = (Cancion) obj;
		return Objects.equals(album, other.album) && Objects.equals(titulo, other.titulo);
	}

	// GETTERS
	public String getTitulo() {
		return titulo;
	}
	public Album getAlbum() {
		return album;
	}
	public Artista getArtista() {
		return artista;
	}
	public int getDuracion() {
		return duracion;
	}
	
	
	
	

}
