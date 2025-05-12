package coche;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CocheTest {
	
	private Coche coche;
    private Usuario usuario1;
    private Usuario usuario2;

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	void initCoche() {
		coche = new Coche();

        usuario1 = new Usuario("Juan", "Pérez");
        usuario1.agregarCancion(new Cancion(1, "Cancion1", "Album1", "Artista1", 180));
        usuario1.agregarCancion(new Cancion(2, "Cancion2", "Album1", "Artista1", 200));

        usuario2 = new Usuario("Ana", "García");
        usuario2.agregarCancion(new Cancion(3, "Cancion3", "Album2", "Artista2", 220));
        usuario2.agregarCancion(new Cancion(4, "Cancion4", "Album3", "Artista3", 240));

        coche.agregarUsuario(usuario1);
        coche.agregarUsuario(usuario2);
	}

	
	/*
	 * Añade 2 usuarios con 2 canciones cada uno. Prueba si al llamar
	 */
    @Test
    void testReproducirCancionDeCadaUsuario_inicializacionDefault() {
    	
    	initCoche();
    	
        List<Cancion> canciones = coche.reproducirCancionDeCadaUsuario();

        assertEquals(2, canciones.size());
        assertEquals("Cancion1", canciones.get(0).getTitulo());
        assertEquals("Cancion3", canciones.get(1).getTitulo());

        canciones = coche.reproducirCancionDeCadaUsuario();
        assertEquals("Cancion2", canciones.get(0).getTitulo());
        assertEquals("Cancion4", canciones.get(1).getTitulo());

        canciones = coche.reproducirCancionDeCadaUsuario();
        assertEquals("Cancion1", canciones.get(0).getTitulo());
        assertEquals("Cancion3", canciones.get(1).getTitulo());
    }
    
    @Test
    void testReproducirCancionDeCadaUsuario_inicializacionCSV() {
    	
    	coche = new Coche();
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
    	
    	coche.leer_csv(rutaArchivo);
    	
        List<Cancion> canciones = coche.reproducirCancionDeCadaUsuario();

        assertEquals(coche.getPasajeros().size(), canciones.size());
        assertEquals("Intro: Persona", canciones.get(0).getTitulo());
        assertEquals("Next to Me", canciones.get(1).getTitulo());
        assertEquals("Future Nostalgia", canciones.get(2).getTitulo());
        assertEquals("Golden", canciones.get(3).getTitulo());
    }

	@Test
	void testLeerCSV() {
        Coche coche = new Coche();
        String rutaArchivo = "src/resources/discos_usuarios.csv";

        coche.leer_csv(rutaArchivo);

        // Comprobación básica: que haya al menos un usuario cargado
        assertFalse(coche.getPasajeros().isEmpty(), "No se ha cargado ningún usuario.");

        // Comprobar que se le han asignado canciones a un usuario
        assertFalse(coche.getPasajeros().get(0).getCancionesPorAlbum().isEmpty(), "El usuario " + coche.getPasajeros().get(0).getNombre() + " no tiene canciones asociadas");
    }
	

    @Test
    void testReproducirHastaTiempo_1hora() {
    	
    	initCoche();
    	
        int tiempoMaximo = 3600;
        int margen = 300; // 5 minutos
        int minimoPermitido = tiempoMaximo - margen;
        int maximoPermitido = tiempoMaximo + margen;
        
        List<Cancion> canciones = coche.reproducirHastaTiempo(tiempoMaximo);

        // Verificar la duración total
        int duracionTotal = canciones.stream().mapToInt(Cancion::getDuracion).sum();

        assertTrue(duracionTotal >= minimoPermitido && duracionTotal <= maximoPermitido,
                "La duración total (" + duracionTotal + "s) está fuera del rango permitido (" + minimoPermitido + "s - " + maximoPermitido + "s).");

        System.out.println(String.format("Duración total: %02d:%02d:%02d", duracionTotal / 3600, (duracionTotal % 3600) / 60, duracionTotal % 60));

        // Verificar el contenido esperado
        assertEquals("Cancion1", canciones.get(0).getTitulo()); // 180s
        assertEquals("Cancion3", canciones.get(1).getTitulo()); // 220s
    }

    @Test
    void testReproducirHastaTiempo_1_5horas() {
    	
    	initCoche();
    	
        int tiempoMaximo = 5400;
        int margen = 300; // 5 minutos
        int minimoPermitido = tiempoMaximo - margen;
        int maximoPermitido = tiempoMaximo + margen;
        
        List<Cancion> canciones = coche.reproducirHastaTiempo(tiempoMaximo);

        // Verificar la duración total
        int duracionTotal = canciones.stream().mapToInt(Cancion::getDuracion).sum();

        assertTrue(duracionTotal >= minimoPermitido && duracionTotal <= maximoPermitido,
                "La duración total (" + duracionTotal + "s) está fuera del rango permitido (" + minimoPermitido + "s - " + maximoPermitido + "s).");

        System.out.println(String.format("Duración total: %02d:%02d:%02d", duracionTotal / 3600, (duracionTotal % 3600) / 60, duracionTotal % 60));

        // Verificar el contenido esperado
        assertEquals("Cancion1", canciones.get(0).getTitulo()); // 180s
        assertEquals("Cancion3", canciones.get(1).getTitulo()); // 220s
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1800, 3600, 5400, 7200})  // Valores de tiempo máximo en segundos
    void testReproducirHastaTiempo_Xsegundos(int tiempoMaximo) {
    	
    	initCoche();
    
        int margen = 300; // 5 minutos
        int minimoPermitido = tiempoMaximo - margen;
        int maximoPermitido = tiempoMaximo + margen;
        
        List<Cancion> canciones = coche.reproducirHastaTiempo(tiempoMaximo);

        // Verificar la duración total
        int duracionTotal = canciones.stream().mapToInt(Cancion::getDuracion).sum();

        assertTrue(duracionTotal >= minimoPermitido && duracionTotal <= maximoPermitido,
                "La duración total (" + duracionTotal + "s) está fuera del rango permitido (" + minimoPermitido + "s - " + maximoPermitido + "s).");

        System.out.println(String.format("Duración total: %02d:%02d:%02d", duracionTotal / 3600, (duracionTotal % 3600) / 60, duracionTotal % 60));

        // Verificar el contenido esperado
        assertEquals("Cancion1", canciones.get(0).getTitulo()); // 180s
        assertEquals("Cancion3", canciones.get(1).getTitulo()); // 220s
    }
	
	

}
