package coche;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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

		// Crear Artistas
	    Artista artista1 = new Artista("Artista1");
	    Artista artista2 = new Artista("Artista2");
	    Artista artista3 = new Artista("Artista3");

	    // Crear Álbumes y agregar canciones
	    Album album1 = new Album("Album1", artista1);
	    Album album2 = new Album("Album2", artista2);
	    Album album3 = new Album("Album3", artista3);
	    
	    // Crear Usuarios y asignar álbumes
	    usuario1 = new Usuario("Juan", "Pérez");
	    usuario1.agregarAlbum(album1);
	    usuario1.agregarCancion(new Cancion(1, "Cancion1", album1, artista1, 180));
	    usuario1.agregarCancion(new Cancion(2, "Cancion2", album1, artista1, 200));
	    
	    usuario2 = new Usuario("Ana", "García");
	    usuario2.agregarAlbum(album2); // Agrega todo el álbum 2
	    usuario2.agregarAlbum(album3); // Agrega todo el álbum 3
	    usuario2.agregarCancion(new Cancion(3, "Cancion3", album2, artista2, 220));
	    usuario2.agregarCancion(new Cancion(4, "Cancion4", album3, artista3, 240));

	    // Crear coche y agregar usuarios
	    coche.agregarUsuario(usuario1);
	    coche.agregarUsuario(usuario2);
	}

	////////////////////////////////////////////////////////////////
	
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
        assertFalse(coche.getPasajeros().get(0).getListaAlbumes().isEmpty(), "El usuario " + coche.getPasajeros().get(0).getNombre() + " no tiene canciones asociadas");
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
        coche.reproducirCancionDeCadaUsuario();

        // Asserts
        assertEquals(2, coche.getCanciones().size());
        assertEquals("Cancion1", coche.getCanciones().get(0).getTitulo());
        assertEquals("Cancion3", coche.getCanciones().get(1).getTitulo());

        // Act
        coche.reproducirCancionDeCadaUsuario();
        // Asserts
        assertEquals("Cancion2", coche.getCanciones().get(2).getTitulo());
        assertEquals("Cancion4", coche.getCanciones().get(3).getTitulo());

        // Act
        coche.reproducirCancionDeCadaUsuario();
        // Asserts
        assertEquals("Cancion1", coche.getCanciones().get(4).getTitulo());
        assertEquals("Cancion3", coche.getCanciones().get(5).getTitulo());
    }
    
    @Test
    @DisplayName("Reproducir playlists para todos los pasajeros del csv")
    void testPlaylistCsv() {
    	
    	// Arrange
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
    	
    	// Act
    	coche.leer_csv(rutaArchivo);
    	
        coche.reproducirCancionDeCadaUsuario();

        // Asserts
        assertEquals(coche.getPasajeros().size(), coche.getCanciones().size());
        assertEquals("Intro: Persona", coche.getCanciones().get(0).getTitulo());
        assertEquals("Next to Me", coche.getCanciones().get(1).getTitulo());
        assertEquals("Future Nostalgia", coche.getCanciones().get(2).getTitulo());
        assertEquals("Golden", coche.getCanciones().get(3).getTitulo());
    }
	

    @Test
    @DisplayName("Creacion de Playlist de 1 hora con los datos del csv")
    void testPlaylist1h() {
    	
    	// Arrange
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
        coche.leer_csv(rutaArchivo);
    	
        int tiempoMaximo = 3600;
        int margen = 300; // 5 minutos
        int minimoPermitido = tiempoMaximo - margen;
        int maximoPermitido = tiempoMaximo + margen;
        
        // Act
        coche.reproducirHastaTiempo(tiempoMaximo);

        // Verificar la duración total
        int duracionTotal = coche.getCanciones().stream().mapToInt(Cancion::getDuracion).sum();

        // Asserts
        assertTrue(duracionTotal >= minimoPermitido && duracionTotal <= maximoPermitido,
                "La duración total (" + duracionTotal + "s) está fuera del rango permitido (" + minimoPermitido + "s - " + maximoPermitido + "s).");

        //System.out.println(String.format("Duración total: %02d:%02d:%02d", duracionTotal / 3600, (duracionTotal % 3600) / 60, duracionTotal % 60));

        // Verificar el contenido esperado
        assertEquals("Intro: Persona", coche.getCanciones().get(0).getTitulo());
        assertEquals("Next to Me", coche.getCanciones().get(1).getTitulo());
    }

    @Test
    @DisplayName("Creacion de Playlist de 1 hora y media con los datos del csv")
    void testPlaylist1_5h() {
    	
    	// Arrange
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
        coche.leer_csv(rutaArchivo);
        int tiempoMaximo = 5400;
        int margen = 300; // 5 minutos
        int minimoPermitido = tiempoMaximo - margen;
        int maximoPermitido = tiempoMaximo + margen;
        
        // Act
        coche.reproducirHastaTiempo(tiempoMaximo);

        // Verificar la duración total
        int duracionTotal = coche.getCanciones().stream().mapToInt(Cancion::getDuracion).sum();

        // Asserts
        assertTrue(duracionTotal >= minimoPermitido && duracionTotal <= maximoPermitido,
                "La duración total (" + duracionTotal + "s) está fuera del rango permitido (" + minimoPermitido + "s - " + maximoPermitido + "s).");

        //System.out.println(String.format("Duración total: %02d:%02d:%02d", duracionTotal / 3600, (duracionTotal % 3600) / 60, duracionTotal % 60));

        // Verificar el contenido esperado
        assertEquals("Intro: Persona", coche.getCanciones().get(0).getTitulo());
        assertEquals("Next to Me", coche.getCanciones().get(1).getTitulo());
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1800, 3600, 5400, 7200})  // Valores de tiempo máximo en segundos
    @DisplayName("Creacion de Playlist de tiempos variables con los datos del csv")
    void testPlaylistXTiempo(int tiempoMaximo) {
    	
    	// Arrange
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
        coche.leer_csv(rutaArchivo);
        int margen = 300; // 5 minutos
        int minimoPermitido = tiempoMaximo - margen;
        int maximoPermitido = tiempoMaximo + margen;
        
        // Act
        coche.reproducirHastaTiempo(tiempoMaximo);

        // Verificar la duración total
        int duracionTotal = coche.getCanciones().stream().mapToInt(Cancion::getDuracion).sum();

        // Asserts
        assertTrue(duracionTotal >= minimoPermitido && duracionTotal <= maximoPermitido,
                "La duración total (" + duracionTotal + "s) está fuera del rango permitido (" + minimoPermitido + "s - " + maximoPermitido + "s).");

        //System.out.println(String.format("Duración total: %02d:%02d:%02d", duracionTotal / 3600, (duracionTotal % 3600) / 60, duracionTotal % 60));

        // Verificar el contenido esperado
        assertEquals("Intro: Persona", coche.getCanciones().get(0).getTitulo());
        assertEquals("Next to Me", coche.getCanciones().get(1).getTitulo());
    }
    
    /*
     * Para 1800 no se acepta porque se queda en 2690 segunda
     * Para 3600 sí se acepta porque coincide en 3840, que entra dento del margen de +-500
     */
    @ParameterizedTest
    @ValueSource(ints = {1800, 3600})  // Valores de tiempo máximo en segundos
    @DisplayName("Creacion de Playlist de tiempos variables con canciones default de duraciones muy altas")
    void testPlaylistCancionesLargas(int tiempoMaximo) {
    	
    	// Arrange
    	// Crear Artistas
	    Artista artista1 = new Artista("Artista1");
	    Artista artista2 = new Artista("Artista2");
	    Artista artista3 = new Artista("Artista3");

	    // Crear Álbumes y agregar canciones
	    Album album1 = new Album("Album1", artista1);
	    Album album2 = new Album("Album2", artista2);
	    Album album3 = new Album("Album3", artista3);
	    
	    // Crear Usuarios y asignar álbumes
	    usuario1 = new Usuario("Juan", "Pérez");
	    usuario1.agregarAlbum(album1);
	    usuario1.agregarCancion(new Cancion(1, "Cancion1", album1, artista1, 500));
	    usuario1.agregarCancion(new Cancion(2, "Cancion2", album1, artista1, 200));
	    
	    usuario2 = new Usuario("Ana", "García");
	    usuario2.agregarAlbum(album2); // Agrega todo el álbum 2
	    usuario2.agregarAlbum(album3); // Agrega todo el álbum 3
	    usuario2.agregarCancion(new Cancion(3, "Cancion3", album2, artista2, 1000));
	    usuario2.agregarCancion(new Cancion(4, "Cancion4", album3, artista3, 900));

	    // Crear coche y agregar usuarios
	    coche.agregarUsuario(usuario1);
	    coche.agregarUsuario(usuario2);
    
        int margen = 300; // 5 minutos
        int minimoPermitido = tiempoMaximo - margen;
        int maximoPermitido = tiempoMaximo + margen;
        
        // Act
        coche.reproducirHastaTiempo(tiempoMaximo);

        // Verificar la duración total
        int duracionTotal = coche.getCanciones().stream().mapToInt(Cancion::getDuracion).sum();

        // Asserts
        assertTrue(duracionTotal >= minimoPermitido && duracionTotal <= maximoPermitido,
                "La duración total (" + duracionTotal + "s) está fuera del rango permitido (" + minimoPermitido + "s - " + maximoPermitido + "s).");

        //System.out.println(String.format("Duración total: %02d:%02d:%02d", duracionTotal / 3600, (duracionTotal % 3600) / 60, duracionTotal % 60));

        // Verificar el contenido esperado
        assertEquals("Cancion1", coche.getCanciones().get(0).getTitulo()); // 180s
        assertEquals("Cancion3", coche.getCanciones().get(1).getTitulo()); // 220s
    }
    
    
    ////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("Prueba de Randonmización")
    void testRandomize() {
    	
    	// Arrange
    	initCoche();
    	
    	// Act
    	coche.reproducirHastaTiempo(1000); // Crear una playlist
    	coche.randomizarPlayList();
    	ArrayList<Cancion> c1 = new ArrayList<Cancion>(coche.getCanciones());
    	coche.randomizarPlayList();
    	ArrayList<Cancion> c2 = new ArrayList<Cancion>(coche.getCanciones());
  
    	
    	assertNotEquals(c1,c2,"Las listas deberían ser distintas");
    	
    	
    }
    
    ////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("Creación de una playlist con 2 usuarios y de canciones con duración de menos de 3 minutos")
    void testComprobarPlaylist() {
    	
    }
    
    
}
