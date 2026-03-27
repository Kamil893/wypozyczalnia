import java.util.*;

/**
 * Enum klas samochodów w wypożyczalni.
 */
enum KlasaSamochodu {
    OSOBOWE, SUV, PREMIUM, KOMPAKT
}

/**
 * Enum skrzyni biegów samochodów.
 */
enum SkrzyniaBiegow {
    MANUALNA, AUTOMATYCZNA
}

/**
 * Klasa reprezentująca samochód w wypożyczalni.
 */
public class Samochod {
    int id;
    String marka, model;
    SkrzyniaBiegow skrzynia;
    KlasaSamochodu klasa;
    int miejsca;
    boolean wypozyczony = false;
    String wypozyczyl = "";

    // Historia wypożyczeń samochodu
    List<String> historia = new ArrayList<>();

    /**
     * Konstruktor samochodu
     */
    public Samochod(int id, String marka, String model, SkrzyniaBiegow skrzynia, KlasaSamochodu klasa, int miejsca) {
        this.id = id;
        this.marka = marka;
        this.model = model;
        this.skrzynia = skrzynia;
        this.klasa = klasa;
        this.miejsca = miejsca;
    }

    /**
     * Reprezentacja samochodu do wyświetlenia w konsoli
     */
    @Override
    public String toString() {
        return id + ". " + marka + " " + model + " | " + skrzynia + " | " + klasa +
                (wypozyczony ? " | WYPOŻYCZONY przez: " + wypozyczyl : " | DOSTĘPNY");
    }

    /**
     * Format samochodu do zapisu w pliku
     */
    public String toFile() {
        return id + "," + model + "," + marka + "," + skrzynia.name() + "," +
                klasa.name() + "," + miejsca + "," + wypozyczyl;
    }

    /**
     * Edycja danych samochodu
     * @param marka nowa marka
     * @param model nowy model
     * @param skrzynia nowa skrzynia
     * @param klasa nowa klasa
     * @param miejsca nowa liczba miejsc
     */
    public void edytujSamochod(String marka, String model, SkrzyniaBiegow skrzynia, KlasaSamochodu klasa, int miejsca) {
        if (marka != null && !marka.isEmpty()) this.marka = marka;
        if (model != null && !model.isEmpty()) this.model = model;
        if (skrzynia != null) this.skrzynia = skrzynia;
        if (klasa != null) this.klasa = klasa;
        if (miejsca > 0) this.miejsca = miejsca;
    }
}