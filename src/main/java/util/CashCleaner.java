package util;

import java.io.File;

public class CashCleaner {

    public static void cleanPhotosCash(){
        File directory = new File("current-session/downloads/photos");

        for(File file : directory.listFiles()){
            if(file.delete()){
                System.out.println("File has been successfully deleted");
            }
        }
    }
}
