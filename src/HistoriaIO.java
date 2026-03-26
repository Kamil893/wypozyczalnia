import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/** Klasa do zapisu i odczytu historii wypożyczeń z pliku historia.txt */
public class HistoriaIO {
    private static final String PLIK = "historia.txt";

    public static void zapiszWpis(int id_auto, String login, String typ) throws IOException {
        String linia = id_auto + "," + login + "," + typ + "," + LocalDateTime.now() + "\n";
        Files.write(Paths.get(PLIK), linia.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}