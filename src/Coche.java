import java.util.*;
public class Coche {

	private ArrayList<Usuario> pasajeros;
	private ArrayList<Cancion> canciones;
	public Coche(ArrayList<Usuario> pasajeros, ArrayList<Cancion> canciones) {
		super();
		this.pasajeros = pasajeros;
		this.canciones = canciones;
	}
	
	public Coche() {
		pasajeros = new ArrayList<Usuario>();
		canciones = new ArrayList<Cancion>();
	}
	
	
	
}
