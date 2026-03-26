import java.util.*;
import java.io.*;
import java.time.LocalDateTime;

/**
 * Klasa zarządzająca wypożyczalnią samochodów.
 */
public class Wypozyczalnia {
    public List<Samochod> auta = new ArrayList<>();

    /** Generuje unikalne ID dla nowego samochodu */
    public int generujId() {
        int max = 0;
        for (Samochod s : auta) if (s.id > max) max = s.id;
        return max + 1;
    }

    /** Szuka samochodu po ID */
    public Samochod znajdzPoId(int id) {
        for (Samochod s : auta)
            if (s.id == id) return s;
        return null;
    }

    /** Dodaje nowy samochód do wypożyczalni */
    public void dodaj(Samochod s) {
        auta.add(s);
        System.out.println("Dodano!");
    }

    /** Pokazuje wszystkie dostępne samochody */
    public void pokaz() {
        for (Samochod s : auta)
            if (!s.wypozyczony) System.out.println(s);
    }

    /** Pokazuje samochody wypożyczone przez użytkownika */
    public void pokazwypozyczone(Uzytkownik user) {
        for (Samochod s : user.mojeAuta)
            System.out.println(s);
    }

    /** Pokazuje samochody wypożyczone przez konkretnego użytkownika */
    public void pokazUzytkownika(String user) {
        boolean found = false;
        for (Samochod s : auta) {
            if (s.wypozyczyl.equals(user)) {
                System.out.println(s);
                found = true;
            }
        }
        if (!found) System.out.println("Brak wypożyczonych aut.");
    }

    /** Wypożycza samochód dla użytkownika */
    public void wypozycz(int id, Uzytkownik user) throws Exception {
        Samochod s = znajdzPoId(id);
        if (s != null && !s.wypozyczony) {
            s.wypozyczony = true;
            s.wypozyczyl = user.login;

            // Historia w pamięci
            s.historia.add("Wypożyczony przez: " + user.login + " | " + LocalDateTime.now());

            // Historia do pliku
            HistoriaIO.zapiszWpis(s.id, user.login, "WYPOŻYCZONE");

            user.dodajAuto(s);
            zapisz();
            System.out.println("Wypożyczono!");
        } else {
            System.out.println("Auto niedostępne!");
        }
    }

    /** Zwraca samochód od użytkownika */
    public void zwroc(int id, Uzytkownik user) throws Exception {
        Samochod s = znajdzPoId(id);
        if (s != null && s.wypozyczyl.equals(user.login)) {
            s.wypozyczony = false;

            // Historia w pamięci
            s.historia.add("Zwrócony przez: " + user.login + " | " + LocalDateTime.now());

            // Historia do pliku
            HistoriaIO.zapiszWpis(s.id, user.login, "ZWROT");

            s.wypozyczyl = "";
            user.usunAuto(s);
            zapisz();
            System.out.println("Zwrócono!");
        }
    }

    /** Usuwa samochód z wypożyczalni jeśli nie jest wypożyczony */
    public void usun(int id) throws Exception {
        Samochod s = znajdzPoId(id);
        if (s != null && !s.wypozyczony) {
            auta.remove(s);
            zapisz();
            System.out.println("Usunięto!");
        }
    }

    /** Wczytuje samochody z pliku */
    public void wczytaj() {
        auta = SamochodDAO.wczytaj();
    }

    /** Zapisuje samochody do pliku */
    public void zapisz() {
        SamochodDAO.zapisz(auta);
    }

    /** Sortuje auta po marce alfabetycznie */
    public void sortuj() {
        auta.sort(Comparator.comparing(s -> s.marka.toLowerCase()));
    }

    /** Filtruje auta po klasie */
    public void filtrujPoKlasie(String klasa) {
        for (Samochod s : auta)
            if (s.klasa.name().equalsIgnoreCase(klasa))
                System.out.println(s);
    }

    /** Filtruje auta po klasie i statusie wypożyczenia */
    public void filtrujPoKlasieIWypozyczeniu(String klasa, boolean wypozyczone) {
        for (Samochod s : auta)
            if (s.klasa.name().equalsIgnoreCase(klasa) && s.wypozyczony == wypozyczone)
                System.out.println(s);
    }

    /** Statystyki dostępnych i wypożyczonych aut */
    public void statystyki() {
        int d = 0, w = 0;
        for (Samochod s : auta)
            if (s.wypozyczony) w++; else d++;
        System.out.println("Dostępne: " + d + ", Wypożyczone: " + w);
    }

    /** Ile samochodów jest w danej klasie */
    public int ileSamochodowWKlasie(String klasa) {
        int c = 0;
        for (Samochod s : auta)
            if (s.klasa.name().equalsIgnoreCase(klasa)) c++;
        return c;
    }

    /** Szuka samochody po marce i modelu (częściowa zgodność) */
    public void szukaj(String fraza) {
        boolean found = false;
        for (Samochod s : auta) {
            if (s.marka.toLowerCase().contains(fraza.toLowerCase()) ||
                    s.model.toLowerCase().contains(fraza.toLowerCase())) {
                System.out.println(s);
                found = true;
            }
        }
        if (!found) System.out.println("Brak wyników.");
    }

    /** Pokazuje historię wypożyczeń samochodu */
    public void pokazHistorie(int id) {
        Samochod s = znajdzPoId(id);
        if (s == null) {
            System.out.println("Nie znaleziono auta.");
            return;
        }
        if (s.historia.isEmpty()) {
            System.out.println("Brak historii.");
            return;
        }
        for (String h : s.historia)
            System.out.println(h);
    }

    /** Edytuje samochód (dla admina) – przy użyciu Scannera, zgodnie z menu */
    public void edytujAuto(int id, Scanner sc) throws Exception {
        Samochod s = znajdzPoId(id);
        if (s == null) {
            System.out.println("Nie znaleziono auta.");
            return;
        }
        if (s.wypozyczony) {
            System.out.println("Nie można edytować auta wypożyczonego.");
            return;
        }

        System.out.print("Nowa marka: "); String marka = sc.nextLine();
        System.out.print("Nowy model: "); String model = sc.nextLine();
        System.out.print("Nowa skrzynia: "); SkrzyniaBiegow skrzynia = SkrzyniaBiegow.valueOf(sc.nextLine().toUpperCase());
        System.out.print("Nowa klasa: "); KlasaSamochodu klasa = KlasaSamochodu.valueOf(sc.nextLine().toUpperCase());
        System.out.print("Nowa liczba miejsc: "); int miejsca = sc.nextInt(); sc.nextLine();

        s.edytuj(marka, model, skrzynia, klasa, miejsca);
        zapisz();
        System.out.println("Zaktualizowano auto!");
    }
}