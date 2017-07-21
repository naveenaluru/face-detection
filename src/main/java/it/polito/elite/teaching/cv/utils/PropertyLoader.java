package it.polito.elite.teaching.cv.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyLoader {

   static Properties prop = new Properties();
   static boolean loaded = false;

   public static String getAmazonKey() {
      if (!loaded) {
         init();
         loaded=true;
      }
      return prop.getProperty("key");
   }

   public static String getAmazonSecretKey() {
      if (!loaded) {
         init();
      }
      return prop.getProperty("secretKey");
   }

   public static void init() {


      InputStream inputStream = null;

      inputStream =
         PropertyLoader.class.getClassLoader().getResourceAsStream("credentials.properties");

      // load a properties file
      try {
         prop.load(inputStream);
      } catch (IOException e) {
         e.printStackTrace();
      }


   }

}


