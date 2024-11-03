package org.example;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        Database db = new Database();
        db.getConnection();

        //Just once at the begining for table initialisation
        //db.dropAllTables();
        //db.createInitialTables();

        ArrayList<String> professions = new ArrayList<>();
        professions.add("dancer");
        db.addToProfessionValuesTable(professions);
        db.addUser("ftest", "ltest", "tes@test.com", true, professions);


        db.closeConnexion();
    }
}