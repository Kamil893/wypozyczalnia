import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/**
 * Klasa reprezentująca samochód w wypożyczalni.
 * Zawiera dane: id, marka, model, skrzynia, klasa, miejsca oraz informacje o wypożyczeniu.
 */
class samochod {
    int id;
    String marka, model, skrzynia, klasa;
    int miejsca;
    boolean wypozyczony = false;
    String wypozyczyl = "";

    /**
     * Konstruktor samochodu.
     * @param id Unikalne ID samochodu
     * @param m Marka samochodu
     * @param mo Model samochodu
     * @param s Rodzaj skrzyni biegów
     * @param k Klasa samochodu
     * @param mi Liczba miejsc w samochodzie
     */
    samochod(int id, String m, String mo, String s, String k, int mi) {
        this.id = id;
        marka = m;
        model = mo;
        skrzynia = s;
        klasa = k;
        miejsca = mi;
    }

    /**
     * Zwraca tekstową reprezentację samochodu do wyświetlenia w menu.
     * @return Opis auta z informacją o dostępności
     */
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
    List<samochod> mojeAuta = new ArrayList<>();

    /**
     * Konstruktor użytkownika.
     * @param l Login użytkownika
     * @param h Hasło użytkownika
     * @param r Rola (admin/user)
     */
    Uzytkownik(String l, String h, String r) {
        login = l;
        haslo = h;
        rola = r;
    }

    /** Dodaje samochód do listy wypożyczonych aut użytkownika. */
    void dodajAuto(samochod s) {
        mojeAuta.add(s);
    }

    /** Usuwa samochód z listy wypożyczonych aut użytkownika. */
    void usunAuto(samochod s) {
        mojeAuta.remove(s);
    }

    /** Wyświetla listę wypożyczonych aut użytkownika. */
    void pokazMojeAuta() {
        if (mojeAuta.isEmpty()) {
            System.out.println("Brak wypożyczonych aut.");
            return;
        }
        int i = 1;
        for (samochod s : mojeAuta) {
            System.out.println(i++ + ". " + s);
        }
    }
}

/**
 * Klasa reprezentująca wypożyczalnię samochodów.
 * Przechowuje listę wszystkich aut i umożliwia ich dodawanie, wypożyczanie, zwracanie, usuwanie i sortowanie.
 */
class wypozyczalnia {
    List<samochod> auta = new ArrayList<>();

    /** Generuje nowe unikalne ID dla samochodu. */
    int generujId() {
        int max = 0;
        for (samochod s : auta) {
            if (s.id > max) max = s.id;
        }
        return max + 1;
    }

    /**
     * Znajduje samochód po jego ID.
     * @param id ID samochodu
     * @return samochód lub null jeśli nie istnieje
     */
    samochod znajdzPoId(int id) {
        for (samochod s : auta) {
            if (s.id == id) return s;
        }
        return null;
    }

    /**
     * Dodaje samochód do wypożyczalni.
     * @param s Samochód do dodania
     */
    void dodaj(samochod s) {
        auta.add(s);
        System.out.println("Dodano!");
    }

    /** Wyświetla listę dostępnych samochodów. */
    void pokaz() {
        for (samochod s : auta) {
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
        for (samochod s : user.mojeAuta) {
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
        for (samochod s : auta) {
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
     * @throws Exception
     */
    void wypozycz(int id, Uzytkownik user) throws Exception {
        samochod s = znajdzPoId(id);
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
     * @throws Exception
     */
    void zwroc(int id, Uzytkownik user) throws Exception {
        samochod s = znajdzPoId(id);
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
     * @throws Exception
     */
    void usun(int id) throws Exception {
        samochod s = znajdzPoId(id);
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
            samochod s = new samochod(id, d[2], d[1], d[3], d[4], Integer.parseInt(d[5]));
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
        for (samochod s : auta) {
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
        Scanner sc = new Scanner(System.in);
        if (!Files.exists(Path.of("bazaDanych.txt"))) {
            FileOutputStream fos = new FileOutputStream("bazaDanych.txt");
            fos.close();
        }

        System.out.println("1. Rejestracja");
        System.out.println("2. Logowanie");
        int start = sc.nextInt();
        sc.nextLine();

        if (start == 1) {
            FileOutputStream fos = new FileOutputStream("bazaDanych.txt", true);
            System.out.print("Login: ");
            String login = sc.nextLine();
            System.out.print("Hasło: ");
            String haslo = sc.nextLine();
            System.out.print("Rola (admin/user): ");
            String rola = sc.nextLine();
            String user = login + "," + haslo + "," + rola + "\n";
            fos.write(user.getBytes(StandardCharsets.UTF_8));
            fos.close();
            System.out.println("Dodano użytkownika!");
        }

        System.out.print("Login: ");
        String login = sc.nextLine();
        System.out.print("Hasło: ");
        String haslo = sc.nextLine();

        List<String> lines = Files.readAllLines(Path.of("bazaDanych.txt"));
        String rola = "";
        boolean ok = false;
        Uzytkownik aktualny = null;

        for (String line : lines) {
            String[] d = line.split(",");
            if (d.length == 3) {
                if (d[0].equals(login) && d[1].equals(haslo)) {
                    ok = true;
                    rola = d[2];
                    aktualny = new Uzytkownik(d[0], d[1], d[2]);
                }
            }
        }

        if (!ok) {
            System.out.println("Błędny login!");
            return;
        }

        System.out.println("Zalogowano jako: " + rola);

        wypozyczalnia w = new wypozyczalnia();
        w.wczytaj();

        for (samochod s : w.auta) {
            if (s.wypozyczyl.equals(aktualny.login)) {
                aktualny.dodajAuto(s);
            }
        }

        int wybor;
        do {
            System.out.println("\nMENU");
            if (rola.equals("admin")) {
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

            switch (wybor) {
                case 1:
                    if (!rola.equals("admin")) break;
                    sc.nextLine();
                    System.out.print("Marka: ");
                    String m = sc.nextLine();
                    System.out.print("Model: ");
                    String mo = sc.nextLine();
                    System.out.print("Skrzynia: ");
                    String s = sc.nextLine();
                    System.out.print("Klasa: ");
                    String k = sc.nextLine();
                    System.out.print("Miejsca: ");
                    int mi = sc.nextInt();
                    w.dodaj(new samochod(w.generujId(), m, mo, s, k, mi));
                    w.zapisz();
                    break;

                case 2:
                    w.pokaz();
                    break;

                case 3:
                    w.pokaz();
                    System.out.print("Podaj ID: ");
                    w.wypozycz(sc.nextInt(), aktualny);
                    break;

                case 4:
                    w.pokazwypozyczone(aktualny);
                    System.out.print("Podaj ID: ");
                    w.zwroc(sc.nextInt(), aktualny);
                    break;

                case 5:
                    aktualny.pokazMojeAuta();
                    break;

                case 6:
                    if (!rola.equals("admin")) break;
                    sc.nextLine();
                    System.out.print("podaj login użytkownika: ");
                    String user = sc.nextLine();
                    w.pokazUzytkownika(user);
                    break;

                case 7:
                    if (!rola.equals("admin")) break;
                    w.pokaz();
                    System.out.print("Podaj ID auta do usunięcia: ");
                    w.usun(sc.nextInt());
                    break;

                case 8:
                    w.sortuj();
                    w.pokaz();
                    break;
            }

        } while (wybor != 0);
    }
}