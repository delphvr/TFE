package org.example;

public class Main {
    public static void main(String[] args) {

        Database db = new Database();
        db.getConnection();

        //Just once at the begining for table initialisation
        //db.dropAllTables();
        //db.createInitialTables();

        db.addUser("ftest", "ltest", "tes@test.com");


        db.closeConnexion();
    }
}