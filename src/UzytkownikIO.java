import java.io.*;
import java.nio.file.*;
import java.util.*;

class UzytkownikIO {

    static void zapisz(Uzytkownik uzytkownik) {
        try {
            Files.write(
                    Path.of("bazaDanych.txt"),
                    (uzytkownik.login + "," + uzytkownik.haslo + "," + uzytkownik.rola + "\n").getBytes(),
                    StandardOpenOption.APPEND
            );
        } catch (IOException ioException) {
            System.out.println("Błąd IO (użytkownicy): " + ioException.getMessage());
        }
    }

    static List<Uzytkownik> wczytaj() {
        List<Uzytkownik> lista = new ArrayList<>();

        try {
            if (!Files.exists(Path.of("bazaDanych.txt")))
                return lista;

            for (String line : Files.readAllLines(Path.of("bazaDanych.txt"))) {
                String[] dane = line.split(",");
                if (dane.length == 3) {
                    lista.add(new Uzytkownik(dane[0], dane[1], dane[2]));
                }
            }
        } catch (IOException ioException) {
            System.out.println("Błąd IO (użytkownicy): " + ioException.getMessage());
        }

        return lista;
    }
}