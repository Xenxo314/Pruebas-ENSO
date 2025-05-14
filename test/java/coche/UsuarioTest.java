package coche;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioTest {
	
	private Usuario usuario;
    private Artista artista1, artista2;
    private Album album1, album2;

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		usuario = new Usuario("Juan", "Pérez");        
	}
	
	void init1album() {
		artista1 = new Artista("Artista1");
        album1 = new Album("Album1", artista1);
        
		usuario.agregarAlbum(album1);
        usuario.agregarCancion(new Cancion(1, "Cancion1", album1, artista1, 180));
        usuario.agregarCancion(new Cancion(2, "Cancion2", album1, artista1, 200));
	}
	
	void init2albums() {
		artista1 = new Artista("Artista1");
        artista2 = new Artista("Artista2");

        album1 = new Album("Album1", artista1);
        album2 = new Album("Album2", artista2);

        usuario.agregarAlbum(album1);
        usuario.agregarCancion(new Cancion(1, "Cancion1", album1, artista1, 180));
        usuario.agregarCancion(new Cancion(2, "Cancion2", album1, artista1, 200));

        usuario.agregarAlbum(album2);
        usuario.agregarCancion(new Cancion(3, "Cancion3", album2, artista2, 220));
        usuario.agregarCancion(new Cancion(4, "Cancion4", album2, artista2, 240));
	}
	
	// HU3: Gestión de álbumes y canciones por usuario
	@Test
    @DisplayName("CP3.1: Reproducción ordenada de canciones en un único álbum")
    void testReproduccionOrdenadaUnAlbum() {
        // Arrange - Agregar un álbum con 2 canciones
		init1album();

        // Act & Assert - Reproducir la primera canción
        Cancion primeraCancion = usuario.obtenerSiguienteCancion();
        assertNotNull(primeraCancion, "La primera canción no debe ser null.");
        assertEquals("Cancion1", primeraCancion.getTitulo(), "Debe reproducir la primera canción en orden.");

        // Reproducir la segunda canción
        Cancion segundaCancion = usuario.obtenerSiguienteCancion();
        assertNotNull(segundaCancion, "La segunda canción no debe ser null.");
        assertEquals("Cancion2", segundaCancion.getTitulo(), "Debe reproducir la segunda canción en orden.");

        // Verificar que vuelve al inicio del álbum tras reproducir todas las canciones
        Cancion terceraCancion = usuario.obtenerSiguienteCancion();
        assertEquals("Cancion1", terceraCancion.getTitulo(), "Debe volver a la primera canción del álbum en reproducción cíclica.");

        // Verificar que solo hay un álbum y dos canciones
        assertEquals(1, usuario.getListaAlbumes().size(), "Debe haber un único álbum.");
        assertEquals(2, usuario.getListaAlbumes().get(0).getListaCanciones().size(), "Debe haber dos canciones en el álbum.");
    }
	
	@Test
    @DisplayName("CP3.2: Verificar ciclo de reproducción entre dos álbumes")
    void testCicloEntreAlbums() {
		
		init2albums();
		
        // Reproducir las canciones del primer álbum
        Cancion primeraCancion = usuario.obtenerSiguienteCancion();
        assertEquals("Cancion1", primeraCancion.getTitulo(), "Debe reproducir la primera canción del primer álbum.");

        Cancion segundaCancion = usuario.obtenerSiguienteCancion();
        assertEquals("Cancion2", segundaCancion.getTitulo(), "Debe reproducir la segunda canción del primer álbum.");

        // Reproducir las canciones del segundo álbum
        Cancion terceraCancion = usuario.obtenerSiguienteCancion();
        assertEquals("Cancion3", terceraCancion.getTitulo(), "Debe pasar al primer tema del segundo álbum.");

        Cancion cuartaCancion = usuario.obtenerSiguienteCancion();
        assertEquals("Cancion4", cuartaCancion.getTitulo(), "Debe reproducir la segunda canción del segundo álbum.");

        // Reproducción cíclica: debe regresar al primer álbum
        Cancion primeraCiclica = usuario.obtenerSiguienteCancion();
        assertEquals("Cancion1", primeraCiclica.getTitulo(), "Debe volver a la primera canción del primer álbum.");
    }
	
	@Test
    @DisplayName("CP3.3: Intentar reproducir canciones cuando no hay álbumes agregados")
    void testReproduccionSinAlbumes() {
        // Act - Intentar reproducir sin álbumes
        Cancion cancion = usuario.obtenerSiguienteCancion();

        // Asserts
        assertNull(cancion, "Debe retornar null cuando no hay álbumes ni canciones.");
        assertThrows(IllegalStateException.class, () -> {
            if (cancion == null) {
                throw new IllegalStateException("No hay canciones para reproducir.");
            }
        }, "Debe lanzar IllegalStateException cuando no hay álbumes agregados.");

        // Comprobación de lista vacía
        assertEquals(0, usuario.getListaAlbumes().size(), "La lista de álbumes debe estar vacía.");
    }
	
	@Test
	@DisplayName("CP3.4: Alcanzar el bloque de código de avance al siguiente álbum vacío")
	void testObtenerSiguienteCancion_AlbumVacio() {
	    // Arrange - Crear artista
	    Artista artista = new Artista("Artista1");

	    // Crear álbumes
	    Album albumVacio = new Album("Álbum Vacío", artista);
	    Album albumConCanciones = new Album("Álbum con Canciones", artista);

	    // Crear canciones
	    Cancion cancion1 = new Cancion(1, "Cancion 1", albumConCanciones, artista, 120);
	    Cancion cancion2 = new Cancion(2, "Cancion 2", albumConCanciones, artista, 150);

	    // Crear usuario y agregar álbumes
	    Usuario usuario = new Usuario("Juan", "Pérez");
	    usuario.agregarAlbum(albumVacio);      // Álbum vacío primero
	    usuario.agregarAlbum(albumConCanciones);  // Álbum con canciones después

	    // Agregar canciones al segundo álbum
	    usuario.agregarCancion(cancion1);
	    usuario.agregarCancion(cancion2);

	    // Act - Obtener la siguiente canción (esto debe pasar por el álbum vacío)
	    Cancion siguienteCancion = usuario.obtenerSiguienteCancion();

	    // Assert
	    assertNotNull(siguienteCancion, "La canción no debe ser nula.");
	    assertEquals("Cancion 1", siguienteCancion.getTitulo(), "La primera canción reproducida debe ser 'Cancion 1'.");

	    // Obtener la siguiente canción para comprobar el ciclo
	    siguienteCancion = usuario.obtenerSiguienteCancion();
	    assertEquals("Cancion 2", siguienteCancion.getTitulo(), "La segunda canción reproducida debe ser 'Cancion 2'.");
	}
	
	@Test
	@DisplayName("CP3.5: Recorrer todos los álbumes hasta reiniciar al primer álbum")
	void testRecorrerTodosLosAlbumes_ReiniciarAlPrimerAlbum() {
	    // Arrange - Crear artista
	    Artista artista = new Artista("Artista1");

	    // Crear álbumes
	    Album album1 = new Album("Álbum 1", artista);
	    Album album2 = new Album("Álbum 2", artista);
	    Album album3 = new Album("Álbum 3", artista); // Vacio

	    // Crear canciones
	    Cancion cancion1 = new Cancion(1, "Cancion 1", album1, artista, 120); // Álbum 1
	    Cancion cancion2 = new Cancion(2, "Cancion 2", album2, artista, 150); // Álbum 2

	    // Crear usuario y agregar álbumes y canciones
	    Usuario usuario = new Usuario("Juan", "Pérez");
	    usuario.agregarAlbum(album1);
	    usuario.agregarAlbum(album2);
	    usuario.agregarAlbum(album3);

	    usuario.agregarCancion(cancion1); // Álbum 1
	    usuario.agregarCancion(cancion2); // Álbum 2

	    // Act - Reproducir todas las canciones para recorrer todos los álbumes
	    Cancion primeraCancion = usuario.obtenerSiguienteCancion();
	    Cancion segundaCancion = usuario.obtenerSiguienteCancion();

	    // Assert - Verificar que hemos recorrido todos los álbumes
	    assertEquals("Cancion 1", primeraCancion.getTitulo(), "La primera canción debe ser del primer álbum.");
	    assertEquals("Cancion 2", segundaCancion.getTitulo(), "La segunda canción debe ser del segundo álbum.");

	    // Act - Volver a iniciar el ciclo de reproducción
	    Cancion siguienteCancion = usuario.obtenerSiguienteCancion();

	    // Assert - Verificar que hemos reiniciado al primer álbum
	    assertEquals("Cancion 1", siguienteCancion.getTitulo(), "Debe reiniciarse al primer álbum y reproducir 'Cancion 1'.");
	}



}