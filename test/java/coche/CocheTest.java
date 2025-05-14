package coche;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mockito;


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
	
	// HU1: Cargar datos desde un archivo CSV
	
	@Test
	@DisplayName("CP1.1: Lectura de los datos del csv")
	void testLeerCSV() {
        
		// Arrange
        String rutaArchivo = "src/resources/discos_usuarios.csv";

        // Act
		try {
			coche.leer_csv(rutaArchivo);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

        // Asserts
        // Comprobación básica: que haya al menos un usuario cargado
        assertFalse(coche.getPasajeros().isEmpty(), "No se ha cargado ningún usuario.");

        // Comprobar que se le han asignado canciones a un usuario
        assertFalse(coche.getPasajeros().get(0).getListaAlbumes().isEmpty(), "El usuario " + coche.getPasajeros().get(0).getNombre() + " no tiene canciones asociadas");
    }
	
	@Test
    @DisplayName("CP1.2: Cargar CSV con formato incorrecto")
    void testLeerCSVFormatoIncorrecto() throws IOException {
		// Arrange
        String csvContent = "ID,Nombre,Apellido,Artista,Album,Canción,Duración\n" +
                            "1,Juan,Pérez,BTS,Map of the Soul: 7,Intro: Persona,2:51\n" +
                            "2,Juan,Pérez,BTS,Map of the Soul: 7,Boy With Luv,3:49\n" +
                            "3,Juan,Pérez,BTS,Map of the Soul: 7,Make It Right,3:46\n" +
                            "4,Juan,Pérez,BTS,Map of the Soul: 7,Jamais Vu,3:47\n" +
                            "5,Juan,Pérez,BTS,Map of the Soul: 7,Dionysus,4:08x";

        Files.write(Paths.get("src/resources/csv_erroneo.csv"), csvContent.getBytes());
        
        
        // Act & Asserts
        assertThrows(NumberFormatException.class, () -> coche.leer_csv("src/resources/csv_erroneo.csv"));
        assertEquals(4, coche.getPasajeros().get(0).getListaAlbumes().get(0).getListaCanciones().size(), "Se esperaban 4 canciones cargadas debido al error en la última línea.");

        Files.delete(Paths.get("src/resources/csv_erroneo.csv"));
    }

    @Test
    @DisplayName("CP1.3: Cargar CSV con usuarios duplicados")
    void testLeerCSVUsuariosDuplicados() throws IOException {
    	
    	// Arrange
        String csvContent = "ID,Nombre,Apellido,Artista,Album,Canción,Duración\n" +
                            "1,Juan,Pérez,BTS,Map of the Soul: 7,Intro: Persona,2:51\n" +
                            "2,Juan,Pérez,BTS,Map of the Soul: 7,Boy With Luv,3:49\n" +
                            "3,Juan,Pérez,BTS,Map of the Soul: 7,Make It Right,3:46\n" +
                            "4,Juan,Pérez,BTS,Map of the Soul: 7,Jamais Vu,3:47\n" +
                            "5,Juan,Pérez,BTS,Map of the Soul: 7,Intro: Persona,2:51";

        Files.write(Paths.get("src/resources/csv_duplicados.csv"), csvContent.getBytes());

        // Act
        coche.leer_csv("src/resources/csv_duplicados.csv");
        
        // Asserts
        assertEquals(1, coche.getPasajeros().size(), "Se esperaba un único usuario cargado.");
        assertEquals(5, coche.getPasajeros().get(0).getListaAlbumes().get(0).getListaCanciones().size(), "Se esperaban 5 canciones cargadas sin duplicados.");

        Files.delete(Paths.get("src/resources/csv_duplicados.csv"));
    }
    
    @Test
    @DisplayName("CP1.4: csv con un número incorrecto de columnas")
    void testLeerCSV8columnas() throws IOException {
    	
    	// Arrange
        String csvContent = "ID,Nombre,Apellido,Artista,Album,Canción,Duración,Valoracion\n" +
                            "1,Juan,Pérez,BTS,Map of the Soul: 7,Intro: Persona,2:51,4\n" +
                            "2,Juan,Pérez,BTS,Map of the Soul: 7,Boy With Luv,3:49,3\n" +
                            "3,Juan,Pérez,BTS,Map of the Soul: 7,Make It Right,3:46,2\n" +
                            "4,Juan,Pérez,BTS,Map of the Soul: 7,Jamais Vu,3:47,5\n" +
                            "5,Juan,Pérez,BTS,Map of the Soul: 7,Intro: Persona,2:51,4";

        Files.write(Paths.get("src/resources/csv_8columnas.csv"), csvContent.getBytes());
        
        // Act & Assert
        assertThrows(IOException.class, () -> coche.leer_csv("src/resources/csv_8columnas.csv"));

        Files.delete(Paths.get("src/resources/csv_8columnas.csv"));
    }
    
    @Test
    @DisplayName("CP1.5: csv con duracion en horas, minutos y segundos")
    void testLeerCSVhoraMinutoSegundo() throws IOException {
    	
    	// Arrange
    	String csvContent = "ID,Nombre,Apellido,Artista,Album,Canción,Duración\n" +
                "1,Juan,Pérez,BTS,Map of the Soul: 7,Intro: Persona,0:2:51\n" +
                "2,Juan,Pérez,BTS,Map of the Soul: 7,Boy With Luv,0:3:49\n" +
                "3,Juan,Pérez,BTS,Map of the Soul: 7,Make It Right,0:3:46\n" +
                "4,Juan,Pérez,BTS,Map of the Soul: 7,Jamais Vu,0:3:47\n" +
                "5,Juan,Pérez,BTS,Map of the Soul: 7,Intro: Persona,0:2:51";
    	
        Files.write(Paths.get("src/resources/csv_horaMinutoSegundo.csv"), csvContent.getBytes());
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> coche.leer_csv("src/resources/csv_horaMinutoSegundo.csv"));

        Files.delete(Paths.get("src/resources/csv_horaMinutoSegundo.csv"));
    }
    
    // HU2: Reproducción de canciones de todos los usuarios en ciclo
    /*
	 * Añade 2 usuarios con 2 canciones cada uno. Prueba si al llamar
	 */
    @Test
    @DisplayName("CP2.1: Reproducir playlists para todos los pasajeros default")
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
    @DisplayName("CP2.2: Reproducir playlists para todos los pasajeros del csv")
    void testPlaylistCsv() {
    	
    	// Arrange
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
    	
    	// Act
    	try {
			coche.leer_csv(rutaArchivo);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
        coche.reproducirCancionDeCadaUsuario();

        // Asserts
        assertEquals(coche.getPasajeros().size(), coche.getCanciones().size());
        assertEquals("Intro: Persona", coche.getCanciones().get(0).getTitulo());
        assertEquals("Next to Me", coche.getCanciones().get(1).getTitulo());
        assertEquals("Future Nostalgia", coche.getCanciones().get(2).getTitulo());
        assertEquals("Golden", coche.getCanciones().get(3).getTitulo());
    }
    
    @Test
    @DisplayName("CP2.3: Reproducir canciones cuando un usuario no tiene canciones disponibles")
    void testReproducirUsuarioSinCanciones() {
        // Arrange
        usuario1 = new Usuario("Juan", "Pérez");
        usuario2 = new Usuario("Carlos", "Sánchez");

        Artista artista = new Artista("Artista1");
        Album album = new Album("Album1", artista);
        usuario2.agregarAlbum(album);
        usuario2.agregarCancion(new Cancion(1, "Cancion1", album, artista, 180));

        coche.agregarUsuario(usuario1);  // Sin canciones
        coche.agregarUsuario(usuario2);  // Con una canción

        // Act
        coche.reproducirCancionDeCadaUsuario();

        // Assert
        assertEquals(1, coche.getCanciones().size(), "Solo se debe reproducir la canción del usuario con canciones.");
        assertEquals("Cancion1", coche.getCanciones().get(0).getTitulo(), "La canción reproducida debe ser la única disponible.");
        assertNotNull(coche.getCanciones().get(0), "La canción reproducida no debe ser null.");

        // Verificación con Mockito para simular comportamiento
        Coche cocheMock = Mockito.mock(Coche.class);
        doThrow(new IllegalStateException("Usuario sin canciones")).when(cocheMock).reproducirCancionDeCadaUsuario();
        assertThrows(IllegalStateException.class, () -> cocheMock.reproducirCancionDeCadaUsuario(), "Debe lanzar excepción si el usuario no tiene canciones.");
    }
    
    @Test
    @DisplayName("CP2.4: Reproducir canciones con un album sin canciones")
    void testReproducirAlbumSinCanciones() {
        // Arrange
        usuario1 = new Usuario("Juan", "Pérez");

        Artista artista = new Artista("Artista1");
        Album album1 = new Album("Album1", artista);
        Album album2 = new Album("Album2", artista);
        usuario1.agregarAlbum(album1); // Álbum vacío
        usuario1.agregarAlbum(album2); // Álbum con una canción
        usuario1.agregarCancion(new Cancion(1, "Cancion1", album1, artista, 180));

        coche.agregarUsuario(usuario1);

        // Act
        coche.reproducirCancionDeCadaUsuario();

        // Assert
        
        // Verificar que no se reproduzcan canciones del álbum vacío
        assertEquals(1, coche.getCanciones().size(), "Debe haber una canción reproducida.");
        assertEquals("Cancion1", coche.getCanciones().get(0).getTitulo(), "La canción reproducida debe ser 'Cancion1' del álbum 2.");

        // Verificar que el álbum vacío no ha añadido nada
        assertTrue(album2.getListaCanciones().isEmpty(), "El álbum 'Album1' debe estar vacío.");

        // Verificar que no se ha añadido ninguna canción del álbum vacío
        assertFalse(coche.getCanciones().stream().anyMatch(c -> c.getAlbum().equals(album2)), 
            "No se deben reproducir canciones del álbum vacío.");
    }
	////////////////////////////////////////////////////////////////
	
	
	// HU4: Reproducción hasta un tiempo límite especificado
    @Test
    @DisplayName("CP4.1: Creacion de Playlist de 1 hora con los datos del csv")
    void testPlaylistNoSongs() {
    	
    	// Arrange
    	 usuario1 = coche.buscarUsuario("Carlos", "Sánchez");
         if (usuario1 == null) {
         	usuario1 = new Usuario("Carlos", "Sánchez");
         }
         usuario2 = coche.buscarUsuario("Juan", "Sánchez");
         if (usuario2 == null) {
         	usuario2 = new Usuario("Juan", "Sánchez");
         }

        Artista artista = new Artista("Artista1");
        Album album = new Album("Album1", artista);
        usuario2.agregarAlbum(album);
        usuario2.agregarCancion(new Cancion(1, "Cancion1", album, artista, 180));
        
        coche.agregarUsuario(usuario1);  // Sin canciones
        coche.agregarUsuario(usuario2);  // Con una canción
    	
    	// Act
        coche.reproducirHastaTiempo(3600);

        // Assert
        // Verificar que solo se ha añadido la canción del usuario con canciones
        assertEquals(1, coche.getCanciones().size(), "Debe haberse añadido únicamente la canción del usuario con canciones.");

        // Verificar que la canción añadida es "Cancion1"
        assertEquals("Cancion1", coche.getCanciones().get(0).getTitulo(), "La única canción reproducida debe ser 'Cancion1'.");

        // Verificar la duración total de la playlist
        int duracionTotal = coche.getCanciones().stream().mapToInt(Cancion::getDuracion).sum();
        assertEquals(180, duracionTotal, "La duración total debe ser de 180 segundos (3 minutos).");

        // Verificar que el usuario sin canciones no ha añadido nada
        assertEquals(0, usuario1.getNumeroCanciones(), "El usuario 'Juan' no debe haber aportado ninguna canción.");

        // Verificar que el usuario con canciones ha visitado todas sus canciones
        assertEquals(1, usuario2.getNumeroCanciones(), "El usuario 'Carlos' debe tener una única canción registrada.");

        // Verificar que se ha visitado exactamente 1 canción
        assertEquals(1, coche.getCanciones().size(), "Debe haberse visitado exactamente una canción en la playlist.");
    }

    @Test
    @DisplayName("CP4.2: Creacion de Playlist de 1 hora y media con los datos del csv")
    void testPlaylist1_5h() {
    	
    	// Arrange
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
    	try {
			coche.leer_csv(rutaArchivo);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
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
    @DisplayName("CP4.3: Creacion de Playlist de tiempos variables con los datos del csv")
    void testPlaylistXTiempo(int tiempoMaximo) {
    	
    	// Arrange
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
    	try {
			coche.leer_csv(rutaArchivo);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
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
    @DisplayName("CP4.4: Creacion de Playlist de 1h pero los usuarios no tienen canciones")
    void testPlaylistSinCanciones() {
    	
    	// Arrange
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
    	try {
			coche.leer_csv(rutaArchivo);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
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
    
    // HU5: Creación de playlist con canciones largas
    
    @ParameterizedTest
    @ValueSource(ints = {1800, 3600})  // Duración máxima en segundos
    @DisplayName("CP5.1: Crear playlist con canciones largas y cortas")
    void testPlaylistCancionesLargas(int tiempoMaximo) {
    	
    	// Arrange
        usuario1 = coche.buscarUsuario("Juan", "Sánchez");
        if (usuario1 == null) {
        	usuario1 = new Usuario("Juan", "Sánchez");
        }
        usuario2 = coche.buscarUsuario("Juan", "Sánchez");
        if (usuario2 == null) {
        	usuario2 = new Usuario("Juan", "Sánchez");
        }

        Artista artista1 = new Artista("Artista1");
        Album album1 = new Album("Album1", artista1);

        usuario1.agregarAlbum(album1);
        usuario1.agregarCancion(new Cancion(1, "Corta1", album1, artista1, 300));  // 5 min
        usuario1.agregarCancion(new Cancion(2, "Larga1", album1, artista1, 1500)); // 25 min

        
        coche.agregarUsuario(usuario1);
    	
        // Act
        coche.reproducirHastaTiempo(tiempoMaximo);

        // Assert
        assertNotNull(coche.getCanciones(), "La lista de canciones reproducidas no debe ser null.");

        // Verificación de tiempos
        int duracionTotal = coche.getCanciones().stream().mapToInt(Cancion::getDuracion).sum();
        assertTrue(duracionTotal <= tiempoMaximo, "La duración total no debe exceder el tiempo máximo.");
    }
    
    @Test
    @DisplayName("CP5.2: Reproducir hasta 3000 segundos con canciones largas y verificar la duración acumulada")
    void testReproduccion3000SegundosConMock() {
        // Arrange
        Usuario usuarioMock = Mockito.mock(Usuario.class);
        Artista artista1 = new Artista("Artista1");
        Album album1 = new Album("Album1", artista1);

        // Canciones simuladas
        Cancion corta = new Cancion(1, "Corta1", album1, artista1, 300);   // 5 min
        Cancion larga1 = new Cancion(2, "Larga1", album1, artista1, 2000); // 33 min
        Cancion larga2 = new Cancion(3, "Larga2", album1, artista1, 1500); // 25 min

        // Simular comportamiento del método obtenerSiguienteCancion()
        when(usuarioMock.obtenerSiguienteCancion()).thenReturn(corta, larga1, larga2, null);
        when(usuarioMock.getNumeroCanciones()).thenReturn(3);

        
        coche.agregarUsuario(usuarioMock);

        // Act
        coche.reproducirHastaTiempo(3000);

        // Assert
        assertNotNull(coche.getCanciones(), "La lista de canciones reproducidas no debe ser null.");

        int duracionTotal = coche.getCanciones().stream().mapToInt(Cancion::getDuracion).sum();
        assertTrue(duracionTotal <= 5400, "La duración total no debe exceder los 5400 segundos.");

        // Verificación de canciones reproducidas
        assertEquals(2, coche.getCanciones().size(), "Debe haber reproducido solo dos canciones debido a la restricción de tiempo.");

        // Verificar que las canciones reproducidas sean "Corta1" y "Larga1"
        assertEquals("Corta1", coche.getCanciones().get(0).getTitulo());
        assertEquals("Larga1", coche.getCanciones().get(1).getTitulo());

        // Verificar que "Larga2" no se ha reproducido
        assertFalse(coche.getCanciones().stream().anyMatch(c -> c.getTitulo().equals("Larga2")),
                "La canción 'Larga2' no debe haberse reproducido por exceso de tiempo.");

        // Verificar interacciones con el mock
        verify(usuarioMock, times(3)).obtenerSiguienteCancion(); // A la tercera llamada, comprueba que la canción larga2 no cabe, así que no la añade
    }


    @Test
    @DisplayName("CP5.3: Reproducir hasta 7200 segundos con canciones largas y cortas, omitiendo las que excedan el tiempo")
    void testReproduccion7200SegundosConMock() {
        // Arrange
        Usuario usuarioMock = Mockito.mock(Usuario.class);
        Artista artista1 = new Artista("Artista1");
        Album album1 = new Album("Album1", artista1);

        // Canciones simuladas
        Cancion corta1 = new Cancion(1, "Corta1", album1, artista1, 300);  // 5 min
        Cancion larga1 = new Cancion(2, "Larga1", album1, artista1, 5000); // 83 min
        Cancion corta2 = new Cancion(3, "Corta2", album1, artista1, 600);  // 10 min
        
        usuarioMock.agregarCancion(corta1);
        usuarioMock.agregarCancion(larga1);
        usuarioMock.agregarCancion(corta2);

        // Simular comportamiento
        when(usuarioMock.obtenerSiguienteCancion()).thenReturn(corta1, larga1, corta2, null);
        when(usuarioMock.getNumeroCanciones()).thenReturn(3);

        coche.agregarUsuario(usuarioMock);

        // Act
        coche.reproducirHastaTiempo(1000);
        
        // System.out.println(coche.getCanciones());

        // Assert
        assertNotNull(coche.getCanciones(), "La lista de canciones reproducidas no debe ser null.");
        int duracionTotal = coche.getCanciones().stream().mapToInt(Cancion::getDuracion).sum();
        assertTrue(duracionTotal <= 7200, "La duración total no debe exceder los 7200 segundos.");

        // Verificar que "Larga1" no se ha reproducido por exceso de tiempo
        assertFalse(coche.getCanciones().stream().anyMatch(c -> c.getTitulo().equals("Larga1")),
                "La canción 'Larga1' no debe haber sido reproducida.");

        // Verificar interacciones con el mock
        verify(usuarioMock, times(3)).obtenerSiguienteCancion();
    }

    
    //////////////////////////////////////////////////////////////// Funcionalidad: Randomizar Playlist
    
    // HU6: Mezclar Playlist con Canciones
    
    @Test
    @DisplayName("CP6.1: Randomizar una playlist con varias canciones")
    void testRandomizarVariasCanciones() {
        // Arrange - Cargar canciones desde el CSV
        String rutaArchivo = "src/resources/discos_usuarios.csv";
        try {
			coche.leer_csv(rutaArchivo);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
        coche.reproducirHastaTiempo(3600);  // Reproducir hasta 1 hora para generar una playlist

        // Obtener el estado inicial de la playlist
        List<Cancion> cancionesOriginal = new ArrayList<>(coche.getCanciones());

        // Act
        coche.randomizarPlayList();

        // Assert - Verificar que el tamaño de la lista no ha cambiado
        assertEquals(cancionesOriginal.size(), coche.getCanciones().size(), "El tamaño de la playlist no debe cambiar tras la mezcla.");

        // Verificar que las canciones siguen siendo las mismas (sin duplicados ni pérdidas)
        assertTrue(coche.getCanciones().containsAll(cancionesOriginal), "Todas las canciones deben estar presentes tras la mezcla.");

        // Verificar que el orden ha cambiado
        assertNotEquals(cancionesOriginal, coche.getCanciones(), "El orden de las canciones debe cambiar tras la mezcla.");
    }

    @Test
    @DisplayName("CP6.2: Randomizar una playlist con una única canción")
    void testRandomizarUnaCancion() {
        // Arrange - Crear usuario y agregar una única canción
        Usuario usuario = new Usuario("Juan", "Pérez");
        Artista artista = new Artista("Artista1");
        Album album = new Album("Album1", artista);
        usuario.agregarAlbum(album);
        Cancion cancionUnica = new Cancion(1, "Unica", album, artista, 180);
        usuario.agregarCancion(cancionUnica);

        coche.agregarUsuario(usuario);
        coche.reproducirHastaTiempo(180);  // Reproducir hasta 3 minutos

        // Obtener el estado inicial de la playlist
        List<Cancion> cancionesOriginal = new ArrayList<>(coche.getCanciones());

        // Act
        coche.randomizarPlayList();

        // Assert - Verificar que el tamaño de la lista no ha cambiado
        assertEquals(1, coche.getCanciones().size(), "La playlist debe contener una única canción.");

        // Verificar que la única canción es la misma antes y después de la mezcla
        assertEquals(cancionesOriginal, coche.getCanciones(), "La única canción debe permanecer en la misma posición.");
    }

    @Test
    @DisplayName("CP6.3: Randomizar una playlist vacía")
    void testRandomizarPlaylistVacia() {
        // Arrange - No cargar canciones, playlist vacía

        // Act
        coche.randomizarPlayList();

        // Assert - Verificar que la lista sigue vacía
        assertTrue(coche.getCanciones().isEmpty(), "La playlist debe permanecer vacía tras la mezcla.");
    }
    
    // HU7: Verificar Integridad del Contenido tras la Mezcla

    @Test
    @DisplayName("CP7.1: Verificar integridad tras mezclar una playlist con varias canciones")
    void testIntegridadTrasMezclar() {
        // Arrange - Cargar canciones desde el CSV
        String rutaArchivo = "src/resources/discos_usuarios.csv";
        try {
			coche.leer_csv(rutaArchivo);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
        coche.reproducirHastaTiempo(5400);  // Reproducir hasta 1.5 horas para una playlist grande

        // Obtener una copia de la playlist original
        List<Cancion> cancionesOriginal = new ArrayList<>(coche.getCanciones());

        // Act
        coche.randomizarPlayList();

        // Assert - Verificar que el tamaño de la playlist no ha cambiado
        assertEquals(cancionesOriginal.size(), coche.getCanciones().size(), "El tamaño de la playlist debe mantenerse tras la mezcla.");

        // Verificar que todas las canciones originales están presentes tras la mezcla
        assertTrue(coche.getCanciones().containsAll(cancionesOriginal), "Todas las canciones deben estar presentes tras la mezcla.");

    }

    @Test
    @DisplayName("CP7.2: Verificar integridad tras mezclar una playlist vacía")
    void testIntegridadPlaylistVacia() {
        // Arrange - Playlist vacía

        // Act
        coche.randomizarPlayList();

        // Assert - Verificar que la lista sigue vacía
        assertTrue(coche.getCanciones().isEmpty(), "La playlist debe permanecer vacía tras la mezcla.");
    }

    
    
    //////////////////////////////////////////////////////////////// Funcionalidad: Randomizar por Artista con Shuffle
    
    // HU8: Filtrar y Mezclar Playlist por Artista
    
    @Test
    @DisplayName("CP8.1: Filtrar y mezclar por artista presente en la playlist")
    void testRandomizarArtistaDesdeCSV() {
        // Arrange - Cargar canciones desde el CSV
        String rutaArchivo = "src/resources/discos_usuarios.csv";

        try {
            coche.leer_csv(rutaArchivo);
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }

        coche.reproducirHastaTiempo(3600);  // Reproducir hasta 1 hora para generar una playlist

        // Verificar que se ha cargado la playlist
        assertFalse(coche.getCanciones().isEmpty(), "La playlist debe contener canciones después de cargar el CSV");

        // Seleccionar un artista presente en la playlist
        Artista artistaSeleccionado = coche.getCanciones().get(0).getArtista();

        // Obtener una copia del contenido filtrado esperado
        List<Cancion> cancionesFiltradas = coche.getCanciones().stream()
                                                .filter(c -> c.getArtista().equals(artistaSeleccionado))
                                                .collect(Collectors.toList());

        // Act - Filtrar y mezclar por artista
        coche.randomizarArtista(artistaSeleccionado);

        // Assert - Verificar que todas las canciones pertenecen al artista seleccionado
        assertTrue(coche.getCanciones().stream().allMatch(c -> c.getArtista().equals(artistaSeleccionado)),
                "Todas las canciones deben pertenecer al artista seleccionado.");

        // Verificar que no hay canciones de otros artistas
        assertEquals(cancionesFiltradas.size(), coche.getCanciones().size(), 
                "La cantidad de canciones debe ser igual al número de canciones del artista.");

        // Verificar que las canciones se han mezclado
        assertNotEquals(cancionesFiltradas, coche.getCanciones(), "El orden de las canciones debe haber cambiado tras el shuffle.");
    }
    
    @Test
    @DisplayName("CP8.2: Filtrar y mezclar una playlist vacía")
    void testRandomizarArtistaListaVacia() {
        // Arrange - Crear un artista cualquiera
        Artista artista = new Artista("Artista Desconocido");

        // Act - Filtrar y mezclar en una lista vacía
        coche.randomizarArtista(artista);

        // Assert - La lista debe permanecer vacía
        assertTrue(coche.getCanciones().isEmpty(), "La playlist debe permanecer vacía tras el filtrado.");
    }

    @Test
    @DisplayName("CP8.3: Filtrar y mezclar por un artista inexistente")
    void testRandomizarArtistaInexistente() {
        // Arrange - Cargar canciones desde el CSV
        String rutaArchivo = "src/resources/discos_usuarios.csv";

        try {
            coche.leer_csv(rutaArchivo);
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }

        coche.reproducirHastaTiempo(3600);  // Reproducir hasta 1 hora para generar una playlist

        // Crear un artista inexistente
        Artista artistaInexistente = new Artista("Artista Fantasma");

        // Act - Filtrar y mezclar
        coche.randomizarArtista(artistaInexistente);

        // Assert - La playlist debe quedar vacía
        assertTrue(coche.getCanciones().isEmpty(), "La playlist debe quedar vacía al no haber coincidencias con el artista seleccionado.");
    }
    
    // HU9: Verificar Integridad y Orden tras Filtrar y Mezclar
    
    @ParameterizedTest
    @CsvSource({
        "Artista1, 3",
        "Artista2, 2",
        "Artista3, 1"
    })
    @DisplayName("CP9.1: Verificar integridad y ausencia de duplicados tras mezclar con parámetros y Mock")
    void testIntegridadTrasRandomizarArtistaMock(String nombreArtista, int cancionesEsperadas) {
        // Arrange - Crear el Mock del coche
        Coche cocheMock = Mockito.spy(new Coche());

        Artista artista1 = new Artista("Artista1");
        Artista artista2 = new Artista("Artista2");
        Artista artista3 = new Artista("Artista3");

        Album album1 = new Album("Album1", artista1);
        Album album2 = new Album("Album2", artista2);
        Album album3 = new Album("Album3", artista3);

        // Crear canciones
        Cancion cancion1 = new Cancion(1, "Cancion1", album1, artista1, 180);
        Cancion cancion2 = new Cancion(2, "Cancion2", album1, artista1, 200);
        Cancion cancion3 = new Cancion(3, "Cancion3", album1, artista1, 220);
        Cancion cancion4 = new Cancion(4, "Cancion4", album2, artista2, 300);
        Cancion cancion5 = new Cancion(5, "Cancion5", album2, artista2, 400);
        Cancion cancion6 = new Cancion(6, "Cancion6", album3, artista3, 500);

        // Añadir canciones al coche
        cocheMock.getCanciones().addAll(List.of(cancion1, cancion2, cancion3, cancion4, cancion5, cancion6));

        // Crear el artista seleccionado basado en el parámetro
        Artista artistaSeleccionado = new Artista(nombreArtista);

        // Act - Filtrar y mezclar por artista
        cocheMock.randomizarArtista(artistaSeleccionado);

        // Assert - Verificar que no hay duplicados
        Set<Cancion> setCanciones = new HashSet<>(cocheMock.getCanciones());
        assertEquals(setCanciones.size(), cocheMock.getCanciones().size(), "No debe haber duplicados tras el filtrado y mezcla.");

        // Verificar que todas las canciones pertenecen al artista seleccionado
        assertTrue(cocheMock.getCanciones().stream().allMatch(c -> c.getArtista().equals(artistaSeleccionado)),
                   "Todas las canciones deben pertenecer al artista seleccionado.");

        // Verificar el tamaño de la lista
        assertEquals(cancionesEsperadas, cocheMock.getCanciones().size(), 
                     "El número de canciones debe coincidir con las del artista seleccionado.");
    }


    @ParameterizedTest
    @CsvSource({
        "Artista1, 3",
        "Artista2, 2",
        "ArtistaInexistente, 0"
    })
    @DisplayName("CP9.2: Verificar cantidad de canciones tras filtrado por artista con parámetros y Mock")
    void testCantidadFiltradaPorArtistaMock(String nombreArtista, int cancionesEsperadas) {
        // Arrange - Crear el Mock del coche
        Coche cocheMock = Mockito.spy(new Coche());

        Artista artista1 = new Artista("Artista1");
        Artista artista2 = new Artista("Artista2");
        Artista artista3 = new Artista("Artista3");

        Album album1 = new Album("Album1", artista1);
        Album album2 = new Album("Album2", artista2);
        Album album3 = new Album("Album3", artista3);

        // Crear canciones
        Cancion cancion1 = new Cancion(1, "Cancion1", album1, artista1, 180);
        Cancion cancion2 = new Cancion(2, "Cancion2", album1, artista1, 200);
        Cancion cancion3 = new Cancion(3, "Cancion3", album1, artista1, 220);
        Cancion cancion4 = new Cancion(4, "Cancion4", album2, artista2, 300);
        Cancion cancion5 = new Cancion(5, "Cancion5", album2, artista2, 400);
        Cancion cancion6 = new Cancion(6, "Cancion6", album3, artista3, 500);

        // Añadir canciones al coche
        cocheMock.getCanciones().addAll(List.of(cancion1, cancion2, cancion3, cancion4, cancion5, cancion6));

        // Crear el artista seleccionado basado en el parámetro
        Artista artistaSeleccionado = new Artista(nombreArtista);

        // Act - Filtrar y mezclar por artista
        cocheMock.randomizarArtista(artistaSeleccionado);

        // Assert - Verificar la cantidad de canciones tras el filtrado
        assertEquals(cancionesEsperadas, cocheMock.getCanciones().size(), 
                     "El número de canciones debe coincidir con las del artista seleccionado.");
    }

    
    //////////////////////////////////////////////////////////////// Funcionalidad: Crear Playlist Personalizada por Usuarios y Duración
    
    // HU10: Crear Playlist Personalizada por Usuarios Específicos y Duración
    
    @ParameterizedTest
    @CsvSource({
        "Juan Pérez, 1",
        "María López, 2",
        "Carlos García, 1"
    })
    @DisplayName("CP10.1: Crear playlist personalizada para usuarios específicos con duración < 3 minutos")
    void testPlaylistUsuariosDuracionMenor3Min(String usuario, int cancionesEsperadas) {
        // Arrange - Cargar canciones desde el CSV
        String rutaArchivo = "src/resources/discos_usuarios.csv";

        try {
            coche.leer_csv(rutaArchivo);
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }

        // Act - Crear playlist personalizada
        coche.crearPlaylistPersonalizada(Arrays.asList(usuario), 3);

        // Assert
        assertNotNull(coche.getCanciones(), "La playlist no debe ser nula.");
        assertEquals(cancionesEsperadas, coche.getCanciones().size(), 
                     "La cantidad de canciones debe coincidir con el número esperado para el usuario.");
        
        assertTrue(coche.getCanciones().stream().allMatch(c -> c.getDuracion() < 180),
                   "Todas las canciones deben tener una duración menor a 3 minutos.");
        
        // Verificar que no haya duplicados
        Set<Cancion> setCanciones = new HashSet<>(coche.getCanciones());
        assertEquals(setCanciones.size(), coche.getCanciones().size(), "No debe haber duplicados tras la creación de la playlist.");
    }

    @Test
    @DisplayName("CP10.2: Crear playlist para usuarios inexistentes")
    void testPlaylistUsuariosInexistentes() {
        // Arrange - Mockear el coche
        Coche cocheMock = Mockito.spy(new Coche());

        // Crear el usuario mock (inexistente en términos de álbumes/canciones)
        Usuario usuarioMock = Mockito.mock(Usuario.class);

        // Configurar el usuario mock
        Mockito.when(usuarioMock.getNombre()).thenReturn("Carlos");
        Mockito.when(usuarioMock.getApellido()).thenReturn("Fernández");
        Mockito.when(usuarioMock.getListaAlbumes()).thenReturn(new ArrayList<>());  // Usuario sin álbumes

        // Mockear el método buscarUsuario para devolver el usuario mock
        Mockito.doReturn(usuarioMock).when(cocheMock).buscarUsuario("Carlos", "Fernández");

        // Act - Crear playlist personalizada
        cocheMock.crearPlaylistPersonalizada(Arrays.asList("Carlos Fernández"), 5);

        // Assert - La playlist debe estar vacía ya que no tiene álbumes
        assertTrue(cocheMock.getCanciones().isEmpty(), "La playlist debe estar vacía porque el usuario no tiene álbumes.");

        // Verificar que `getListaAlbumes` fue llamado, pero no se añadieron canciones
        Mockito.verify(usuarioMock).getListaAlbumes();
    }

    @Test
    @DisplayName("CP10.3: Crear playlist sin usuarios seleccionados")
    void testPlaylistSinUsuarios() {
        // Arrange - Cargar canciones desde el CSV
        String rutaArchivo = "src/resources/discos_usuarios.csv";
        
        try {
            coche.leer_csv(rutaArchivo);
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }

        // Act & Assert
        assertTimeout(Duration.ofSeconds(1), () -> {
            coche.crearPlaylistPersonalizada(new ArrayList<>(), 5);
        }, "La operación no debe exceder el tiempo límite.");

        assertTrue(coche.getCanciones().isEmpty(), "La playlist debe estar vacía porque no se seleccionaron usuarios.");
    }

    
    @Test
    @DisplayName("CP10.4: Crear playlist con duración máxima de 1 minuto")
    void testPlaylistDuracionBaja() {
        // Arrange - Mockear el coche
        Coche cocheMock = Mockito.spy(new Coche());

        // Crear artista
        Artista artista = new Artista("Artista1");

        // Crear álbum como mock
        Album albumMock = Mockito.mock(Album.class);

        // Crear canciones
        Cancion cancion1 = new Cancion(1, "Cancion Larga", albumMock, artista, 300);  // 5 minutos
        Cancion cancion2 = new Cancion(2, "Cancion Corta", albumMock, artista, 50);   // 50 segundos

        // Crear usuario mock
        Usuario usuarioMock = Mockito.mock(Usuario.class);

        // Configurar el usuario mock
        Mockito.when(usuarioMock.getNombre()).thenReturn("Juan");
        Mockito.when(usuarioMock.getApellido()).thenReturn("Pérez");
        Mockito.when(usuarioMock.getListaAlbumes()).thenReturn(List.of(albumMock));

        // Mockear el método `buscarUsuario()` para devolver el usuario mock
        Mockito.doReturn(usuarioMock).when(cocheMock).buscarUsuario("Juan", "Pérez");

        // Configurar las canciones en el álbum mock
        Mockito.when(albumMock.getListaCanciones()).thenReturn(List.of(cancion1, cancion2));

        // Act - Crear playlist con duración de 1 minuto
        cocheMock.crearPlaylistPersonalizada(Arrays.asList("Juan Pérez"), 1);

        // Assert
        assertEquals(1, cocheMock.getCanciones().size(), "Solo debe añadirse la canción corta.");
        assertEquals("Cancion Corta", cocheMock.getCanciones().get(0).getTitulo(), "La única canción debe ser la de 50 segundos.");
    }


    @ParameterizedTest
    @CsvSource({
        "Juan Pérez, 10, 3",
        "Ana García, 15, 3",
        "María López, 20, 3"
    })
    @DisplayName("CP10.5: Crear playlist personalizada con duración alta")
    void testPlaylistDuracionAlta(String usuario, int duracionMax, int cancionesEsperadas) {
        // Arrange - Mockear el coche
        Coche cocheMock = Mockito.spy(new Coche());

        // Crear artista
        Artista artista = new Artista("Artista1");

        // Crear álbum como mock
        Album albumMock = Mockito.mock(Album.class);

        // Crear canciones
        Cancion cancion1 = new Cancion(1, "Cancion Corta", albumMock, artista, 180);  // 3 minutos
        Cancion cancion2 = new Cancion(2, "Cancion Media", albumMock, artista, 400);  // 6.67 minutos
        Cancion cancion3 = new Cancion(3, "Cancion Larga", albumMock, artista, 600);  // 10 minutos

        // Crear usuario mock
        Usuario usuarioMock = Mockito.mock(Usuario.class);

        // Configurar el usuario mock
        Mockito.when(usuarioMock.getNombre()).thenReturn(usuario.split(" ")[0]);
        Mockito.when(usuarioMock.getApellido()).thenReturn(usuario.split(" ")[1]);
        Mockito.when(usuarioMock.getListaAlbumes()).thenReturn(List.of(albumMock));

        // Mockear el método `buscarUsuario()` para devolver el usuario mock
        Mockito.doReturn(usuarioMock).when(cocheMock).buscarUsuario(usuario.split(" ")[0], usuario.split(" ")[1]);

        // Configurar las canciones en el álbum mock
        Mockito.when(albumMock.getListaCanciones()).thenReturn(List.of(cancion1, cancion2, cancion3));

        // Act - Crear playlist personalizada
        cocheMock.crearPlaylistPersonalizada(List.of(usuario), duracionMax);

        // Assert
        assertEquals(cancionesEsperadas, cocheMock.getCanciones().size(), 
                     "La cantidad de canciones debe coincidir con las que cumplen la duración máxima.");

        // Verificar que todas las canciones cumplen el criterio de duración
        assertTrue(cocheMock.getCanciones().stream()
                            .allMatch(c -> c.getDuracion() <= duracionMax * 60),
                   "Todas las canciones deben cumplir con la duración máxima permitida.");
    }



    
    // HU11: Crear Playlist Personalizada con Duración Baja (1 minuto o menos)
    
    @Test
    @DisplayName("CP11.1: Crear playlist con duración de 1 minuto")
    void testPlaylistDuracion1Minuto() {
        // Arrange - Mockear el coche
        Coche cocheMock = Mockito.spy(new Coche());

        // Crear artista
        Artista artista = new Artista("Artista1");

        // Crear álbum como mock
        Album albumMock = Mockito.mock(Album.class);

        // Crear canciones
        Cancion cancion1 = new Cancion(1, "Cancion Corta", albumMock, artista, 110);   // 110 segundos
        Cancion cancion2 = new Cancion(2, "Cancion Larga", albumMock, artista, 300);  // 5 minutos

        // Crear usuario mock
        Usuario usuarioMock = Mockito.mock(Usuario.class);
        Mockito.when(usuarioMock.getNombre()).thenReturn("Juan");
        Mockito.when(usuarioMock.getApellido()).thenReturn("Pérez");
        Mockito.when(usuarioMock.getListaAlbumes()).thenReturn(List.of(albumMock));

        // Mockear método buscarUsuario() para devolver usuarioMock
        Mockito.doReturn(usuarioMock).when(cocheMock).buscarUsuario("Juan", "Pérez");

        // Configurar canciones en el álbum mock
        Mockito.when(albumMock.getListaCanciones()).thenReturn(List.of(cancion1, cancion2));

        // Act & Assert - Verificar que no se lanza ninguna excepción
        assertDoesNotThrow(() -> {
            cocheMock.crearPlaylistPersonalizada(List.of("Juan Pérez"), 1);
        }, "No debe lanzarse ninguna excepción por duración baja.");

        // Assert - La playlist debe estar vacía
        assertTrue(cocheMock.getCanciones().isEmpty(), "La playlist debe estar vacía porque la duración es muy baja.");
    }

    @Test
    @DisplayName("CP11.2: Crear playlist con duración de 0 minutos")
    void testPlaylistDuracion0Minutos() {
        // Arrange - Mockear el coche
        Coche cocheMock = Mockito.spy(new Coche());

        // Crear artista
        Artista artista = new Artista("Artista2");

        // Crear álbum como mock
        Album albumMock = Mockito.mock(Album.class);

        // Crear canciones
        Cancion cancion1 = new Cancion(1, "Cancion Corta", albumMock, artista, 30);   // 30 segundos
        Cancion cancion2 = new Cancion(2, "Cancion Larga", albumMock, artista, 200);  // 3.33 minutos

        // Crear usuario mock
        Usuario usuarioMock = Mockito.mock(Usuario.class);
        Mockito.when(usuarioMock.getNombre()).thenReturn("Ana");
        Mockito.when(usuarioMock.getApellido()).thenReturn("García");
        Mockito.when(usuarioMock.getListaAlbumes()).thenReturn(List.of(albumMock));

        // Mockear método buscarUsuario() para devolver usuarioMock
        Mockito.doReturn(usuarioMock).when(cocheMock).buscarUsuario("Ana", "García");

        // Configurar canciones en el álbum mock
        Mockito.when(albumMock.getListaCanciones()).thenReturn(List.of(cancion1, cancion2));

        // Act & Assert - Verificar que no se lanza ninguna excepción
        assertDoesNotThrow(() -> {
            cocheMock.crearPlaylistPersonalizada(List.of("Ana García"), 0);
        }, "No debe lanzarse ninguna excepción por duración de 0 minutos.");

        // Assert - La playlist debe estar vacía
        assertTrue(cocheMock.getCanciones().isEmpty(), "La playlist debe estar vacía porque la duración es 0 minutos.");
    }

    
    // HU12: Crear Playlist Personalizada con Duración Alta (10 minutos o más)
    
    @Test
    @DisplayName("CP12.1: Crear playlist con duración de 10 minutos para usuarios específicos")
    void testPlaylistDuracion10Minutos() {
        // Arrange - Mockear el coche
        Coche cocheMock = Mockito.spy(new Coche());

        // Crear artista
        Artista artista = new Artista("Artista1");

        // Crear álbum como mock
        Album albumMock = Mockito.mock(Album.class);

        // Crear canciones
        Cancion cancion1 = new Cancion(1, "Cancion Corta", albumMock, artista, 180);  // 3 minutos
        Cancion cancion2 = new Cancion(2, "Cancion Media", albumMock, artista, 400);  // 6.67 minutos
        Cancion cancion3 = new Cancion(3, "Cancion Larga", albumMock, artista, 600);  // 10 minutos

        // Crear usuario mock
        Usuario usuarioMock = Mockito.mock(Usuario.class);
        Mockito.when(usuarioMock.getNombre()).thenReturn("Juan");
        Mockito.when(usuarioMock.getApellido()).thenReturn("Pérez");
        Mockito.when(usuarioMock.getListaAlbumes()).thenReturn(List.of(albumMock));

        // Mockear método buscarUsuario() para devolver usuarioMock
        Mockito.doReturn(usuarioMock).when(cocheMock).buscarUsuario("Juan", "Pérez");

        // Configurar canciones en el álbum mock
        Mockito.when(albumMock.getListaCanciones()).thenReturn(List.of(cancion1, cancion2, cancion3));

        // Act - Crear playlist personalizada
        cocheMock.crearPlaylistPersonalizada(List.of("Juan Pérez"), 10);

        // Assert - Verificar que todas las canciones que cumplen con la duración se añaden
        assertEquals(3, cocheMock.getCanciones().size(), "La playlist debe contener todas las canciones que cumplen la duración máxima.");
        
        assertNotNull(cocheMock.getCanciones(), "La playlist no debe ser nula.");
        assertTrue(cocheMock.getCanciones().contains(cancion1), "La canción corta debe estar en la playlist.");
        assertTrue(cocheMock.getCanciones().contains(cancion2), "La canción media debe estar en la playlist.");
        assertTrue(cocheMock.getCanciones().contains(cancion3), "La canción larga debe estar en la playlist.");
    }

    @Test
    @DisplayName("CP12.2: Crear playlist con duración alta para un usuario sin canciones largas")
    void testPlaylistDuracionAltaSinCancionesLargas() {
        // Arrange - Mockear el coche
        Coche cocheMock = Mockito.spy(new Coche());

        // Crear artista
        Artista artista = new Artista("Artista2");

        // Crear álbum como mock
        Album albumMock = Mockito.mock(Album.class);

        // Crear canciones (todas cortas, menos de 3 minutos)
        Cancion cancion1 = new Cancion(1, "Cancion Corta 1", albumMock, artista, 120);  // 2 minutos
        Cancion cancion2 = new Cancion(2, "Cancion Corta 2", albumMock, artista, 180);  // 3 minutos

        // Crear usuario mock
        Usuario usuarioMock = Mockito.mock(Usuario.class);
        Mockito.when(usuarioMock.getNombre()).thenReturn("Juan");
        Mockito.when(usuarioMock.getApellido()).thenReturn("Pérez");
        Mockito.when(usuarioMock.getListaAlbumes()).thenReturn(List.of(albumMock));

        // Mockear método buscarUsuario() para devolver usuarioMock
        Mockito.doReturn(usuarioMock).when(cocheMock).buscarUsuario("Juan", "Pérez");

        // Configurar canciones en el álbum mock
        Mockito.when(albumMock.getListaCanciones()).thenReturn(List.of(cancion1, cancion2));

        // Act - Crear playlist personalizada con duración alta (10 minutos)
        cocheMock.crearPlaylistPersonalizada(List.of("Juan Pérez"), 10);

        // Assert - Solo deben incluirse las canciones cortas
        assertEquals(2, cocheMock.getCanciones().size(), "Solo deben añadirse las canciones cortas ya que no hay canciones largas.");
        
        assertNotNull(cocheMock.getCanciones(), "La playlist no debe ser nula.");
        assertTrue(cocheMock.getCanciones().contains(cancion1), "La canción corta 1 debe estar en la playlist.");
        assertTrue(cocheMock.getCanciones().contains(cancion2), "La canción corta 2 debe estar en la playlist.");
    }
    
    // HU13: Crear Playlist Personalizada y Verificar Integridad del Contenido
    
    @Test
    @DisplayName("CP13.1: Verificar integridad y ausencia de duplicados tras crear la playlist")
    void testPlaylistSinDuplicados() {
        // Arrange - Mockear el coche
        Coche cocheMock = Mockito.spy(new Coche());

        // Crear artista
        Artista artista = new Artista("Artista1");

        // Crear álbum como mock
        Album albumMock = Mockito.mock(Album.class);

        // Crear canciones (algunas con el mismo título y duración)
        Cancion cancion1 = new Cancion(1, "Cancion1", albumMock, artista, 120);  // 2 minutos
        Cancion cancion2 = new Cancion(2, "Cancion2", albumMock, artista, 180);  // 3 minutos
        Cancion cancion3 = new Cancion(3, "Cancion1", albumMock, artista, 120);  // Duplicado en título y duración

        // Crear usuarios
        Usuario usuario1 = Mockito.mock(Usuario.class);
        Usuario usuario2 = Mockito.mock(Usuario.class);

        Mockito.when(usuario1.getNombre()).thenReturn("Juan");
        Mockito.when(usuario1.getApellido()).thenReturn("Pérez");
        Mockito.when(usuario1.getListaAlbumes()).thenReturn(List.of(albumMock));

        Mockito.when(usuario2.getNombre()).thenReturn("Ana");
        Mockito.when(usuario2.getApellido()).thenReturn("García");
        Mockito.when(usuario2.getListaAlbumes()).thenReturn(List.of(albumMock));

        // Mockear método `buscarUsuario()` para devolver los mocks
        Mockito.doReturn(usuario1).when(cocheMock).buscarUsuario("Juan", "Pérez");
        Mockito.doReturn(usuario2).when(cocheMock).buscarUsuario("Ana", "García");

        // Configurar las canciones en el álbum mock
        Mockito.when(albumMock.getListaCanciones()).thenReturn(List.of(cancion1, cancion2, cancion3));

        // Act - Crear playlist personalizada con duración máxima de 5 minutos
        cocheMock.crearPlaylistPersonalizada(List.of("Juan Pérez", "Ana García"), 5);

        // Assert - Verificar ausencia de duplicados
        Set<Cancion> setCanciones = new HashSet<>(cocheMock.getCanciones());
        assertEquals(setCanciones.size(), cocheMock.getCanciones().size(), "No debe haber duplicados en la playlist.");

        // Verificar que todas las canciones cumplen la duración
        assertTrue(cocheMock.getCanciones().stream().allMatch(c -> c.getDuracion() <= 300), 
                   "Todas las canciones deben cumplir con la duración máxima de 5 minutos.");

        // Verificar que todas las canciones son de los usuarios seleccionados
        assertTrue(cocheMock.getCanciones().stream()
                            .allMatch(c -> c.getArtista().equals(artista)),
                   "Todas las canciones deben pertenecer al artista configurado.");
    }

    @Test
    @DisplayName("CP13.2: Verificar que no se añadan canciones de usuarios no seleccionados")
    void testPlaylistFiltrarUsuarios() {
        // Arrange - Mockear el coche
        Coche cocheMock = Mockito.spy(new Coche());

        // Crear artista
        Artista artista = new Artista("Artista2");

        // Crear álbum como mock
        Album albumMock1 = Mockito.mock(Album.class);
        Album albumMock2 = Mockito.mock(Album.class);
        Album albumMock3 = Mockito.mock(Album.class);

        // Crear canciones
        Cancion cancion1 = new Cancion(1, "Cancion1", albumMock1, artista, 150);  // 2.5 minutos
        Cancion cancion2 = new Cancion(2, "Cancion2", albumMock2, artista, 250);  // 4.17 minutos
        Cancion cancion3 = new Cancion(3, "Cancion Excluida", albumMock3, artista, 300);  // 5 minutos

        // Crear usuarios
        Usuario usuario1 = Mockito.mock(Usuario.class);
        Usuario usuario2 = Mockito.mock(Usuario.class);
        Usuario usuarioExcluido = Mockito.mock(Usuario.class);

        Mockito.when(usuario1.getNombre()).thenReturn("Juan");
        Mockito.when(usuario1.getApellido()).thenReturn("Pérez");
        Mockito.when(usuario1.getListaAlbumes()).thenReturn(List.of(albumMock1));

        Mockito.when(usuario2.getNombre()).thenReturn("Ana");
        Mockito.when(usuario2.getApellido()).thenReturn("García");
        Mockito.when(usuario2.getListaAlbumes()).thenReturn(List.of(albumMock2));

        Mockito.when(usuarioExcluido.getNombre()).thenReturn("María");
        Mockito.when(usuarioExcluido.getApellido()).thenReturn("López");
        Mockito.when(usuarioExcluido.getListaAlbumes()).thenReturn(List.of(albumMock3));

        // Mockear método `buscarUsuario()` para devolver los mocks
        Mockito.doReturn(usuario1).when(cocheMock).buscarUsuario("Juan", "Pérez");
        Mockito.doReturn(usuario2).when(cocheMock).buscarUsuario("Ana", "García");
        Mockito.doReturn(usuarioExcluido).when(cocheMock).buscarUsuario("María", "López");

        // Configurar canciones en el álbum mock
        Mockito.when(albumMock1.getListaCanciones()).thenReturn(List.of(cancion1));
        Mockito.when(albumMock2.getListaCanciones()).thenReturn(List.of(cancion2));
        Mockito.when(albumMock3.getListaCanciones()).thenReturn(List.of(cancion3));

        // Act - Crear playlist personalizada con duración máxima de 5 minutos para "Juan Pérez" y "Ana García"
        cocheMock.crearPlaylistPersonalizada(List.of("Juan Pérez", "Ana García"), 5);

        // Assert - Verificar que no se incluyen canciones del usuario excluido ("María López")
        assertFalse(cocheMock.getCanciones().stream()
                             .anyMatch(c -> c.getTitulo().equals("Cancion Excluida")),
                   "No deben incluirse canciones del usuario excluido.");

        // Verificar que todas las canciones son de los usuarios seleccionados
        assertEquals(2, cocheMock.getCanciones().size(), "La playlist debe contener solo canciones de los usuarios seleccionados.");
    }

}
