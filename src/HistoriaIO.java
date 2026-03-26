import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa do zapisu i odczytu historii wypożyczeń z pliku historia.txt
 */
public class HistoriaIO {

    private static final Path PLIK_HISTORIA = Paths.get("historia.txt");

    /**
     * Zapisuje wpis historii wypożyczenia/zwrotu auta.
     * @param id_auto ID auta
     * @param login login użytkownika
     * @param typ typ operacji ("WYPOŻYCZONE" / "ZWROT")
     */
    public static void zapiszWpis(int id_auto, String login, String typ) throws IOException {
        String linia = id_auto + "," + login + "," + typ + "," + LocalDateTime.now() + "\n";
        Files.write(PLIK_HISTORIA, linia.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * Wczytuje wszystkie wpisy historii.
     * @return lista linii z pliku historia.txt
     */
    public static List<String> wczytaj() throws IOException {
        if (!Files.exists(PLIK_HISTORIA)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(PLIK_HISTORIA);
    }
}