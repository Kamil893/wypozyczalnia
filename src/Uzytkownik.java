import java.util.*;

/**
 * Klasa użytkownika.
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
        for (Samochod s : mojeAuta)
            System.out.println(s);
    }
}