import java.io.IOException;
import java.sql.*;
public class Database {

	public Database() throws IOException
	{
	}
	public void houseTable(int people,int amount, int bills) throws Exception{
		String s = "insert into House(People, BillAmount, Bills) values('" + Integer.toString(people) +"', '"+Integer.toString(amount)+"', '"+Integer.toString(bills)+"')";
		System.out.println(s);
		connect(s);
	}

	public static void connect(String s) throws IOException
	{
		Connection conn=null;
		 try {
	            // db parameters
	            String url = "jdbc:sqlite:M:/git/SYSC-3010-project/SYSC-3010-airQulaity/hera.db";
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
	}
	public static void main(String[] args) throws Exception{
        //connect();
    }
}
