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

    void dodajAuto(Samochod samochod) {
        mojeAuta.add(samochod);
    }

    void usunAuto(Samochod samochod) {
        mojeAuta.remove(samochod);
    }

    void pokazMojeAuta() {
        if (mojeAuta.isEmpty()) {
            System.out.println("Brak wypożyczonych aut.");
            return;
        }
        for (Samochod samochod : mojeAuta)
            System.out.println(samochod);
    }
}