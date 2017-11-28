package com.company;

/**
 * Created by si8822fb on 11/28/2017.
 */
import java.sql.*;
public class Database {
    static String db_url = "jdbc:mysql://localhost:3306/driver_gui";
    static String user = System.getenv("qaalib");      // TODO set this environment variable
    static String password = System.getenv("farah101");
    static String getData = "SELECT * FROM changes LIMIT 5";
    private static Connection conn;
    private static ResultSet resultSet;
    Database(){
        try{
            conn = DriverManager.getConnection(db_url, user, password);

        }
        catch(SQLException sqle){
            System.out.println(sqle);
        }
    }

}
