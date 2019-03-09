/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Austyn
 */
public class Reporter {
  
  public static void main(String args []){
    
    Path path = Paths.get("src/util/AppointmentReport.txt");
    
    File file = new File(path.toString());
    
    ResultSet rs = null;
    
    try{
      //Create the file
      if (!file.createNewFile()){
        file.createNewFile(); 
      }
    }catch(IOException e){
      e.printStackTrace();
    }
    
    
    try(FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw)){
      
      bw.write("Appointment Schedules by Consultant\n\n");
      bw.write("Consultant \t Appt Title \t Description \t Location \t\t Start \t\t\t End\n\n");
      
      
      String line = "";
      try{
        DBConnection.connect();
        PreparedStatement stmt = DBConnection.conn.prepareStatement("select user.userName, " +
          "appointment.appointmentId, " +
          "appointment.title, appointment.description, appointment.location, " +
          "appointment.start, appointment.end, appointment.userId " +
          "from appointment, user where user.userId = appointment.userId");

        rs = stmt.executeQuery();
        
        while(rs.next()){
          
          line = rs.getString("userName") + "\t\t" +
                 rs.getString("title") + "\t\t" +
                 rs.getString("description") + "\t" +
                 rs.getString("location") + "\t" +
                 rs.getString("start") + "\t" +
                 rs.getString("end") + "\n";
          System.out.println(line);
                 
          bw.write(line);
          bw.newLine();
        }

      }catch(SQLException | ClassNotFoundException | IOException e){
        e.printStackTrace();
      }
      

    }catch(IOException e){
      e.printStackTrace();
    }finally{
      DBConnection.disconnect();
    }
        
  }
  
}
