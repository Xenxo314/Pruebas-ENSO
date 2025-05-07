import java.util.*;
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
	
	
}
