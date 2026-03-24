import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/**
 * Klasa reprezentująca samochód w wypożyczalni.
 * Przechowuje dane auta oraz jego status wypożyczenia.
 */
class Samochod {
    int id;
    String marka, model, skrzynia, klasa;
    int miejsca;
    boolean wypozyczony = false;
    String wypozyczyl = "";

    /**
     * Konstruktor samochodu.
     * @param id unikalne ID
     * @param marka marka auta
     * @param model model auta
     * @param skrzynia typ skrzyni biegów
     * @param klasa klasa auta
     * @param miejsca liczba miejsc
     */
    Samochod(int id, String marka, String model, String skrzynia, String klasa, int miejsca) {
        this.id = id;
        this.marka = marka;
        this.model = model;
        this.skrzynia = skrzynia;
        this.klasa = klasa;
        this.miejsca = miejsca;
    }

    /**
     * Reprezentacja tekstowa auta.
     * @return opis auta
     */
    public String toString() {
        return id + ". " + marka + " " + model + " | " + skrzynia + " | " + klasa +
                (wypozyczony ? " | WYPOŻYCZONY przez: " + wypozyczyl : " | DOSTĘPNY");
    }

    /**
     * Format zapisu do pliku.
     * @return linia CSV
     */
    public String toFile() {
        return id + "," + model + "," + marka + "," + skrzynia + "," +
                klasa + "," + miejsca + "," + wypozyczyl;
    }
}

/**
 * Klasa reprezentująca użytkownika systemu.
 */
class Uzytkownik {
    String login, haslo, rola;
    List<Samochod> mojeAuta = new ArrayList<>();

    /**
     * Konstruktor użytkownika.
     * @param login login
     * @param haslo hasło
     * @param rola rola (admin/user)
     */
    Uzytkownik(String login, String haslo, String rola) {
        this.login = login;
        this.haslo = haslo;
        this.rola = rola;
    }

    /**
     * Dodaje auto do listy użytkownika.
     * @param s samochód
     */
    void dodajAuto(Samochod s) {
        mojeAuta.add(s);
    }

    /**
     * Usuwa auto z listy użytkownika.
     * @param s samochód
     */
    void usunAuto(Samochod s) {
        mojeAuta.remove(s);
    }

    /**
     * Wyświetla auta użytkownika.
     */
    void pokazMojeAuta() {
        if (mojeAuta.isEmpty()) {
            System.out.println("Brak wypożyczonych aut.");
            return;
        }
        for (Samochod s : mojeAuta) System.out.println(s);
    }
}

/**
 * Klasa zarządzająca wypożyczalnią.
 */
class Wypozyczalnia {
    List<Samochod> auta = new ArrayList<>();

    /**
     * Generuje nowe ID auta.
     * @return nowe ID
     */
    int generujId() {
        int max = 0;
        for (Samochod s : auta) if (s.id > max) max = s.id;
        return max + 1;
    }

    /**
     * Szuka auta po ID.
     * @param id ID auta
     * @return samochód lub null
     */
    Samochod znajdzPoId(int id) {
        for (Samochod s : auta)
            if (s.id == id) return s;
        return null;
    }

    /**
     * Dodaje auto.
     * @param s samochód
     */
    void dodaj(Samochod s) {
        auta.add(s);
        System.out.println("Dodano!");
    }

    /** Wyświetla dostępne auta. */
    void pokaz() {
        for (Samochod s : auta)
            if (!s.wypozyczony) System.out.println(s);
    }

    /**
     * Wyświetla auta użytkownika.
     * @param user użytkownik
     */
    void pokazwypozyczone(Uzytkownik user) {
        for (Samochod s : user.mojeAuta)
            System.out.println(s);
    }

    /**
     * Pokazuje auta danego użytkownika.
     * @param user login użytkownika
     */
    void pokazUzytkownika(String user) {
        boolean found = false;
        for (Samochod s : auta) {
            if (s.wypozyczyl.equals(user)) {
                System.out.println(s);
                found = true;
            }
        }
        if (!found) System.out.println("Brak wypożyczonych aut.");
    }

    /**
     * Wypożycza auto.
     */
    void wypozycz(int id, Uzytkownik user) throws Exception {
        Samochod s = znajdzPoId(id);
        if (s != null && !s.wypozyczony) {
            s.wypozyczony = true;
            s.wypozyczyl = user.login;
            user.dodajAuto(s);
            zapisz();
            System.out.println("Wypożyczono!");
        } else {
            System.out.println("Auto niedostępne!");
        }
    }

    /**
     * Zwraca auto.
     */
    void zwroc(int id, Uzytkownik user) throws Exception {
        Samochod s = znajdzPoId(id);
        if (s != null && s.wypozyczyl.equals(user.login)) {
            s.wypozyczony = false;
            s.wypozyczyl = "";
            user.usunAuto(s);
            zapisz();
            System.out.println("Zwrócono!");
        }
    }

    /**
     * Usuwa auto.
     */
    void usun(int id) throws Exception {
        Samochod s = znajdzPoId(id);
        if (s != null && !s.wypozyczony) {
            auta.remove(s);
            zapisz();
            System.out.println("Usunięto!");
        }
    }

    /** Wczytuje auta z pliku. */
    void wczytaj() throws Exception {
        if (!Files.exists(Path.of("auta.txt")))
            Files.write(Path.of("auta.txt"),
                    List.of("ID,Model,Marka,Skrzynia,Klasa,Miejsca,WypozyczonePrzez"));

        List<String> lines = Files.readAllLines(Path.of("auta.txt"));
        for (int i = 1; i < lines.size(); i++) {
            String[] d = lines.get(i).split(",");
            Samochod s = new Samochod(Integer.parseInt(d[0]), d[2], d[1], d[3], d[4], Integer.parseInt(d[5]));
            if (d.length > 6 && !d[6].isEmpty()) {
                s.wypozyczony = true;
                s.wypozyczyl = d[6];
            }
            auta.add(s);
        }
    }

    /** Zapisuje auta do pliku. */
    void zapisz() throws Exception {
        List<String> out = new ArrayList<>();
        out.add("ID,Model,Marka,Skrzynia,Klasa,Miejsca,WypozyczonePrzez");
        for (Samochod s : auta) out.add(s.toFile());
        Files.write(Path.of("auta.txt"), out, StandardCharsets.UTF_8);
    }

    /** Sortuje auta po marce. */
    void sortuj() {
        auta.sort(Comparator.comparing(s -> s.marka.toLowerCase()));
    }

    /** Filtrowanie po klasie. */
    void filtrujPoKlasie(String klasa) {
        for (Samochod s : auta)
            if (s.klasa.equalsIgnoreCase(klasa))
                System.out.println(s);
    }

    /** Filtrowanie po klasie i statusie. */
    void filtrujPoKlasieIWypozyczeniu(String klasa, boolean wypozyczone) {
        for (Samochod s : auta)
            if (s.klasa.equalsIgnoreCase(klasa) && s.wypozyczony == wypozyczone)
                System.out.println(s);
    }

    /** Statystyki aut. */
    void statystyki() {
        int d = 0, w = 0;
        for (Samochod s : auta)
            if (s.wypozyczony) w++; else d++;
        System.out.println("Dostępne: " + d + ", Wypożyczone: " + w);
    }

    /** Liczba aut w klasie. */
    int ileSamochodowWKlasie(String klasa) {
        int c = 0;
        for (Samochod s : auta)
            if (s.klasa.equalsIgnoreCase(klasa)) c++;
        return c;
    }
}

/**
 * Główna klasa aplikacji.
 */
public class aplikacjaWypozyczalnia {

    public static void main(String[] args) throws Exception {
        new aplikacjaWypozyczalnia().start();
    }

    /** Start aplikacji */
    void start() throws Exception {
        Scanner sc = new Scanner(System.in);

        if (!Files.exists(Path.of("bazaDanych.txt")))
            Files.createFile(Path.of("bazaDanych.txt"));

        System.out.println("1. Rejestracja\n2. Logowanie");
        int wybor = sc.nextInt();
        sc.nextLine();

        if (wybor == 1) rejestracja(sc);

        Uzytkownik u = logowanie(sc);
        if (u == null) return;

        Wypozyczalnia w = new Wypozyczalnia();
        w.wczytaj();

        for (Samochod s : w.auta)
            if (s.wypozyczyl.equals(u.login)) u.dodajAuto(s);

        menu(sc, w, u);
    }

    /** Rejestracja */
    void rejestracja(Scanner sc) throws IOException {
        System.out.print("Login: "); String l = sc.nextLine();
        System.out.print("Hasło: "); String h = sc.nextLine();
        System.out.print("Rola (admin/user): "); String r = sc.nextLine();

        Files.write(Path.of("bazaDanych.txt"),
                (l + "," + h + "," + r + "\n").getBytes(),
                StandardOpenOption.APPEND);
    }

    /** Logowanie */
    Uzytkownik logowanie(Scanner sc) throws IOException {
        System.out.print("Login: "); String l = sc.nextLine();
        System.out.print("Hasło: "); String h = sc.nextLine();

        for (String line : Files.readAllLines(Path.of("bazaDanych.txt"))) {
            String[] d = line.split(",");
            if (d.length == 3 && d[0].equals(l) && d[1].equals(h))
                return new Uzytkownik(d[0], d[1], d[2]);
        }
        System.out.println("Błędny login!");
        return null;
    }

    /** Menu aplikacji */
    void menu(Scanner sc, Wypozyczalnia w, Uzytkownik u) throws Exception {
        int wybor;
        do {
            System.out.println("\nMENU");

            if (u.rola.equals("admin")) {
                System.out.println("1. Dodaj auto");
                System.out.println("6. Pokaż wypożyczenia użytkownika");
                System.out.println("7. Usuń auto");
            }

            System.out.println("2. Pokaż dostępne");
            System.out.println("3. Wypożycz");
            System.out.println("4. Zwróć");
            System.out.println("5. Moje auta");
            System.out.println("8. Sortuj");
            System.out.println("9. Filtruj po klasie");
            System.out.println("10. Filtruj po klasie i statusie");
            System.out.println("11. Statystyki");
            System.out.println("12. Liczba aut w klasie");
            System.out.println("0. Wyjście");

            wybor = sc.nextInt();
            sc.nextLine();

            switch (wybor) {
                case 1 -> { if (u.rola.equals("admin")) dodajAuto(sc, w); }
                case 2 -> w.pokaz();
                case 3 -> { w.pokaz(); w.wypozycz(sc.nextInt(), u); sc.nextLine(); }
                case 4 -> { w.pokazwypozyczone(u); w.zwroc(sc.nextInt(), u); sc.nextLine(); }
                case 5 -> u.pokazMojeAuta();
                case 6 -> { if (u.rola.equals("admin")) { System.out.print("Login: "); w.pokazUzytkownika(sc.nextLine()); } }
                case 7 -> { if (u.rola.equals("admin")) { w.pokaz(); w.usun(sc.nextInt()); sc.nextLine(); } }
                case 8 -> { w.sortuj(); w.pokaz(); }
                case 9 -> { System.out.print("Klasa: "); w.filtrujPoKlasie(sc.nextLine()); }
                case 10 -> {
                    System.out.print("Klasa: "); String k = sc.nextLine();
                    System.out.print("Czy wypożyczone (true/false): ");
                    boolean s = sc.nextBoolean(); sc.nextLine();
                    w.filtrujPoKlasieIWypozyczeniu(k, s);
                }
                case 11 -> w.statystyki();
                case 12 -> {
                    System.out.print("Klasa: ");
                    String k = sc.nextLine();
                    System.out.println("Ilość: " + w.ileSamochodowWKlasie(k));
                }
            }

        } while (wybor != 0);
    }

    /** Dodawanie auta (admin) */
    void dodajAuto(Scanner sc, Wypozyczalnia w) throws Exception {
        System.out.print("Marka: "); String m = sc.nextLine();
        System.out.print("Model: "); String mo = sc.nextLine();
        System.out.print("Skrzynia: "); String s = sc.nextLine();
        System.out.print("Klasa: "); String k = sc.nextLine();
        System.out.print("Miejsca: "); int mi = sc.nextInt(); sc.nextLine();

        w.dodaj(new Samochod(w.generujId(), m, mo, s, k, mi));
        w.zapisz();
    }
}