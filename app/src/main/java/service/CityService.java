package service;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by jack on 2015/12/30.
 */
public class CityService {
    public static boolean saveCityName(Context context,String city) {
        try {
        File file = new File(context.getFilesDir(), "city.txt");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write((city ).getBytes());
            fos.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String getcity(Context context){
        try {
            File file = new File(context.getFilesDir(),"city.txt");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String str = bufferedReader.readLine();

            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
