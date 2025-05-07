import java.util.*;
import java.io.*;
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
	
	public void leer_csv(String rutaArchivo) {
		String linea;
        String separador = ","; // Puedes cambiarlo si tu CSV usa otro separador

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            while ((linea = br.readLine()) != null) {
                String[] valores = linea.split(separador);
                // Aquí puedes hacer algo con los valores
                for (String valor : valores) {
                    System.out.print(valor + " ");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	
}
