import java.util.*;
import java.io.*;
public class Usuario {

	private String nombre;
	private String apellido;
	
	private ArrayList<Cancion> playlist;

	public Usuario(String nombre, String apellido) {
		super();
		this.nombre = nombre;
		this.apellido = apellido;
		this.playlist = new ArrayList<>();
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

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public ArrayList<Cancion> getPlaylist() {
		return playlist;
	}

	public void setPlaylist(ArrayList<Cancion> playlist) {
		this.playlist = playlist;
	}
	
	
	
}
