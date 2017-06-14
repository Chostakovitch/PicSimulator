package Util;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by aureliedigeon on 14/06/2017.
 */
public class DataPicker {
    static List<String> lines;

    public DataPicker() {
        BufferedReader reader = null;
        lines = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader("resources/parsed-data-students.csv"));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Random r = new Random();
        String randomLine = lines.get(r.nextInt(lines.size()));
    }

    public static String[] getRandomLine() {
        Random r = new Random();
        String randomLine = lines.get(r.nextInt(lines.size()));
        String[] res = randomLine.split("\",\"");
        return res;
    }
}
