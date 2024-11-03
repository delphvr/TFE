package org.example;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//base on: https://www.tutorialspoint.com/postgresql/postgresql_java.htm

public class Database {

    Connection c = null;
    Statement stmt = null;

    void getConnection(){
        Properties props = new Properties();
        try {
            InputStream input = new FileInputStream("dbconfig.properties");
            props.load(input);
            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            error(e);
        }
    }

    public void dropAllTables() {
        try {
            Statement stmt = c.createStatement();
            
            ResultSet tables = stmt.executeQuery("SELECT tablename FROM pg_tables WHERE schemaname = 'public';");
            List<String> tableNames = new ArrayList<>();
            while (tables.next()) {
                tableNames.add(tables.getString("tablename"));
            }
            tables.close();

            for (String tableName : tableNames) {
                stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName + " CASCADE;");
            }
        } catch (Exception e) {
            error(e);
        }
    }

    void createInitialTables(){
        String users = "CREATE TABLE users " +
                        "(id SERIAL PRIMARY KEY," +
                        " first_name VARCHAR(255) NOT NULL, " +
                        " last_name VARCHAR(255) NOT NULL, " +
                        " email VARCHAR(255) NOT NULL)";
        String profession_values = "CREATE TABLE profession_values " +
                        "(profession VARCHAR(255) PRIMARY KEY  NOT NULL)";
        String user_professions = "CREATE TABLE user_professions " +
                        "(user_id INT NOT NULL, " +
                        " profession VARCHAR(255) NOT NULL, " +
                        " PRIMARY KEY (user_id, profession), " + 
                        " FOREIGN KEY (user_id) REFERENCES users(id)," +
                        " FOREIGN KEY (profession) REFERENCES profession_values(profession))";
        String organizers = "CREATE TABLE organizers " +
                        "(user_id INT PRIMARY KEY  NOT NULL," +
                        " FOREIGN KEY (user_id) REFERENCES users(id))";

        createTable(users);
        createTable(profession_values);
        createTable(user_professions);
        createTable(organizers);
    }

    void createTable(String statement){
        try {
            stmt = c.createStatement();
            stmt.executeUpdate(statement);
            stmt.close();
        } catch (Exception e){
            error(e);
        }
    }

    void addUser(String firstName, String lastName, String email){
        String statement = "INSERT INTO users (first_name, last_name, email) VALUES (?, ?, ?)";
        try {
            c.setAutoCommit(false);
            
            PreparedStatement pstmt = c.prepareStatement(statement);
            pstmt.setString(1, firstName);  
            pstmt.setString(2, lastName); 
            pstmt.setString(3, email);     

            pstmt.executeUpdate();
            pstmt.close();
            c.commit();
        } catch (Exception e) {
            error(e);
        }
    }

    void closeConnexion(){
        try {
            c.close();
        } catch (Exception e) {
            error(e);
        }
    }

    void error(Exception e){
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
    }
    
}
