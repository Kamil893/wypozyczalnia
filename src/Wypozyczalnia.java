import java.util.*;
import java.time.LocalDateTime;

public class Wypozyczalnia {

    private final List<Samochod> samochody = new ArrayList<>();

    private final SamochodySerwis samochodySerwis;
    private final HistoriaSerwis historiaSerwis;

    public Wypozyczalnia() {
        this.samochodySerwis = new SamochodySerwis(this);
        this.historiaSerwis = new HistoriaSerwis();
    }

    public List<Samochod> getSamochody() {
        return samochody;
    }


    public SamochodySerwis getSamochodySerwis() {
        return samochodySerwis;
    }


    public HistoriaSerwis getHistoriaSerwis() {
        return historiaSerwis;
    }

    public int generujIdSamochodu() {
        return samochody.stream().mapToInt(s -> s.id).max().orElse(0) + 1;
    }

    public Samochod znajdzSamochodPoId(int id) {
        return samochody.stream()
                .filter(s -> s.id == id)
                .findFirst()
                .orElse(null);
    }

    private Samochod pobierzSamochod(int id) {
        Samochod samochod = znajdzSamochodPoId(id);
        if (samochod == null) {
            throw new IllegalArgumentException("Nie ma auta o ID: " + id);
        }
        return samochod;
    }

    public String dodajSamochod(Samochod samochod) {
        samochody.add(samochod);
        SamochodIO.zapisz(samochody);
        return "Dodano auto!";
    }

    public String usunSamochod(int id) throws Exception {
        Samochod samochod = pobierzSamochod(id);
        if (samochod.wypozyczony) {
            return "Nie można usunąć wypożyczonego auta!";
        }
        samochody.remove(samochod);
        SamochodIO.zapisz(samochody);
        return "Usunięto!";
    }

    public String wypozyczSamochod(int id, Uzytkownik uzytkownik) throws Exception {
        Samochod samochod = pobierzSamochod(id);

        if (samochod.wypozyczony) {
            return "Auto niedostępne!";
        }

        samochod.wypozyczony = true;
        samochod.wypozyczyl = uzytkownik.login;

        historiaSerwis.dodajWpisWypozyczenia(samochod, uzytkownik);
        uzytkownik.dodajAuto(samochod);

        SamochodIO.zapisz(samochody);
        return "Wypożyczono!";
    }

    public String zwrocSamochod(int id, Uzytkownik uzytkownik) throws Exception {
        Samochod samochod = pobierzSamochod(id);

        if (!samochod.wypozyczyl.equals(uzytkownik.login)) {
            return "To nie Twoje auto!";
        }

        samochod.wypozyczony = false;
        samochod.wypozyczyl = "";

        historiaSerwis.dodajWpisZwrotu(samochod, uzytkownik);
        uzytkownik.usunAuto(samochod);

        SamochodIO.zapisz(samochody);
        return "Zwrócono!";
    }


    public void wczytajSamochodyZPliku() {
        samochody.clear();
        samochody.addAll(SamochodIO.wczytaj());
    }
}