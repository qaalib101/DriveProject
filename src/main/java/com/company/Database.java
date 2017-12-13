package com.company;

/**
 * Created by si8822fb on 11/28/2017.
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    static String db_url = "jdbc:sqlite:driver_gui.db";
    private static String getData = "SELECT * FROM changes LIMIT 5";
    private static Connection conn;
    private static ResultSet rs;
    Database(){
        try{
            Class.forName("org.sqlite.JDBC");
            createTable();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void createTable(){
        try{
            conn = DriverManager.getConnection(db_url);
            Statement st = conn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS changes (file_name text, file_size real, action_date text, action_type text) WITHOUT ROWID");
            st.close();
            conn.close();
        }catch(SQLException sqle){
            System.out.println(sqle.getMessage());
            sqle.printStackTrace();
        }
    }
    public HashMap<Integer,ArrayList<String>> getAllData(){
        HashMap<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();

        try{
            conn = DriverManager.getConnection(db_url);
            Statement st = conn.createStatement();
            rs = st.executeQuery(getData);
            int accumulator = 0;
            while(rs.next()){
                ArrayList<String> list = new ArrayList<String>();
                list.add(rs.getString(1));
                list.add(Double.toString(rs.getDouble(2)));
                list.add(rs.getString(3));
                list.add(rs.getString(4));
                map.put(accumulator, list);
                accumulator += 1;
            }
            rs.close();
            st.close();
            conn.close();
        }catch (SQLException se){
            System.out.println(se);
        }
        return map;
    }
    public void insertValues(String fileName, double fileSize, String date, String action){
        try{
            String sqlQuery = "INSERT INTO changes (file_name, file_size, action_date, action_type) VALUES(?, ?, ?, ?)";
            conn = DriverManager.getConnection(db_url);
            PreparedStatement st = conn.prepareStatement(sqlQuery);
            st.setString(1, fileName);
            st.setDouble(2, fileSize);
            st.setString(3, date);
            st.setString(4, action);
            st.execute();
            st.close();
            conn.close();
        }
        catch (SQLException sqle){
            System.out.println(sqle.getMessage());
            sqle.printStackTrace();
        }
    }
}
