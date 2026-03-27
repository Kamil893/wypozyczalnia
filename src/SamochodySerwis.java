import java.util.*;
import java.util.stream.Collectors;

public class SamochodySerwis {

    private final Wypozyczalnia wypozyczalnia;

    public SamochodySerwis(Wypozyczalnia wypozyczalnia) {
        this.wypozyczalnia = wypozyczalnia;
    }

    public List<Samochod> filtrujPoKlasie(String nazwaKlasy) {
        return wypozyczalnia.getSamochody().stream()
                .filter(samochod -> samochod.klasa.name().equalsIgnoreCase(nazwaKlasy))
                .collect(Collectors.toList());
    }

    public List<Samochod> filtrujPoKlasieIWypozyczeniu(String nazwaKlasy, boolean czyWypozyczony) {
        return wypozyczalnia.getSamochody().stream()
                .filter(samochod -> samochod.klasa.name().equalsIgnoreCase(nazwaKlasy)
                        && samochod.wypozyczony == czyWypozyczony)
                .collect(Collectors.toList());
    }

    public List<Samochod> szukaj(String fraza) {
        String frazaLower = fraza.toLowerCase();
        return wypozyczalnia.getSamochody().stream()
                .filter(samochod -> samochod.marka.toLowerCase().contains(frazaLower)
                        || samochod.model.toLowerCase().contains(frazaLower))
                .collect(Collectors.toList());
    }

    public String statystyki() {
        long liczbaDostepnych = wypozyczalnia.getSamochody().stream()
                .filter(samochod -> !samochod.wypozyczony).count();
        long liczbaWypozyczonych = wypozyczalnia.getSamochody().size() - liczbaDostepnych;
        return "Dostępne: " + liczbaDostepnych + ", Wypożyczone: " + liczbaWypozyczonych;
    }

    public int ileSamochodowWKlasie(String nazwaKlasy) {
        return (int) wypozyczalnia.getSamochody().stream()
                .filter(samochod -> samochod.klasa.name().equalsIgnoreCase(nazwaKlasy))
                .count();
    }

    public void sortujAutaAlfabetycznie() {
        wypozyczalnia.getSamochody().sort(Comparator.comparing(samochod -> samochod.marka.toLowerCase()));
    }
    public List<Samochod> pobierzDostepneSamochody() {
        return wypozyczalnia.getSamochody().stream()
                .filter(samochod -> !samochod.wypozyczony)
                .collect(Collectors.toList());
    }

    public List<Samochod> pobierzSamochodyWypozyczonePrzez(String login) {
        return wypozyczalnia.getSamochody().stream()
                .filter(samochod -> samochod.wypozyczony && samochod.wypozyczyl.equals(login))
                .collect(Collectors.toList());
    }

    public Samochod znajdzPoId(int id) {
        return wypozyczalnia.getSamochody().stream()
                .filter(samochod -> samochod.id == id)
                .findFirst()
                .orElse(null);
    }
}