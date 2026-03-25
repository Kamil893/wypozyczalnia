import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class SamochodDAO {

    static List<Samochod> wczytaj() {
        List<Samochod> auta = new ArrayList<>();

        try {
            if (!Files.exists(Path.of("auta.txt"))) {
                Files.write(Path.of("auta.txt"),
                        List.of("ID,Model,Marka,Skrzynia,Klasa,Miejsca,WypozyczonePrzez"));
                return auta;
            }

            List<String> lines = Files.readAllLines(Path.of("auta.txt"));

            for (int i = 1; i < lines.size(); i++) {
                String[] d = lines.get(i).split(",");

                Samochod s = new Samochod(
                        Integer.parseInt(d[0]),
                        d[2],
                        d[1],
                        SkrzyniaBiegow.valueOf(d[3]),
                        KlasaSamochodu.valueOf(d[4]),
                        Integer.parseInt(d[5])
                );

                if (d.length > 6 && !d[6].isEmpty()) {
                    s.wypozyczony = true;
                    s.wypozyczyl = d[6];
                }

                auta.add(s);
            }

        } catch (Exception e) {
            System.out.println("Błąd IO (auta): " + e.getMessage());
        }

        return auta;
    }

    static void zapisz(List<Samochod> auta) {
        try {
            List<String> out = new ArrayList<>();
            out.add("ID,Model,Marka,Skrzynia,Klasa,Miejsca,WypozyczonePrzez");

            for (Samochod s : auta) out.add(s.toFile());

            Files.write(Path.of("auta.txt"), out, StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.out.println("Błąd IO (auta): " + e.getMessage());
        }
    }
}