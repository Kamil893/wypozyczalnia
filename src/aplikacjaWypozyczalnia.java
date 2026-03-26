import java.util.*;
import java.io.*;

/**
 * Główna klasa aplikacji wypożyczalni.
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

        // dopisanie aut użytkownika po wczytaniu
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
                wypiszMenu(u);

                try {
                    wybor = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Podaj liczbę!");
                    sc.nextLine();
                    continue;
                }
                sc.nextLine();

                switch (wybor) {
                    case 1 -> { if (u.rola.equals("admin")) dodajAuto(sc, w); }
                    case 2 -> w.pokaz();
                    case 3 -> {
                        w.pokaz();
                        int id = sc.nextInt(); sc.nextLine();
                        w.wypozycz(id, u);
                    }
                    case 4 -> {
                        w.pokazwypozyczone(u);
                        int id = sc.nextInt(); sc.nextLine();
                        w.zwroc(id, u);
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
                            w.pokaz();
                            int id = sc.nextInt(); sc.nextLine();
                            w.usun(id);
                        }
                    }
                    case 8 -> { w.sortuj(); w.pokaz(); }
                    case 9 -> {
                        System.out.print("Klasa: ");
                        String k = sc.nextLine();
                        w.filtrujPoKlasie(k);
                    }
                    case 10 -> {
                        System.out.print("Klasa: ");
                        String k = sc.nextLine();
                        System.out.print("true/false: ");
                        boolean s = sc.nextBoolean(); sc.nextLine();
                        w.filtrujPoKlasieIWypozyczeniu(k, s);
                    }
                    case 11 -> w.statystyki();
                    case 12 -> {
                        System.out.print("Klasa: ");
                        String k = sc.nextLine();
                        System.out.println(w.ileSamochodowWKlasie(k));
                    }
                    case 13 -> {
                        System.out.print("Fraza: ");
                        w.szukaj(sc.nextLine());
                    }
                    case 14 -> {
                        System.out.print("ID auta: ");
                        int id = sc.nextInt(); sc.nextLine();
                        w.pokazHistorie(id);
                    }
                    case 15 -> {
                        if (u.rola.equals("admin")) {
                            System.out.print("ID auta: ");
                            int id = sc.nextInt(); sc.nextLine();
                            w.edytujAuto(id, sc);
                        }
                    }
                }

            } while (wybor != 0);

        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    void wypiszMenu(Uzytkownik u) {
        System.out.println("\nMENU");
        if (u.rola.equals("admin")) {
            System.out.println("1. Dodaj auto");
            System.out.println("6. Pokaż wypożyczenia użytkownika");
            System.out.println("7. Usuń auto");
            System.out.println("15. Edytuj auto");
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
        System.out.println("13. Szukaj");
        System.out.println("14. Historia auta");
        System.out.println("0. Wyjście");
    }

    void dodajAuto(Scanner sc, Wypozyczalnia w) throws Exception {
        System.out.print("Marka: "); String m = sc.nextLine();
        System.out.print("Model: "); String mo = sc.nextLine();
        System.out.print("Skrzynia: "); SkrzyniaBiegow s = SkrzyniaBiegow.valueOf(sc.nextLine().toUpperCase());
        System.out.print("Klasa: "); KlasaSamochodu k = KlasaSamochodu.valueOf(sc.nextLine().toUpperCase());
        System.out.print("Miejsca: "); int mi = sc.nextInt(); sc.nextLine();

        w.dodaj(new Samochod(w.generujId(), m, mo, s, k, mi));
        w.zapisz();
    }
}