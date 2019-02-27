package util;

import java.util.logging.Logger;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import static util.Logging.logger;
 
public class DBConnection {
  
  public static Connection conn;
  private static final String DBUSER = "U05bpp";
  private static final String DBPASS = "53688457833";
  private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  private static final String DB_URL = "jdbc:mysql://52.206.157.109/" + DBUSER;
  private static Logger logger;
  
  public static void connect() throws ClassNotFoundException, SQLException, IOException{
    Class.forName(JDBC_DRIVER);
//    Logging.init();
//    Logging.logger.info( DBUSER);
    conn = DriverManager.getConnection(DB_URL, DBUSER, DBPASS);
  }

  public static Connection getConn(){ 
    return conn;
  }
  
  public static void disconnect(){
    try{
      conn.close();
    }catch (Exception e){
      e.printStackTrace();
    }
   
  }
}