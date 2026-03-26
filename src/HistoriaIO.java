import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/** Klasa do zapisu i odczytu historii wypożyczeń z pliku historia.txt */
public class HistoriaIO {
    private static final String PLIK_HISTORIA = "historia.txt";

    /** Zapisuje pojedynczy wpis do historii */
    public static void zapiszWpis(int id_auto, String login, String typ) throws IOException {
        String linia = id_auto + "," + login + "," + typ + "," + LocalDateTime.now() + "\n";
        Files.write(Paths.get(PLIK_HISTORIA), linia.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /** Wczytuje wpisy z pliku wejściowego i zapisuje do historii */
    public static void zapiszZPliku(String plikWejsciowy) throws IOException {
        List<String> linie = Files.readAllLines(Paths.get(plikWejsciowy));
        for (String linia : linie) {
            // Zakładamy format pliku: id_auto,login,typ
            String[] czesci = linia.split(",");
            if (czesci.length >= 3) {
                int id_auto = Integer.parseInt(czesci[0].trim());
                String login = czesci[1].trim();
                String typ = czesci[2].trim();
                zapiszWpis(id_auto, login, typ);
            }
        }
    }

    public static void main(String[] args) {
        try {
            zapiszZPliku("wpisy.txt");
            System.out.println("Wpisy zostały zapisane do historia.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}