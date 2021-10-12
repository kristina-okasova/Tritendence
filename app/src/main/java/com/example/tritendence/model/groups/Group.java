package com.example.tritendence.model.groups;

import com.example.tritendence.model.TrainingUnit;
import com.example.tritendence.model.users.Athlete;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {
    private static final String ONE = "1";
    private static final String TWO = "2";
    private static final String THREE = "3";
    private static final String FOUR = "4";
    private static final String FIVE = "5";
    private static final String CATEGORY_ONE = "Športovci";
    private static final String CATEGORY_TWO = "Prípravka";
    private static final String CATEGORY_THREE = "Začiatočníci";
    private static final String CATEGORY_FOUR = "Rekreační";
    private static final String CATEGORY_FIVE = "Dospelácí";

    private final int ID;
    private String name;
    private String category;
    private final ArrayList<TrainingUnit> timetable;
    private final ArrayList<Athlete> athletesOfGroup;

    public Group(int ID, String name, String category) {
        this.ID = ID;
        this.name = name;
        this.category = category;

        this.timetable = new ArrayList<>();
        this.athletesOfGroup = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTrainingUnit(TrainingUnit unit) {
        this.timetable.add(unit);
    }

    public void addAthlete(Athlete newAthlete) {
        int i;
        for (i = 0; i < this.athletesOfGroup.size(); i++) {
            if (this.athletesOfGroup.get(i).getSurname().compareTo(newAthlete.getSurname()) == 0) {
                if (this.athletesOfGroup.get(i).getName().compareTo(newAthlete.getName()) > 0)
                    break;
            }
            else if (this.athletesOfGroup.get(i).getSurname().compareTo(newAthlete.getSurname()) > 0)
                break;
        }
        this.athletesOfGroup.add(i, newAthlete);
    }

    public ArrayList<TrainingUnit> getTimetable() {
        return this.timetable;
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return category;
    }

    public int getID() {
        return this.ID;
    }

    public ArrayList<Athlete> getAthletesOfGroup() {
        return this.athletesOfGroup;
    }

    public ArrayList<String> getNamesOfAthletesOfGroup() {
        ArrayList<String> namesOfAthletes = new ArrayList<>();
        for (Athlete athlete : this.athletesOfGroup) {
            if (athlete.getGroupID() == -1)
                continue;
            namesOfAthletes.add(athlete.getFullName());
        }

        return namesOfAthletes;
    }

    public int getNumberOfAthletes() {
        return this.athletesOfGroup.size();
    }

    public String getCategoryTranslation() {
        switch(this.category) {
            case ONE:
                return CATEGORY_ONE;
            case TWO:
                return CATEGORY_TWO;
            case THREE:
                return CATEGORY_THREE;
            case FOUR:
                return CATEGORY_FOUR;
            case FIVE:
                return CATEGORY_FIVE;
            default:
                return null;
        }
    }

    public void deleteAthleteFromGroup(Athlete athlete) {
        for (int i = 0; i < this.athletesOfGroup.size(); i++) {
            if (this.athletesOfGroup.get(i).getFullName().equals(athlete.getFullName()))
                this.athletesOfGroup.remove(i);
        }
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
