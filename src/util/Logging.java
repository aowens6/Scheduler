/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Austyn
 */
public class Logging{
  
  public static Logger logger = Logger.getLogger(Logging.class.getClass().getName());
  private static Handler handler = null;
  
  public static void init() throws IOException {
    
    handler = new FileHandler("info.log", true);
    handler.setFormatter(new SimpleFormatter());
    logger.addHandler(handler);
    logger.setLevel(Level.INFO);
    
  }
}

//public class Test {
//    static Handler fileHandler = null;
//    private static final Logger logger = Logger.getLogger(Test.class
//            .getClass().getName());
//
//    public static void setup() {
//
//        try {
//            fileHandler = new FileHandler("./logfile.log");//file
//            SimpleFormatter simple = new SimpleFormatter();
//            fileHandler.setFormatter(simple);
//
//            logger.addHandler(fileHandler);//adding Handler for file
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//        }
//
//    }