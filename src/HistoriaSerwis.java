import java.util.List;

public class HistoriaSerwis {

    public void dodajWpisWypozyczenia(Samochod samochod, Uzytkownik uzytkownik) {
        String wpis = "Wypożyczony przez: " + uzytkownik.login + " | " + java.time.LocalDateTime.now();
        samochod.historia.add(wpis);
        HistoriaIO.zapiszWpis(samochod.id, uzytkownik.login, "WYPOŻYCZONE");
    }

    public void dodajWpisZwrotu(Samochod samochod, Uzytkownik uzytkownik) {
        String wpis = "Zwrócony przez: " + uzytkownik.login + " | " + java.time.LocalDateTime.now();
        samochod.historia.add(wpis);
        HistoriaIO.zapiszWpis(samochod.id, uzytkownik.login, "ZWROT");
    }

    public List<String> pobierzHistorieSamochodu(Samochod samochod) {
        return HistoriaIO.wczytajHistorie(samochod.id);
    }
}