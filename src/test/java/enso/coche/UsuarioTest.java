package enso.coche;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UsuarioTest {
	
	private Usuario usuario;

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		usuario = new Usuario("Juan", "Pérez");

        // Añadir canciones de dos álbumes
        usuario.agregarCancion(new Cancion(1, "Cancion1", "Album1", "Artista1", 180));
        usuario.agregarCancion(new Cancion(2, "Cancion2", "Album1", "Artista1", 200));
        usuario.agregarCancion(new Cancion(3, "Cancion3", "Album2", "Artista2", 240));
        usuario.agregarCancion(new Cancion(4, "Cancion4", "Album2", "Artista2", 220));
	}

	@Test
	void testObtenerSiguienteCancion() {
        Cancion primera = usuario.obtenerSiguienteCancion();
        assertEquals("Cancion1", primera.getTitulo());

        Cancion segunda = usuario.obtenerSiguienteCancion();
        assertEquals("Cancion2", segunda.getTitulo());

        Cancion tercera = usuario.obtenerSiguienteCancion();
        assertEquals("Cancion3", tercera.getTitulo());

        Cancion cuarta = usuario.obtenerSiguienteCancion();
        assertEquals("Cancion4", cuarta.getTitulo());

        // Debe volver a la primera canción del primer álbum
        Cancion quinta = usuario.obtenerSiguienteCancion();
        assertEquals("Cancion1", quinta.getTitulo());
    }

}