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
        	linea = br.readLine(); // Ignorar la cabecera de las narices
            while ((linea = br.readLine()) != null) {
            	
                String[] valores = linea.split(separador);
                if (valores.length < 7) {
            	    System.out.println("Línea mal formada: " + linea);
            	    continue;
            	}
                Usuario u = new Usuario(valores[1],valores[2]);
                String duracion[] = valores[6].split(":");
                Cancion c = new Cancion(valores[5], valores[4], valores[3],Integer.parseInt(duracion[0])*60 + Integer.parseInt(duracion[1])); 
                
                // Añadimos la cancion a a la playList
                canciones.add(c);
                pasajeros.add(u);
            }

            for (Cancion cancion : canciones) {
				System.out.println(cancion.toString());
			}
        } catch (IOException e) {
            e.printStackTrace(); 
        }
	}
	
	
	
}
