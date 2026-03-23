import java.util.*;

/**
 * Klasa reprezentująca samochód w wypożyczalni
 */
class Auto {
    int id;             // Unikalne ID samochodu
    String marka;       // Marka samochodu
    String model;       // Model samochodu
    String skrzynia;    // Rodzaj skrzyni biegów (manual / automat)
    String klasa;       // Klasa samochodu (compact, SUV, premium)
    int miejsca;        // Liczba miejsc w samochodzie
    boolean wypozyczony; // Czy samochód jest aktualnie wypożyczony
    String wypozyczyl;   // Login użytkownika, który wypożyczył auto

    // Konstruktor samochodu
    Auto(int id, String marka, String model, String skrzynia, String klasa, int miejsca) {
        this.id = id;
        this.marka = marka;
        this.model = model;
        this.skrzynia = skrzynia;
        this.klasa = klasa;
        this.miejsca = miejsca;
        this.wypozyczony = false;
        this.wypozyczyl = "";
    }

    // Wyświetlenie samochodu w czytelnej formie
    @Override
    public String toString() {
        if (wypozyczony)
            return id + ". " + marka + " " + model + " | " + skrzynia + " | " + klasa +
                    " | WYPOŻYCZONY przez: " + wypozyczyl;
        else
            return id + ". " + marka + " " + model + " | " + skrzynia + " | " + klasa + " | DOSTĘPNY";
    }
}

/**
 * Klasa reprezentująca użytkownika systemu
 */
class Uzytkownik {
    String login;       // Login użytkownika
    String haslo;       // Hasło
    String rola;        // Rola: "admin" lub "user"
    List<Auto> mojeAuta; // Lista wypożyczonych samochodów

    // Konstruktor użytkownika
    Uzytkownik(String login, String haslo, String rola) {
        this.login = login;
        this.haslo = haslo;
        this.rola = rola;
        this.mojeAuta = new ArrayList<>();
    }

    // Dodaje samochód do listy wypożyczonych aut
    void dodajAuto(Auto a) {
        mojeAuta.add(a);
    }

    // Usuwa samochód z listy wypożyczonych aut
    void usunAuto(Auto a) {
        mojeAuta.remove(a);
    }

    // Wyświetla listę wypożyczonych aut
    void pokazMojeAuta() {
        if (mojeAuta.isEmpty()) {
            System.out.println("Brak wypożyczonych aut.");
            return;
        }
        System.out.println("Twoje wypożyczone auta:");
        for (Auto a : mojeAuta) {
            System.out.println(a);
        }
    }
}

/**
 * Klasa reprezentująca wypożyczalnię samochodów
 */
class Wypozyczalnia {
    List<Auto> auta;    // Lista wszystkich samochodów w wypożyczalni
    int licznikId;      // Licznik do nadawania unikalnych ID

    Wypozyczalnia() {
        auta = new ArrayList<>();
        licznikId = 1; // zaczynamy ID od 1
    }

    // Dodaje nowy samochód
    void dodajAuto(String marka, String model, String skrzynia, String klasa, int miejsca) {
        Auto a = new Auto(licznikId++, marka, model, skrzynia, klasa, miejsca);
        auta.add(a);
        System.out.println("Dodano auto: " + a);
    }

    // Wyświetla dostępne samochody
    void pokazDostepne() {
        boolean znaleziono = false;
        for (Auto a : auta) {
            if (!a.wypozyczony) {
                System.out.println(a);
                znaleziono = true;
            }
        }
        if (!znaleziono) System.out.println("Brak dostępnych samochodów.");
    }

    // Wypożycza samochód użytkownikowi
    void wypozycz(int id, Uzytkownik user) {
        Auto a = znajdzPoId(id);
        if (a == null) {
            System.out.println("Nie ma takiego auta!");
            return;
        }
        if (a.wypozyczony) {
            System.out.println("Auto jest już wypożyczone!");
            return;
        }
        a.wypozyczony = true;
        a.wypozyczyl = user.login;
        user.dodajAuto(a);
        System.out.println("Wypożyczono auto: " + a);
    }

    // Zwraca samochód przez użytkownika
    void zwroc(int id, Uzytkownik user) {
        Auto a = znajdzPoId(id);
        if (a == null) {
            System.out.println("Nie ma takiego auta!");
            return;
        }
        if (!a.wypozyczony || !a.wypozyczyl.equals(user.login)) {
            System.out.println("Nie możesz zwrócić tego auta!");
            return;
        }
        a.wypozyczony = false;
        a.wypozyczyl = "";
        user.usunAuto(a);
        System.out.println("Zwrócono auto: " + a);
    }

    // Znajduje samochód po ID
    Auto znajdzPoId(int id) {
        for (Auto a : auta) {
            if (a.id == id) return a;
        }
        return null;
    }

    // Wyświetla auta wypożyczone przez danego użytkownika
    void pokazWypozyczoneUzytkownika(String login) {
        boolean znaleziono = false;
        for (Auto a : auta) {
            if (a.wypozyczyl.equals(login)) {
                System.out.println(a);
                znaleziono = true;
            }
        }
        if (!znaleziono) System.out.println("Brak wypożyczonych aut przez użytkownika: " + login);
    }

    // Sortuje samochody po marce
    void sortujPoMarce() {
        auta.sort(Comparator.comparing(a -> a.marka.toLowerCase()));
        System.out.println("Auta posortowane po marce.");
    }
}

/**
 * Główna klasa programu
 */
public class aplikacjaWypozyczalnia {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Uzytkownik> uzytkownicy = new ArrayList<>();
        Wypozyczalnia w = new Wypozyczalnia();

        // Przykładowe samochody
        w.dodajAuto("Toyota", "Corolla", "Manual", "Compact", 5);
        w.dodajAuto("BMW", "X5", "Automatic", "SUV", 5);
        w.dodajAuto("Audi", "A6", "Automatic", "Premium", 5);

        // MENU REJESTRACJA / LOGOWANIE
        System.out.println("1. Rejestracja");
        System.out.println("2. Logowanie");
        int opcja = sc.nextInt();
        sc.nextLine();

        if (opcja == 1) {
            System.out.print("Login: ");
            String login = sc.nextLine();
            System.out.print("Hasło: ");
            String haslo = sc.nextLine();
            System.out.print("Rola (admin/user): ");
            String rola = sc.nextLine();
            uzytkownicy.add(new Uzytkownik(login, haslo, rola));
            System.out.println("Zarejestrowano użytkownika: " + login);
        }

        // Logowanie
        System.out.print("Login: ");
        String login = sc.nextLine();
        System.out.print("Hasło: ");
        String haslo = sc.nextLine();

        Uzytkownik aktualny = null;
        for (Uzytkownik u : uzytkownicy) {
            if (u.login.equals(login) && u.haslo.equals(haslo)) {
                aktualny = u;
            }
        }
        if (aktualny == null) {
            System.out.println("Błędny login lub hasło!");
            return;
        }

        System.out.println("Zalogowano jako: " + aktualny.rola);

        // Główne menu
        int wybor;
        do {
            System.out.println("\nMENU:");
            if (aktualny.rola.equals("admin")) System.out.println("1. Dodaj auto");
            System.out.println("2. Pokaż dostępne auta");
            System.out.println("3. Wypożycz auto");
            System.out.println("4. Zwróć auto");
            System.out.println("5. Moje auta");
            if (aktualny.rola.equals("admin")) System.out.println("6. Pokaż wypożyczenia użytkownika");
            System.out.println("7. Sortuj auta po marce");
            System.out.println("0. Wyjście");

            wybor = sc.nextInt();
            sc.nextLine();

            switch (wybor) {
                case 1:
                    if (!aktualny.rola.equals("admin")) break;
                    System.out.print("Marka: "); String m = sc.nextLine();
                    System.out.print("Model: "); String mo = sc.nextLine();
                    System.out.print("Skrzynia: "); String s = sc.nextLine();
                    System.out.print("Klasa: "); String k = sc.nextLine();
                    System.out.print("Miejsca: "); int mi = sc.nextInt();
                    w.dodajAuto(m, mo, s, k, mi);
                    break;
                case 2:
                    w.pokazDostepne();
                    break;
                case 3:
                    w.pokazDostepne();
                    System.out.print("Podaj ID auta do wypożyczenia: ");
                    int idW = sc.nextInt();
                    w.wypozycz(idW, aktualny);
                    break;
                case 4:
                    aktualny.pokazMojeAuta();
                    System.out.print("Podaj ID auta do zwrotu: ");
                    int idZ = sc.nextInt();
                    w.zwroc(idZ, aktualny);
                    break;
                case 5:
                    aktualny.pokazMojeAuta();
                    break;
                case 6:
                    if (!aktualny.rola.equals("admin")) break;
                    System.out.print("Podaj login użytkownika: ");
                    String u = sc.nextLine();
                    w.pokazWypozyczoneUzytkownika(u);
                    break;
                case 7:
                    w.sortujPoMarce();
                    break;
                case 0:
                    System.out.println("Wyjście z programu...");
                    break;
                default:
                    System.out.println("Niepoprawny wybór!");
            }
        } while (wybor != 0);

        sc.close();
    }
}