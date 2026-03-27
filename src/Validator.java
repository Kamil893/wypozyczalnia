class Validator {

    static boolean isValidLogin(String login) {
        return login != null && login.length() >= 3;
    }

}