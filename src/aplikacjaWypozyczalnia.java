import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/** Enum klas samochodów */
enum KlasaSamochodu {
    OSOBOWE, SUV, PREMIUM, KOMPAKT
}

/** Enum skrzyni biegów */
enum SkrzyniaBiegow {
    MANUALNA, AUTOMATYCZNA
}

/**
 * Klasa reprezentująca samochód w wypożyczalni.
 */
class Samochod {
    int id;
    String marka, model;
    SkrzyniaBiegow skrzynia;
    KlasaSamochodu klasa;
    int miejsca;
    boolean wypozyczony = false;
    String wypozyczyl = "";

    Samochod(int id, String marka, String model, SkrzyniaBiegow skrzynia, KlasaSamochodu klasa, int miejsca) {
        this.id = id;
        this.marka = marka;
        this.model = model;
        this.skrzynia = skrzynia;
        this.klasa = klasa;
        this.miejsca = miejsca;
    }

    public String toString() {
        return id + ". " + marka + " " + model + " | " + skrzynia + " | " + klasa +
                (wypozyczony ? " | WYPOŻYCZONY przez: " + wypozyczyl : " | DOSTĘPNY");
    }

    public String toFile() {
        return id + "," + model + "," + marka + "," + skrzynia.name() + "," +
                klasa.name() + "," + miejsca + "," + wypozyczyl;
    }
}

/**
 * Klasa reprezentująca użytkownika systemu.
 */
class Uzytkownik {
    String login, haslo, rola;
    List<Samochod> mojeAuta = new ArrayList<>();

    Uzytkownik(String login, String haslo, String rola) {
        this.login = login;
        this.haslo = haslo;
        this.rola = rola;
    }

    void dodajAuto(Samochod s) {
        mojeAuta.add(s);
    }

    void usunAuto(Samochod s) {
        mojeAuta.remove(s);
    }

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

    int generujId() {
        int max = 0;
        for (Samochod s : auta) if (s.id > max) max = s.id;
        return max + 1;
    }

    Samochod znajdzPoId(int id) {
        for (Samochod s : auta)
            if (s.id == id) return s;
        return null;
    }

    void dodaj(Samochod s) {
        auta.add(s);
        System.out.println("Dodano!");
    }

    void pokaz() {
        for (Samochod s : auta)
            if (!s.wypozyczony) System.out.println(s);
    }

    void pokazwypozyczone(Uzytkownik user) {
        for (Samochod s : user.mojeAuta)
            System.out.println(s);
    }

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

    void usun(int id) throws Exception {
        Samochod s = znajdzPoId(id);
        if (s != null && !s.wypozyczony) {
            auta.remove(s);
            zapisz();
            System.out.println("Usunięto!");
        }
    }

    void wczytaj() {
        auta = SamochodDAO.wczytaj();
    }

    void zapisz() {
        SamochodDAO.zapisz(auta);
    }

    void sortuj() {
        auta.sort(Comparator.comparing(s -> s.marka.toLowerCase()));
    }

    void filtrujPoKlasie(String klasa) {
        for (Samochod s : auta)
            if (s.klasa.name().equalsIgnoreCase(klasa))
                System.out.println(s);
    }

    void filtrujPoKlasieIWypozyczeniu(String klasa, boolean wypozyczone) {
        for (Samochod s : auta)
            if (s.klasa.name().equalsIgnoreCase(klasa) && s.wypozyczony == wypozyczone)
                System.out.println(s);
    }

    void statystyki() {
        int d = 0, w = 0;
        for (Samochod s : auta)
            if (s.wypozyczony) w++; else d++;
        System.out.println("Dostępne: " + d + ", Wypożyczone: " + w);
    }

    int ileSamochodowWKlasie(String klasa) {
        int c = 0;
        for (Samochod s : auta)
            if (s.klasa.name().equalsIgnoreCase(klasa)) c++;
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

    void start() throws Exception {
        Scanner sc = new Scanner(System.in);

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

    void rejestracja(Scanner sc) {
        System.out.print("Login: "); String l = sc.nextLine();
        System.out.print("Hasło: "); String h = sc.nextLine();
        System.out.print("Rola (admin/user): "); String r = sc.nextLine();

        if (!Validator.isValidLogin(l)) {
            System.out.println("Login musi mieć min. 3 znaki!");
            return;
        }

        UzytkownikIO.zapisz(new Uzytkownik(l, h, r));
    }

    Uzytkownik logowanie(Scanner sc) {
        System.out.print("Login: "); String l = sc.nextLine();
        System.out.print("Hasło: "); String h = sc.nextLine();

        for (Uzytkownik u : UzytkownikIO.wczytaj()) {
            if (u.login.equals(l) && u.haslo.equals(h))
                return u;
        }

        System.out.println("Błędny login!");
        return null;
    }

    void menu(Scanner sc, Wypozyczalnia w, Uzytkownik u) {
        int wybor = -1;

        try {
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

                try {
                    wybor = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Podaj liczbę!");
                    sc.nextLine();
                    continue;
                }
                sc.nextLine();

                switch (wybor) {
                    case 1 -> {
                        if (u.rola.equals("admin")) {
                            try {
                                dodajAuto(sc, w);
                            } catch (Exception e) {
                                System.out.println("Operacja nieudana: " + e.getMessage());
                            }
                        }
                    }
                    case 2 -> w.pokaz();

                    case 3 -> {
                        try {
                            w.pokaz();

                            int id;
                            try {
                                id = sc.nextInt();
                            } catch (InputMismatchException e) {
                                System.out.println("Podaj poprawne ID!");
                                sc.nextLine();
                                break;
                            }
                            sc.nextLine();

                            if (!Validator.isValidId(id)) {
                                System.out.println("Niepoprawne ID!");
                                break;
                            }

                            w.wypozycz(id, u);

                        } catch (Exception e) {
                            System.out.println("Operacja nieudana: " + e.getMessage());
                        }
                    }

                    case 4 -> {
                        try {
                            w.pokazwypozyczone(u);

                            int id;
                            try {
                                id = sc.nextInt();
                            } catch (InputMismatchException e) {
                                System.out.println("Podaj poprawne ID!");
                                sc.nextLine();
                                break;
                            }
                            sc.nextLine();

                            if (!Validator.isValidId(id)) {
                                System.out.println("Niepoprawne ID!");
                                break;
                            }

                            w.zwroc(id, u);

                        } catch (Exception e) {
                            System.out.println("Operacja nieudana: " + e.getMessage());
                        }
                    }

                    case 5 -> u.pokazMojeAuta();

                    case 6 -> {
                        if (u.rola.equals("admin")) {
                            System.out.print("Login: ");
                            w.pokazUzytkownika(sc.nextLine());
                        }
                    }

                    case 7 -> {
                        if (u.rola.equals("admin")) {
                            try {
                                w.pokaz();

                                int id;
                                try {
                                    id = sc.nextInt();
                                } catch (InputMismatchException e) {
                                    System.out.println("Podaj poprawne ID!");
                                    sc.nextLine();
                                    break;
                                }
                                sc.nextLine();

                                w.usun(id);

                            } catch (Exception e) {
                                System.out.println("Operacja nieudana: " + e.getMessage());
                            }
                        }
                    }

                    case 8 -> {
                        w.sortuj();
                        w.pokaz();
                    }

                    case 9 -> {
                        System.out.print("Klasa: ");
                        String k = sc.nextLine();

                        if (!Validator.isValidKlasaSamochodu(k)) {
                            System.out.println("Niepoprawna klasa!");
                            break;
                        }

                        w.filtrujPoKlasie(k);
                    }

                    case 10 -> {
                        System.out.print("Klasa: ");
                        String k = sc.nextLine();

                        if (!Validator.isValidKlasaSamochodu(k)) {
                            System.out.println("Niepoprawna klasa!");
                            break;
                        }

                        System.out.print("Czy wypożyczone (true/false): ");

                        boolean s;
                        try {
                            s = sc.nextBoolean();
                        } catch (InputMismatchException e) {
                            System.out.println("Podaj true/false!");
                            sc.nextLine();
                            break;
                        }
                        sc.nextLine();

                        w.filtrujPoKlasieIWypozyczeniu(k, s);
                    }

                    case 11 -> w.statystyki();

                    case 12 -> {
                        System.out.print("Klasa: ");
                        String k = sc.nextLine();

                        if (!Validator.isValidKlasaSamochodu(k)) {
                            System.out.println("Niepoprawna klasa!");
                            break;
                        }

                        System.out.println("Ilość: " + w.ileSamochodowWKlasie(k));
                    }
                }

            } while (wybor != 0);

        } catch (Exception e) {
            System.out.println("Krytyczny błąd aplikacji: " + e.getMessage());
        }
    }

    void dodajAuto(Scanner sc, Wypozyczalnia w) throws Exception {
        System.out.print("Marka: "); String m = sc.nextLine();
        System.out.print("Model: "); String mo = sc.nextLine();

        System.out.print("Skrzynia (MANUALNA/AUTOMATYCZNA): ");
        SkrzyniaBiegow s = SkrzyniaBiegow.valueOf(sc.nextLine().toUpperCase());

        System.out.print("Klasa (OSOBOWE/SUV/PREMIUM/KOMPAKT): ");
        KlasaSamochodu k = KlasaSamochodu.valueOf(sc.nextLine().toUpperCase());

        System.out.print("Miejsca: "); int mi = sc.nextInt(); sc.nextLine();

        w.dodaj(new Samochod(w.generujId(), m, mo, s, k, mi));
        w.zapisz();
    }
}