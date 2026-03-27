import java.util.*;
import java.io.*;

public class aplikacjaWypozyczalnia {

    public static void main(String[] args) throws Exception {
        new aplikacjaWypozyczalnia().startAplikacji();
    }

    void startAplikacji() throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Rejestracja\n2. Logowanie");
        int wybor = scanner.nextInt();
        scanner.nextLine();

        if (wybor == 1) rejestracja(scanner);

        Uzytkownik zalogowanyUzytkownik = logowanie(scanner);
        if (zalogowanyUzytkownik == null) return;

        Wypozyczalnia wypozyczalnia = new Wypozyczalnia();
        wypozyczalnia.wczytajSamochodyZPliku();

        // Przywrócenie samochodów użytkownika
        for (Samochod samochod : wypozyczalnia.getSamochody()) {
            if (samochod.wypozyczyl.equals(zalogowanyUzytkownik.login)) {
                zalogowanyUzytkownik.dodajAuto(samochod);
            }
        }

        menu(scanner, wypozyczalnia, zalogowanyUzytkownik);
    }

    void rejestracja(Scanner scanner) {
        System.out.print("Login: "); String login = scanner.nextLine();
        System.out.print("Hasło: "); String haslo = scanner.nextLine();
        System.out.print("Rola (admin/user): "); String rola = scanner.nextLine();

        if (!Validator.isValidLogin(login)) {
            System.out.println("Login musi mieć min. 3 znaki!");
            return;
        }

        UzytkownikIO.zapisz(new Uzytkownik(login, haslo, rola));
    }

    Uzytkownik logowanie(Scanner scanner) {
        System.out.print("Login: "); String login = scanner.nextLine();
        System.out.print("Hasło: "); String haslo = scanner.nextLine();

        for (Uzytkownik uzytkownik : UzytkownikIO.wczytaj()) {
            if (uzytkownik.login.equals(login) && uzytkownik.haslo.equals(haslo))
                return uzytkownik;
        }

        System.out.println("Błędny login!");
        return null;
    }

    void menu(Scanner scanner, Wypozyczalnia wypozyczalnia, Uzytkownik uzytkownik) {
        int wybor = -1;

        try {
            do {
                wypiszMenu(uzytkownik);

                try {
                    wybor = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Podaj liczbę!");
                    scanner.nextLine();
                    continue;
                }
                scanner.nextLine();

                switch (wybor) {

                    case 1 -> {
                        if (uzytkownik.rola.equals("admin"))
                            dodajAuto(scanner, wypozyczalnia);
                    }

                    case 2 -> {
                        for (Samochod samochod : wypozyczalnia.getSamochodySerwis().pobierzDostepneSamochody())
                            System.out.println(samochod);
                    }

                    case 3 -> {
                        for (Samochod samochod : wypozyczalnia.getSamochodySerwis().pobierzDostepneSamochody())
                            System.out.println(samochod);

                        int id = scanner.nextInt(); scanner.nextLine();
                        System.out.println(wypozyczalnia.wypozyczSamochod(id, uzytkownik));
                    }

                    case 4 -> {
                        uzytkownik.pokazMojeAuta();

                        int id = scanner.nextInt(); scanner.nextLine();
                        System.out.println(wypozyczalnia.zwrocSamochod(id, uzytkownik));
                    }

                    case 5 -> uzytkownik.pokazMojeAuta();

                    case 6 -> {
                        if (uzytkownik.rola.equals("admin")) {
                            System.out.print("Login: ");
                            String login = scanner.nextLine();

                            for (Samochod samochod : wypozyczalnia.getSamochodySerwis().pobierzSamochodyWypozyczonePrzez(login))
                                System.out.println(samochod);
                        }
                    }

                    case 7 -> {
                        if (uzytkownik.rola.equals("admin")) {
                            for (Samochod samochod : wypozyczalnia.getSamochodySerwis().pobierzDostepneSamochody())
                                System.out.println(samochod);

                            int id = scanner.nextInt(); scanner.nextLine();
                            System.out.println(wypozyczalnia.usunSamochod(id));
                        }
                    }

                    case 8 -> {
                        wypozyczalnia.getSamochodySerwis().sortujAutaAlfabetycznie();
                        for (Samochod samochod : wypozyczalnia.getSamochodySerwis().pobierzDostepneSamochody())
                            System.out.println(samochod);
                    }

                    case 9 -> {
                        System.out.print("Klasa: ");
                        String nazwaKlasy = scanner.nextLine();

                        for (Samochod samochod : wypozyczalnia.getSamochodySerwis().filtrujPoKlasie(nazwaKlasy))
                            System.out.println(samochod);
                    }

                    case 10 -> {
                        System.out.print("Klasa: ");
                        String nazwaKlasy = scanner.nextLine();

                        System.out.print("true/false: ");
                        boolean czyWypozyczony = scanner.nextBoolean();
                        scanner.nextLine();

                        for (Samochod samochod : wypozyczalnia.getSamochodySerwis().filtrujPoKlasieIWypozyczeniu(nazwaKlasy, czyWypozyczony))
                            System.out.println(samochod);
                    }

                    case 11 -> System.out.println(wypozyczalnia.getSamochodySerwis().statystyki());

                    case 12 -> {
                        System.out.print("Klasa: ");
                        String nazwaKlasy = scanner.nextLine();
                        System.out.println(wypozyczalnia.getSamochodySerwis().ileSamochodowWKlasie(nazwaKlasy));
                    }

                    case 13 -> {
                        System.out.print("Fraza: ");
                        String fraza = scanner.nextLine();

                        for (Samochod samochod : wypozyczalnia.getSamochodySerwis().szukaj(fraza))
                            System.out.println(samochod);
                    }

                    case 14 -> {
                        System.out.print("ID auta: ");
                        int id = scanner.nextInt(); scanner.nextLine();

                        for (String wpisHistorii : wypozyczalnia.getHistoriaSerwis().pobierzHistorieSamochodu(wypozyczalnia.getSamochodySerwis().znajdzPoId(id)))
                            System.out.println(wpisHistorii);
                    }
                }

            } while (wybor != 0);

        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    void wypiszMenu(Uzytkownik uzytkownik) {
        System.out.println("\nMENU");

        if (uzytkownik.rola.equals("admin")) {
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
        System.out.println("13. Szukaj");
        System.out.println("14. Historia auta");
        System.out.println("0. Wyjście");
    }

    void dodajAuto(Scanner scanner, Wypozyczalnia wypozyczalnia) throws Exception {
        System.out.print("Marka: "); String marka = scanner.nextLine();
        System.out.print("Model: "); String model = scanner.nextLine();
        System.out.print("Skrzynia: "); SkrzyniaBiegow skrzynia = SkrzyniaBiegow.valueOf(scanner.nextLine().toUpperCase());
        System.out.print("Klasa: "); KlasaSamochodu klasa = KlasaSamochodu.valueOf(scanner.nextLine().toUpperCase());
        System.out.print("Miejsca: "); int liczbaMiejsc = scanner.nextInt(); scanner.nextLine();

        System.out.println(
                wypozyczalnia.dodajSamochod(
                        new Samochod(wypozyczalnia.generujIdSamochodu(), marka, model, skrzynia, klasa, liczbaMiejsc)
                )
        );
    }
}