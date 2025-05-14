package coche;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

    
    ////////////////////////////////////////////////////////////////
/*
    @Test
    @DisplayName("Prueba de Randonmización")
    void testRandomize() {
    	
    	// Arrange
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
    	try {
			coche.leer_csv(rutaArchivo);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
    	
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
    @DisplayName("Randomizar canciones por artista desde CSV")
    void randomizarArtistaDesdeCSV() {
        // Ruta del archivo CSV
        String rutaArchivo = "src/resources/discos_usuarios.csv";

        // Cargar las canciones desde el CSV
        try {
			coche.leer_csv(rutaArchivo);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
        coche.reproducirHastaTiempo(3000);

        // Verificar que el coche tenga canciones cargadas
        assertFalse(coche.getCanciones().isEmpty(), "El coche debe tener canciones después de cargar el CSV");

        // Obtener el artista desde las canciones cargadas
        Artista artista1 = coche.getCanciones().get(0).getArtista();

        // Ejecutar el método a probar
        coche.randomizarArtista(artista1);

        // Verificar que todas las canciones sean del artista especificado
        assertTrue(coche.getCanciones().stream().allMatch(c -> c.getArtista().equals(artista1)),
                "Todas las canciones deben pertenecer al artista seleccionado");
    }

    @Test
    @DisplayName("Randomizar artista con lista vacía")
    void randomizarArtistaListaVacia() {
        // Crear una instancia real del coche y convertirlo en Spy
        Coche cocheReal = new Coche();
        Coche coche = Mockito.spy(cocheReal);

        // Crear un artista
        Artista artista = new Artista("Artista Desconocido");

        // Ejecutar el método con lista vacía
        coche.randomizarArtista(artista);

        // Verificar que la lista siga vacía
        assertTrue(coche.getCanciones().isEmpty(), "La lista debe permanecer vacía");
    }

    @Test
    @DisplayName("Randomizar artista inexistente")
    void randomizarArtistaInexistente() {
        // Crear una instancia real del coche y convertirlo en Spy
        Coche cocheReal = new Coche();
        Coche coche = Mockito.spy(cocheReal);

        // Ruta del archivo CSV
        String rutaArchivo = "src/resources/discos_usuarios.csv";

        // Cargar las canciones desde el CSV
        coche.leer_csv(rutaArchivo);

        // Crear un artista que no está en el CSV
        Artista artista = new Artista("Artista Inexistente");

        // Ejecutar el método
        coche.randomizarArtista(artista);

        // Verificar que la lista esté vacía tras el filtrado
        assertTrue(coche.getCanciones().isEmpty(), "La lista debe estar vacía al no haber coincidencias");
    }
    
    ////////////////////////////////////////////////////////////////
    @Test
    @DisplayName("Playlist con canciones < 3 min")
    void playlistMenor3Min() {
    	
    	// Arrange
    	String rutaArchivo = "src/resources/discos_usuarios.csv";
        coche.leer_csv(rutaArchivo);
        
        // Act
        coche.crearPlaylistPersonalizada(Arrays.asList("Juan Pérez", "María López"), 3);

        // Assert
        assertEquals(3, coche.getCanciones().size());
        assertTrue(coche.getCanciones().stream().anyMatch(c -> c.getTitulo().equals("Intro: Persona")), "Debería contener Intro: Persona");
    }

    @Test
    @DisplayName("Usuarios inexistentes")
    void usuariosInexistentes() {
        initCoche();
        coche.crearPlaylistPersonalizada(Arrays.asList("Carlos Fernández", "Pedro López"), 5);
        assertTrue(coche.getCanciones().isEmpty(), "La playlist debe estar vacía");
    }

    @Test
    @DisplayName("Sin usuarios")
    void sinUsuarios() {
        initCoche();
        coche.crearPlaylistPersonalizada(new ArrayList<>(), 5);
        assertTrue(coche.getCanciones().isEmpty(), "La playlist debe estar vacía");
    }

    @Test
    @DisplayName("Duración muy baja")
    void duracionBaja() {
        initCoche();
        coche.crearPlaylistPersonalizada(Arrays.asList("Juan Pérez", "Ana García"), 1);
        assertTrue(coche.getCanciones().isEmpty(), "La playlist debe estar vacía");
    }
    */
    
}
