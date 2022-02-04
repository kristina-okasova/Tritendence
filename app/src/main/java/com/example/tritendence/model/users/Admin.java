package com.example.tritendence.model.users;

public class Admin extends Member {
    private final String email;
    private String sport, theme;

    public Admin(int ID, String name, String surname, String email, String theme) {
        super(ID, name, surname);
        this.email = email;
        this.theme = theme;
        this.sport = "";
    }

    public String getEmail() {
        return this.email;
    }

    public String getSport() {
        return this.sport;
    }

    public String getTheme() {
        return this.theme;
    }

    public String getSportTranslation() {
        String sportTranslation;
        switch (this.sport) {
            case "Atletika":
                sportTranslation = "Athletics";
                break;
            case "Plávanie":
                sportTranslation = "Swimming";
                break;
            case "Cyklistika":
                sportTranslation = "Cycling";
                break;
            default:
                sportTranslation = "";
        }
        return sportTranslation;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
