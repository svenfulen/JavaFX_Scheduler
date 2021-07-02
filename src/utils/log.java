package utils;

import main_package.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class log {
    /**
     * Create the log file if it's not already there.
     */
    public static void createLogFile(){
        try {
            File myObj = new File("login_activity.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Write a string to the log file.
     * @param str The text to write to the log
     */
    public static void writeToLog(String str){
        try {
            FileWriter myWriter = new FileWriter("login_activity.txt", true);
            myWriter.append(str).append("\n");
            //myWriter.write(str);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            System.out.println(str);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Record the login as a success or failure
     * @param login_success true to record login success, false to record login failure
     */
    public static void record_login(boolean login_success){
        createLogFile();
        if(login_success){
            writeToLog("Login success at " + LocalDateTime.now().toString() + " - User: " + Main.user_name);
        }
        else {
            writeToLog("Login failure at " + LocalDateTime.now().toString() + " - User: " + Main.user_name);
        }
    }


}
