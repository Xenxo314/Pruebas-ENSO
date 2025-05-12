package enso.coche;

public class Cancion {

	private int id;
	private String titulo;
	private String album;
	private String artista;
	private int duracion;
	
	public Cancion(int id, String titulo, String album, String artista, int duracion) {
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

	// GETTERS
	public int getId() {
		return id;
	}
	public String getTitulo() {
		return titulo;
	}
	public String getAlbum() {
		return album;
	}
	public String getArtista() {
		return artista;
	}
	public int getDuracion() {
		return duracion;
	}
	
	// SETTERS
	public void setId(int id) {
		this.id = id;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public void setArtista(String artista) {
		this.artista = artista;
	}
	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}
	
	
	

}
