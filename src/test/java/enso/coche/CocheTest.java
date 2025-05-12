package enso.coche;
	
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class CocheTest {

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testLeerCSV() {
        Coche coche = new Coche();
        String rutaArchivo = "src/main/resources/discos_usuarios.csv";

        // Capturar la salida estándar
        ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();
        PrintStream salidaOriginal = System.out;
        System.setOut(new PrintStream(salidaCapturada));

        coche.leer_csv(rutaArchivo);

        // Restaurar la salida estándar
        System.setOut(salidaOriginal);

        // Comprobación básica: que haya al menos un usuario cargado
        assertFalse(coche.getPasajeros().isEmpty(), "No se ha cargado ningún usuario.");

        // Comprobar que se le han asignado canciones a un usuario
        assertFalse(coche.getPasajeros().get(0).getCancionesPorAlbum().isEmpty(), "El usuario " + coche.getPasajeros().get(0).getNombre() + " no tiene canciones asociadas");
    }
	
	

}
