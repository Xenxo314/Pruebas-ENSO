package coche;

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
		
		Artista artista1 = new Artista("Artista1");
	    Artista artista2 = new Artista("Artista2");
	    
	    Album album1 = new Album("Album1", artista1);
	    Album album2 = new Album("Album2", artista2);
	    
	    // Añadir canciones de dos álbumes
        usuario.agregarAlbum(album1);
        usuario.agregarCancion(new Cancion(1, "Cancion1", album1, artista1, 180));
        usuario.agregarCancion(new Cancion(2, "Cancion2", album1, artista1, 200));
        
        usuario.agregarAlbum(album2);
        usuario.agregarCancion(new Cancion(3, "Cancion3", album2, artista2, 240));
        usuario.agregarCancion(new Cancion(3, "Cancion4", album2, artista2, 220));
	    

        
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