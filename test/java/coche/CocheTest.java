package coche;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CocheTest {
	
	private Coche coche;
    private Usuario usuario1;
    private Usuario usuario2;
	
	@BeforeEach
	void setUp() throws Exception {
		coche = new Coche();
	}

	void initCoche() {

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
    @DisplayName("Reproducir playlists para todos los pasajeros default")
    void testPlaylistDefault() {
    	
    	// Arrange
    	initCoche();
    	
    	// Act
        List<Cancion> canciones = coche.reproducirCancionDeCadaUsuario();

        // Asserts
        assertEquals(2, canciones.size());
        assertEquals("Cancion1", canciones.get(0).getTitulo());
        assertEquals("Cancion3", canciones.get(1).getTitulo());

        // Act
        canciones = coche.reproducirCancionDeCadaUsuario();
        // Asserts
        assertEquals("Cancion2", canciones.get(0).getTitulo());
        assertEquals("Cancion4", canciones.get(1).getTitulo());

        // Act
        canciones = coche.reproducirCancionDeCadaUsuario();
        // Asserts
        assertEquals("Cancion1", canciones.get(0).getTitulo());
        assertEquals("Cancion3", canciones.get(1).getTitulo());
    }
    
    @Test
    @DisplayName("Reproducir playlists para todos los pasajeros del csv")
    void testPlaylistCsv() {
    	
    	// Arrange
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
    	
    	// Act
    	coche.leer_csv(rutaArchivo);
    	
        List<Cancion> canciones = coche.reproducirCancionDeCadaUsuario();

        // Asserts
        assertEquals(coche.getPasajeros().size(), canciones.size());
        assertEquals("Intro: Persona", canciones.get(0).getTitulo());
        assertEquals("Next to Me", canciones.get(1).getTitulo());
        assertEquals("Future Nostalgia", canciones.get(2).getTitulo());
        assertEquals("Golden", canciones.get(3).getTitulo());
    }

	@Test
	@DisplayName("Lectura de los datos del csv")
	void testLeerCSV() {
        
		// Arrange
        String rutaArchivo = "src/resources/discos_usuarios.csv";

        // Act
        coche.leer_csv(rutaArchivo);

        // Asserts
        // Comprobación básica: que haya al menos un usuario cargado
        assertFalse(coche.getPasajeros().isEmpty(), "No se ha cargado ningún usuario.");

        // Comprobar que se le han asignado canciones a un usuario
        assertFalse(coche.getPasajeros().get(0).getCancionesPorAlbum().isEmpty(), "El usuario " + coche.getPasajeros().get(0).getNombre() + " no tiene canciones asociadas");
    }
	

    @Test
    @DisplayName("Creacion de Playlist de 1 hora con los datos del csv")
    void testPlaylist1h() {
    	
    	// Arrange
    	initCoche();
    	
        int tiempoMaximo = 3600;
        int margen = 300; // 5 minutos
        int minimoPermitido = tiempoMaximo - margen;
        int maximoPermitido = tiempoMaximo + margen;
        
        // Act
        List<Cancion> canciones = coche.reproducirHastaTiempo(tiempoMaximo);

        // Verificar la duración total
        int duracionTotal = canciones.stream().mapToInt(Cancion::getDuracion).sum();

        // Asserts
        assertTrue(duracionTotal >= minimoPermitido && duracionTotal <= maximoPermitido,
                "La duración total (" + duracionTotal + "s) está fuera del rango permitido (" + minimoPermitido + "s - " + maximoPermitido + "s).");

        //System.out.println(String.format("Duración total: %02d:%02d:%02d", duracionTotal / 3600, (duracionTotal % 3600) / 60, duracionTotal % 60));

        // Verificar el contenido esperado
        assertEquals("Cancion1", canciones.get(0).getTitulo()); // 180s
        assertEquals("Cancion3", canciones.get(1).getTitulo()); // 220s
    }

    @Test
    @DisplayName("Creacion de Playlist de 1 hora y media con los datos del csv")
    void testPlaylist1_5h() {
    	
    	// Arrange
    	initCoche();
    	
        int tiempoMaximo = 5400;
        int margen = 300; // 5 minutos
        int minimoPermitido = tiempoMaximo - margen;
        int maximoPermitido = tiempoMaximo + margen;
        
        // Act
        List<Cancion> canciones = coche.reproducirHastaTiempo(tiempoMaximo);

        // Verificar la duración total
        int duracionTotal = canciones.stream().mapToInt(Cancion::getDuracion).sum();

        // Asserts
        assertTrue(duracionTotal >= minimoPermitido && duracionTotal <= maximoPermitido,
                "La duración total (" + duracionTotal + "s) está fuera del rango permitido (" + minimoPermitido + "s - " + maximoPermitido + "s).");

        //System.out.println(String.format("Duración total: %02d:%02d:%02d", duracionTotal / 3600, (duracionTotal % 3600) / 60, duracionTotal % 60));

        // Verificar el contenido esperado
        assertEquals("Cancion1", canciones.get(0).getTitulo()); // 180s
        assertEquals("Cancion3", canciones.get(1).getTitulo()); // 220s
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1800, 3600, 5400, 7200})  // Valores de tiempo máximo en segundos
    @DisplayName("Creacion de Playlist de tiempos variables con los datos del csv")
    void testPlaylistXTiempo(int tiempoMaximo) {
    	
    	// Arrange
    	initCoche();
    
        int margen = 300; // 5 minutos
        int minimoPermitido = tiempoMaximo - margen;
        int maximoPermitido = tiempoMaximo + margen;
        
        // Act
        List<Cancion> canciones = coche.reproducirHastaTiempo(tiempoMaximo);

        // Verificar la duración total
        int duracionTotal = canciones.stream().mapToInt(Cancion::getDuracion).sum();

        // Asserts
        assertTrue(duracionTotal >= minimoPermitido && duracionTotal <= maximoPermitido,
                "La duración total (" + duracionTotal + "s) está fuera del rango permitido (" + minimoPermitido + "s - " + maximoPermitido + "s).");

        //System.out.println(String.format("Duración total: %02d:%02d:%02d", duracionTotal / 3600, (duracionTotal % 3600) / 60, duracionTotal % 60));

        // Verificar el contenido esperado
        assertEquals("Cancion1", canciones.get(0).getTitulo()); // 180s
        assertEquals("Cancion3", canciones.get(1).getTitulo()); // 220s
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1800, 3600, 5400, 7200})  // Valores de tiempo máximo en segundos
    @DisplayName("Creacion de Playlist de tiempos variables con canciones default de duraciones muy altas")
    void testPlaylistCancionesLargas(int tiempoMaximo) {
    	
    	// Arrange
    	usuario1 = new Usuario("Juan", "Pérez");
        usuario1.agregarCancion(new Cancion(1, "Cancion1", "Album1", "Artista1", 500));
        usuario1.agregarCancion(new Cancion(2, "Cancion2", "Album1", "Artista1", 500));

        usuario2 = new Usuario("Ana", "García");
        usuario2.agregarCancion(new Cancion(3, "Cancion3", "Album2", "Artista2", 500));
        usuario2.agregarCancion(new Cancion(4, "Cancion4", "Album3", "Artista3", 500));

        coche.agregarUsuario(usuario1);
        coche.agregarUsuario(usuario2);
    
        int margen = 300; // 5 minutos
        int minimoPermitido = tiempoMaximo - margen;
        int maximoPermitido = tiempoMaximo + margen;
        
        // Act
        List<Cancion> canciones = coche.reproducirHastaTiempo(tiempoMaximo);

        // Verificar la duración total
        int duracionTotal = canciones.stream().mapToInt(Cancion::getDuracion).sum();

        // Asserts
        assertTrue(duracionTotal >= minimoPermitido && duracionTotal <= maximoPermitido,
                "La duración total (" + duracionTotal + "s) está fuera del rango permitido (" + minimoPermitido + "s - " + maximoPermitido + "s).");

        //System.out.println(String.format("Duración total: %02d:%02d:%02d", duracionTotal / 3600, (duracionTotal % 3600) / 60, duracionTotal % 60));

        // Verificar el contenido esperado
        assertEquals("Cancion1", canciones.get(0).getTitulo()); // 180s
        assertEquals("Cancion3", canciones.get(1).getTitulo()); // 220s
    }
    

}
