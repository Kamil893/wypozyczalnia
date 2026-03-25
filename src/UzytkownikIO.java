import java.io.*;
import java.nio.file.*;
import java.util.*;

class UzytkownikIO {

    static void zapisz(Uzytkownik u) {
        try {
            Files.write(
                    Path.of("bazaDanych.txt"),
                    (u.login + "," + u.haslo + "," + u.rola + "\n").getBytes(),
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.out.println("Błąd IO (użytkownicy): " + e.getMessage());
        }
    }

    static List<Uzytkownik> wczytaj() {
        List<Uzytkownik> lista = new ArrayList<>();

        try {
            if (!Files.exists(Path.of("bazaDanych.txt")))
                return lista;

            for (String line : Files.readAllLines(Path.of("bazaDanych.txt"))) {
                String[] d = line.split(",");
                if (d.length == 3) {
                    lista.add(new Uzytkownik(d[0], d[1], d[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Błąd IO (użytkownicy): " + e.getMessage());
        }

        return lista;
    }
}