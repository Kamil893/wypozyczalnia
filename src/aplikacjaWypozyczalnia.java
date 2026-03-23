import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/**
 * Klasa reprezentująca samochód w wypożyczalni.
 * Zawiera dane: id, marka, model, skrzynia, klasa, miejsca oraz informacje o wypożyczeniu.
 */
class Samochod {
    int id;
    String marka, model, skrzynia, klasa;
    int miejsca;
    boolean wypozyczony = false;
    String wypozyczyl = "";

    /**
     * Konstruktor samochodu.
     * @param id Unikalne ID samochodu
     * @param marka Marka samochodu
     * @param model Model samochodu
     * @param skrzynia Rodzaj skrzyni biegów
     * @param klasa Klasa samochodu
     * @param miejsca Liczba miejsc w samochodzie
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
     * Zwraca tekstową reprezentację samochodu do wyświetlenia w menu.
     * @return Opis auta z informacją o dostępności
     */
    @Override
    public String toString() {
        return id + ". " + marka + " " + model + " | " + skrzynia + " | " + klasa +
                (wypozyczony ? " | WYPOŻYCZONY przez: " + wypozyczyl : " | DOSTĘPNY");
    }

    /**
     * Zwraca tekstową reprezentację samochodu do zapisania w pliku.
     * @return Linia CSV reprezentująca samochód
     */
    public String toFile() {
        return id + "," + model + "," + marka + "," + skrzynia + "," +
                klasa + "," + miejsca + "," + wypozyczyl;
    }
}

/**
 * Klasa reprezentująca użytkownika systemu.
 * Przechowuje login, hasło, rolę oraz listę wypożyczonych samochodów.
 */
class Uzytkownik {
    String login;
    String haslo;
    String rola;
    List<Samochod> mojeAuta = new ArrayList<>();

    /**
     * Konstruktor użytkownika.
     * @param login Login użytkownika
     * @param haslo Hasło użytkownika
     * @param rola Rola (admin/user)
     */
    Uzytkownik(String login, String haslo, String rola) {
        this.login = login;
        this.haslo = haslo;
        this.rola = rola;
    }

    /**
     * Dodaje samochód do listy wypożyczonych aut użytkownika.
     * @param s Samochód do dodania
     */
    void dodajAuto(Samochod s) {
        mojeAuta.add(s);
    }

    /**
     * Usuwa samochód z listy wypożyczonych aut użytkownika.
     * @param s Samochód do usunięcia
     */
    void usunAuto(Samochod s) {
        mojeAuta.remove(s);
    }

    /**
     * Wyświetla listę wypożyczonych aut użytkownika.
     */
    void pokazMojeAuta() {
        if (mojeAuta.isEmpty()) {
            System.out.println("Brak wypożyczonych aut.");
            return;
        }
        int i = 1;
        for (Samochod s : mojeAuta) {
            System.out.println(i++ + ". " + s);
        }
    }
}

/**
 * Klasa reprezentująca wypożyczalnię samochodów.
 * Przechowuje listę wszystkich aut i umożliwia ich dodawanie, wypożyczanie, zwracanie, usuwanie i sortowanie.
 */
class Wypozyczalnia {
    List<Samochod> auta = new ArrayList<>();

    /**
     * Generuje nowe unikalne ID dla samochodu.
     * @return Nowe ID
     */
    int generujId() {
        int max = 0;
        for (Samochod s : auta) {
            if (s.id > max) max = s.id;
        }
        return max + 1;
    }

    /**
     * Znajduje samochód po jego ID.
     * @param id ID samochodu
     * @return Samochód lub null jeśli nie istnieje
     */
    Samochod znajdzPoId(int id) {
        for (Samochod s : auta) {
            if (s.id == id) return s;
        }
        return null;
    }

    /**
     * Dodaje samochód do wypożyczalni.
     * @param s Samochód do dodania
     */
    void dodaj(Samochod s) {
        auta.add(s);
        System.out.println("Dodano!");
    }

    /** Wyświetla listę dostępnych samochodów. */
    void pokaz() {
        for (Samochod s : auta) {
            if (!s.wypozyczony)
                System.out.println(s);
        }
    }

    /** Wyświetla listę samochodów wypożyczonych przez użytkownika. */
    void pokazwypozyczone(Uzytkownik user) {
        if (user.mojeAuta.isEmpty()) {
            System.out.println("Brak wypożyczonych aut.");
            return;
        }
        for (Samochod s : user.mojeAuta) {
            System.out.println(s);
        }
    }

    /**
     * Pokazuje samochody wypożyczone przez określonego użytkownika.
     * @param user Login użytkownika
     */
    void pokazUzytkownika(String user) {
        System.out.println("Użytkownik: " + user);
        System.out.println("Wypożyczone auta:");
        boolean znaleziono = false;
        int nr = 1;
        for (Samochod s : auta) {
            if (s.wypozyczyl.equals(user)) {
                System.out.println(nr++ + ". " + s);
                znaleziono = true;
            }
        }
        if (!znaleziono) {
            System.out.println("Brak wypożyczonych aut.");
        }
    }

    /**
     * Wypożycza samochód użytkownikowi.
     * @param id ID samochodu
     * @param user Użytkownik wypożyczający
     * @throws Exception jeśli zapis do pliku się nie powiedzie
     */
    void wypozycz(int id, Uzytkownik user) throws Exception {
        Samochod s = znajdzPoId(id);
        if (s == null) {
            System.out.println("Nie ma takiego auta!");
            return;
        }
        if (!s.wypozyczony) {
            s.wypozyczony = true;
            s.wypozyczyl = user.login;
            user.dodajAuto(s);
            zapisz();
            System.out.println("Wypożyczono!");
        } else {
            System.out.println("Auto jest już wypożyczone!");
        }
    }

    /**
     * Zwraca samochód wypożyczony przez użytkownika.
     * @param id ID samochodu
     * @param user Użytkownik zwracający auto
     * @throws Exception jeśli zapis do pliku się nie powiedzie
     */
    void zwroc(int id, Uzytkownik user) throws Exception {
        Samochod s = znajdzPoId(id);
        if (s == null) {
            System.out.println("Nie ma takiego auta!");
            return;
        }
        if (s.wypozyczony && s.wypozyczyl.equals(user.login)) {
            s.wypozyczony = false;
            s.wypozyczyl = "";
            user.usunAuto(s);
            zapisz();
            System.out.println("Zwrócono!");
        } else {
            System.out.println("Nie możesz zwrócić tego auta!");
        }
    }

    /**
     * Usuwa samochód z wypożyczalni (jeśli nie jest wypożyczony).
     * @param id ID samochodu
     * @throws Exception jeśli zapis do pliku się nie powiedzie
     */
    void usun(int id) throws Exception {
        Samochod s = znajdzPoId(id);
        if (s == null) {
            System.out.println("Nie ma takiego auta!");
            return;
        }
        if (s.wypozyczony) {
            System.out.println("Nie można usunąć auta, które jest wypożyczone!");
            return;
        }
        auta.remove(s);
        zapisz();
        System.out.println("Usunięto auto!");
    }

    /** Wczytuje dane samochodów z pliku auta.txt. */
    void wczytaj() throws Exception {
        if (!Files.exists(Path.of("auta.txt"))) {
            Files.write(Path.of("auta.txt"),
                    List.of("ID,Model,Marka,Skrzynia,Klasa,Miejsca,WypozyczonePrzez"));
        }
        List<String> linie = Files.readAllLines(Path.of("auta.txt"));
        for (int i = 1; i < linie.size(); i++) {
            String[] d = linie.get(i).split(",");
            int id = Integer.parseInt(d[0]);
            Samochod s = new Samochod(id, d[2], d[1], d[3], d[4], Integer.parseInt(d[5]));
            if (d.length > 6 && !d[6].equals("")) {
                s.wypozyczony = true;
                s.wypozyczyl = d[6];
            }
            auta.add(s);
        }
    }

    /** Zapisuje dane samochodów do pliku auta.txt. */
    void zapisz() throws Exception {
        List<String> out = new ArrayList<>();
        out.add("ID,Model,Marka,Skrzynia,Klasa,Miejsca,WypozyczonePrzez");
        for (Samochod s : auta) {
            out.add(s.toFile());
        }
        Files.write(Path.of("auta.txt"), out, StandardCharsets.UTF_8);
    }

    /** Sortuje samochody po marce (alfabetycznie). */
    void sortuj() {
        auta.sort(Comparator.comparing(s -> s.marka.toLowerCase()));
        System.out.println("Posortowano po marce");
    }
}

/**
 * Główna klasa aplikacji wypożyczalni.
 * Obsługuje rejestrację, logowanie oraz menu użytkownika i admina.
 */
public class aplikacjaWypozyczalnia {

    public static void main(String[] args) throws Exception {
        new aplikacjaWypozyczalnia().start();
    }

    /** Uruchamia aplikację i obsługuje logowanie/rejestrację. */
    void start() throws Exception {
        Scanner sc = new Scanner(System.in);
        przygotujPliki();

        System.out.println("1. Rejestracja");
        System.out.println("2. Logowanie");
        int start = sc.nextInt();
        sc.nextLine();

        if (start == 1) rejestracja(sc);

        Uzytkownik aktualny = logowanie(sc);
        if (aktualny == null) return;

        System.out.println("Zalogowano jako: " + aktualny.rola);

        Wypozyczalnia w = new Wypozyczalnia();
        w.wczytaj();

        for (Samochod s : w.auta) {
            if (s.wypozyczyl.equals(aktualny.login)) {
                aktualny.dodajAuto(s);
            }
        }

        menu(sc, w, aktualny);
    }

    /** Tworzy pliki bazy danych i auta, jeśli nie istnieją. */
    void przygotujPliki() throws IOException {
        if (!Files.exists(Path.of("bazaDanych.txt"))) Files.createFile(Path.of("bazaDanych.txt"));
        if (!Files.exists(Path.of("auta.txt"))) Files.write(Path.of("auta.txt"),
                List.of("ID,Model,Marka,Skrzynia,Klasa,Miejsca,WypozyczonePrzez"));
    }

    /** Rejestracja nowego użytkownika. */
    void rejestracja(Scanner sc) throws IOException {
        System.out.print("Login: ");
        String login = sc.nextLine();
        System.out.print("Hasło: ");
        String haslo = sc.nextLine();
        System.out.print("Rola (admin/user): ");
        String rola = sc.nextLine();
        String user = login + "," + haslo + "," + rola + "\n";
        Files.write(Path.of("bazaDanych.txt"), user.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        System.out.println("Dodano użytkownika!");
    }

    /** Logowanie użytkownika. */
    Uzytkownik logowanie(Scanner sc) throws IOException {
        System.out.print("Login: ");
        String login = sc.nextLine();
        System.out.print("Hasło: ");
        String haslo = sc.nextLine();

        List<String> lines = Files.readAllLines(Path.of("bazaDanych.txt"));
        for (String line : lines) {
            String[] d = line.split(",");
            if (d.length == 3 && d[0].equals(login) && d[1].equals(haslo)) {
                return new Uzytkownik(d[0], d[1], d[2]);
            }
        }
        System.out.println("Błędny login!");
        return null;
    }

    /** Menu główne aplikacji. */
    void menu(Scanner sc, Wypozyczalnia w, Uzytkownik user) throws Exception {
        int wybor;
        do {
            System.out.println("\nMENU");
            if (user.rola.equals("admin")) {
                System.out.println("1. Dodaj auto");
                System.out.println("6. Pokaż wypożyczenia użytkownika");
                System.out.println("7. Usuń auto");
            }
            System.out.println("2. Pokaż dostępne");
            System.out.println("3. Wypożycz");
            System.out.println("4. Zwróć");
            System.out.println("5. Moje auta");
            System.out.println("8. Sortuj auta po marce");
            System.out.println("0. Wyjście");

            wybor = sc.nextInt();
            sc.nextLine(); // czyszczenie bufora

            switch (wybor) {
                case 1 -> { if (user.rola.equals("admin")) dodajAuto(sc, w); }
                case 2 -> w.pokaz();
                case 3 -> wypozyczAuto(sc, w, user);
                case 4 -> zwrocAuto(sc, w, user);
                case 5 -> user.pokazMojeAuta();
                case 6 -> { if (user.rola.equals("admin")) pokazUzytkownika(sc, w); }
                case 7 -> { if (user.rola.equals("admin")) usunAuto(sc, w); }
                case 8 -> { w.sortuj(); w.pokaz(); }
            }
        } while (wybor != 0);
    }

    void dodajAuto(Scanner sc, Wypozyczalnia w) throws Exception {
        System.out.print("Marka: "); String m = sc.nextLine();
        System.out.print("Model: "); String mo = sc.nextLine();
        System.out.print("Skrzynia: "); String s = sc.nextLine();
        System.out.print("Klasa: "); String k = sc.nextLine();
        System.out.print("Miejsca: "); int mi = sc.nextInt(); sc.nextLine();
        w.dodaj(new Samochod(w.generujId(), m, mo, s, k, mi));
        w.zapisz();
    }

    void wypozyczAuto(Scanner sc, Wypozyczalnia w, Uzytkownik user) throws Exception {
        w.pokaz();
        System.out.print("Podaj ID: "); int id = sc.nextInt(); sc.nextLine();
        w.wypozycz(id, user);
    }

    void zwrocAuto(Scanner sc, Wypozyczalnia w, Uzytkownik user) throws Exception {
        w.pokazwypozyczone(user);
        System.out.print("Podaj ID: "); int id = sc.nextInt(); sc.nextLine();
        w.zwroc(id, user);
    }

    void pokazUzytkownika(Scanner sc, Wypozyczalnia w) {
        System.out.print("Podaj login użytkownika: "); String login = sc.nextLine();
        w.pokazUzytkownika(login);
    }

    void usunAuto(Scanner sc, Wypozyczalnia w) throws Exception {
        w.pokaz();
        System.out.print("Podaj ID auta do usunięcia: "); int id = sc.nextInt(); sc.nextLine();
        w.usun(id);
    }
}