import java.util.Objects;

public class Cancion {

	private String titulo;
	private String album;
	private String artista;
	private int duracion;
	
	public Cancion(String titulo, String album, String artista, int duracion) {
		super();
		this.titulo = titulo;
		this.album = album;
		this.artista = artista;
		this.duracion = duracion;
	}
	
	

	@Override
	public String toString() {
		return "Cancion [titulo=" + titulo + ", album=" + album + ", artista=" + artista + ", duracion=" + duracion
				+ "]";
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
	
	
	
}
