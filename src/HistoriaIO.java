import java.io.*;
import java.nio.file.*;
import java.util.*;

public class HistoriaIO {

    private static final String PLIK_HISTORIA = "historia.txt";

    public static void zapiszWpis(int idSamochodu, String uzytkownik, String akcja) {
        String wpis = idSamochodu + "," + uzytkownik + "," + akcja + "," + java.time.LocalDateTime.now();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PLIK_HISTORIA, true))) {
            writer.write(wpis);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Błąd zapisu historii: " + e.getMessage());
        }
    }

    public static List<String> wczytajHistorie(int idSamochodu) {
        List<String> historia = new ArrayList<>();
        try {
            for (String linia : Files.readAllLines(Paths.get(PLIK_HISTORIA))) {
                String[] czesci = linia.split(",", 4);
                if (czesci.length >= 4 && Integer.parseInt(czesci[0].trim()) == idSamochodu) {
                    historia.add(linia);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Błąd odczytu historii: " + e.getMessage());
        }
        return historia;
    }
}