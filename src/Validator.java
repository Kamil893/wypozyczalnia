class Validator {

    static boolean isValidId(int id) {
        return id > 0;
    }

    static boolean isValidLogin(String login) {
        return login != null && login.length() >= 3;
    }

    static boolean isValidKlasaSamochodu(String klasa) {
        return klasa != null && !klasa.isBlank();
    }
}