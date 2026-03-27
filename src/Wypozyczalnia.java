import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;

/**
 * Klasa zarządzająca wypożyczalnią samochodów.
 */
public class Wypozyczalnia {

    private List<Samochod> samochody = new ArrayList<>();

    public List<Samochod> getSamochody() {
        return samochody;
    }

    public int generujId() {
        int maksymalneId = 0;
        for (Samochod samochod : samochody) {
            if (samochod.id > maksymalneId) {
                maksymalneId = samochod.id;
            }
        }
        return maksymalneId + 1;
    }

    public Samochod znajdzPoId(int id) {
        for (Samochod samochod : samochody) {
            if (samochod.id == id) {
                return samochod;
            }
        }
        return null;
    }

    private Samochod pobierzAuto(int id) {
        Samochod znalezionySamochod = znajdzPoId(id);
        if (znalezionySamochod == null) {
            throw new IllegalArgumentException("Nie ma auta o ID: " + id);
        }
        return znalezionySamochod;
    }

    public String dodajSamochod(Samochod samochod) {
        samochody.add(samochod);
        zapiszAutaDoPliku();
        return "Dodano auto!";
    }

    public List<Samochod> pobierzDostepneSamochody() {
        List<Samochod> dostepneSamochody = new ArrayList<>();
        for (Samochod samochod : samochody) {
            if (!samochod.wypozyczony) {
                dostepneSamochody.add(samochod);
            }
        }
        return dostepneSamochody;
    }

    public String wypozyczSamochod(int id, Uzytkownik uzytkownik) throws Exception {
        Samochod samochod = pobierzAuto(id);

        if (samochod.wypozyczony) {
            return "Auto niedostępne!";
        }

        samochod.wypozyczony = true;
        samochod.wypozyczyl = uzytkownik.login;

        samochod.historia.add("Wypożyczony przez: " + uzytkownik.login + " | " + LocalDateTime.now());
        HistoriaIO.zapiszWpis(samochod.id, uzytkownik.login, "WYPOŻYCZONE");

        uzytkownik.dodajAuto(samochod);
        zapiszAutaDoPliku();

        return "Wypożyczono!";
    }

    public String zwrocAuto(int id, Uzytkownik uzytkownik) throws Exception {
        Samochod samochod = pobierzAuto(id);

        if (!samochod.wypozyczyl.equals(uzytkownik.login)) {
            return "To nie Twoje auto!";
        }

        samochod.wypozyczony = false;
        samochod.wypozyczyl = "";

        samochod.historia.add("Zwrócony przez: " + uzytkownik.login + " | " + LocalDateTime.now());
        HistoriaIO.zapiszWpis(samochod.id, uzytkownik.login, "ZWROT");

        uzytkownik.usunAuto(samochod);
        zapiszAutaDoPliku();

        return "Zwrócono!";
    }

    public String usun(int id) throws Exception {
        Samochod samochod = pobierzAuto(id);

        if (samochod.wypozyczony) {
            return "Nie można usunąć wypożyczonego auta!";
        }

        samochody.remove(samochod);
        zapiszAutaDoPliku();

        return "Usunięto!";
    }

    public void wczytajZPliku() {
        samochody = SamochodIO.wczytaj();
    }

    public void zapiszAutaDoPliku() {
        SamochodIO.zapisz(samochody);
    }

    public void sortujAutaAlfabetycznie() {
        samochody.sort(Comparator.comparing(samochod -> samochod.marka.toLowerCase()));
    }

    public List<Samochod> filtrujPoKlasie(String nazwaKlasy) {
        List<Samochod> wynik = new ArrayList<>();
        for (Samochod samochod : samochody) {
            if (samochod.klasa.name().equalsIgnoreCase(nazwaKlasy)) {
                wynik.add(samochod);
            }
        }
        return wynik;
    }

    public List<Samochod> filtrujPoKlasieIWypozyczeniu(String nazwaKlasy, boolean czyWypozyczony) {
        List<Samochod> wynik = new ArrayList<>();
        for (Samochod samochod : samochody) {
            if (samochod.klasa.name().equalsIgnoreCase(nazwaKlasy)
                    && samochod.wypozyczony == czyWypozyczony) {
                wynik.add(samochod);
            }
        }
        return wynik;
    }

    public String statystyki() {
        int liczbaDostepnych = 0;
        int liczbaWypozyczonych = 0;

        for (Samochod samochod : samochody) {
            if (samochod.wypozyczony) {
                liczbaWypozyczonych++;
            } else {
                liczbaDostepnych++;
            }
        }

        return "Dostępne: " + liczbaDostepnych + ", Wypożyczone: " + liczbaWypozyczonych;
    }

    public int ileSamochodowWKlasie(String nazwaKlasy) {
        int licznik = 0;
        for (Samochod samochod : samochody) {
            if (samochod.klasa.name().equalsIgnoreCase(nazwaKlasy)) {
                licznik++;
            }
        }
        return licznik;
    }

    public List<Samochod> szukaj(String fraza) {
        List<Samochod> wynik = new ArrayList<>();

        for (Samochod samochod : samochody) {
            if (samochod.marka.toLowerCase().contains(fraza.toLowerCase()) ||
                    samochod.model.toLowerCase().contains(fraza.toLowerCase())) {
                wynik.add(samochod);
            }
        }

        return wynik;
    }

    public List<String> pobierzHistorie(int id) {
        Samochod samochod = znajdzPoId(id);

        if (samochod == null) {
            throw new IllegalArgumentException("Nie znaleziono auta.");
        }

        List<String> historia = new ArrayList<>();

        try {
            for (String linia : Files.readAllLines(Paths.get("historia.txt"))) {
                String[] czesci = linia.split(",", 4);
                if (czesci.length >= 4 && Integer.parseInt(czesci[0].trim()) == id) {
                    historia.add(linia);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Błąd historii: " + e.getMessage());
        }

        return historia;
    }
}