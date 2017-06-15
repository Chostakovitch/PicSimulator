package Util;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

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

    public static String[] getRandomLine(LocalDate date) {
        Locale locale = Locale.FRANCE;
        Random r = new Random();
        String[] res;
        String[] jours;
        do {
            String randomLine = lines.get(r.nextInt(lines.size()));
            res = randomLine.split("\",\"");
            jours = res[20].replace(" ", "").split(",");
        } while (!Arrays.asList(jours).contains(date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale)));
        return res;
    }
}
