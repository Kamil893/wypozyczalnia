import java.util.*;

public class Uzytkownik {
    public String login;
    public String haslo;
    public String rola;

    // lista samochodów wypożyczonych przez użytkownika
    private List<Samochod> wypozyczoneSamochody = new ArrayList<>();

    public Uzytkownik(String login, String haslo, String rola) {
        this.login = login;
        this.haslo = haslo;
        this.rola = rola;
    }

    // dodaje samochód do listy wypożyczonych
    public void dodajAuto(Samochod samochod) {
        wypozyczoneSamochody.add(samochod);
    }

    // usuwa samochód z listy wypożyczonych
    public void usunAuto(Samochod samochod) {
        wypozyczoneSamochody.remove(samochod);
    }

    // metoda do wyświetlania samochodów użytkownika
    public void pokazMojeAuta() {
        if (wypozyczoneSamochody.isEmpty()) {
            System.out.println("Nie masz żadnych aut.");
            return;
        }
        for (Samochod samochod : wypozyczoneSamochody) {
            System.out.println(samochod);
        }
    }

}