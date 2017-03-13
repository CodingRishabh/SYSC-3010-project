package group6;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Database {

	private int value;
	private String time;
	String url;
    public Connection connect(String url)  {
        Connection conn = null;
        try {
            // db parameters
            this.url = url;
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
		return conn;
    }
    
    public void insert(int value, String time) {
    	this.value=value;
    	this.time=time;
        String sql = "INSERT INTO SensorData(value,time) VALUES(?,?)";
 
        try (Connection conn = this.connect(url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            // set the corresponding param
        	pstmt.setInt(1, value);
            pstmt.setString(2, time);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void delete() {
        String sql = "DELETE FROM SensorData";
 
        try (Connection conn = this.connect(url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void display(){
        String sql = "SELECT value, time FROM SensorData";
        
        try (Connection conn = this.connect(url);
        		Statement stmt = conn.createStatement()) {
        	ResultSet rs    = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("Value") +  "\t" + rs.getString("Time") + "\t");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        //run here
    }
}

