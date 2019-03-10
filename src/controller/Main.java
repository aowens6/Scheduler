/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.DBConnection;

/**
 *
 * @author Austyn
 */
public class Main extends Application {
  
  @Override
  public void start(Stage stage) throws Exception {
//    Locale.setDefault(new Locale("es"));
    FXMLLoader root = new FXMLLoader(getClass().getResource("/view/login.fxml"));
    root.setResources(ResourceBundle.getBundle("view.Schedule"));
    Parent rootParent = (Parent) root.load();

    Scene scene = new Scene(rootParent);
    
    stage.setScene(scene);
    stage.show();
    
  }

  public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
    
    DBConnection.connect();
//    Logging.init();
//    Logging.logger.info("msg from MAIN");
    launch(args);
    DBConnection.disconnect();

  }
  
}
