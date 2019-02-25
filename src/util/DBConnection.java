package util;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
public class DBConnection {
  
  public static Connection conn;
  private static final String DBUSER = "U05bpp";
  private static final String DBPASS = "53688457833";
  private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  private static final String DB_URL = "jdbc:mysql://52.206.157.109/" + DBUSER;
  
  public static void connect() throws ClassNotFoundException, SQLException{
    
    try {
      Class.forName(JDBC_DRIVER);
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL, DBUSER, DBPASS);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
//    Statement stmt;
//
//    ResultSet rs = null;
//    try {
//      stmt = conn.createStatement();
//      rs = stmt.executeQuery("SELECT * FROM address");
//      while (rs.next()) {
//        String country = rs.getString("address");
//        System.out.println(country);
//      }
//    } catch (SQLException ex) {
//            ex.printStackTrace();
//
//    }
  }

  public static Connection getConn(){ 
    return conn;
  }
  
  public static void closeConn(){
    try{
      conn.close();
    }catch (Exception e){
      e.printStackTrace();
    }
   
  }
}