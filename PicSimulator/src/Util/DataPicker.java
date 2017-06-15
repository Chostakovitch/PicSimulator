package Util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by aureliedigeon on 14/06/2017.
 */
public class DataPicker {
    static List<String> studentsData;
    static HashMap<LocalDate, Integer> studentPerDay;

    public DataPicker() {
        initStudents();
        initStudentsPerDay();
    }

    public void initStudents() {
        BufferedReader reader;
        studentsData = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader("resources/parsed-data-students.csv"));
            String line = reader.readLine();
            while (line != null) {
                studentsData.add(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initStudentsPerDay() {
        BufferedReader reader;
        studentPerDay = new HashMap<LocalDate, Integer>();
        try {
            reader = new BufferedReader(new FileReader("resources/students_per_day.csv"));
            reader.readLine(); // Skip header
            String line = reader.readLine();
            String[] data;
            while (line != null) {
                data = line.split(",");
                System.out.println("HHHH---------------------HHHH");
                System.out.println(data[0]);
                System.out.println(data[1]);
                System.out.println("---------------------");
                System.out.println(LocalDate.parse(data[0]));
                System.out.println(Integer.parseInt(data[1]));
                System.out.println("HHH---------------------HHH");

                studentPerDay.put(LocalDate.parse(data[0]), Integer.parseInt(data[1]));
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getRandomLineStudent(LocalDate date) {
        Locale locale = Locale.FRANCE;
        Random r = new Random();
        String[] res;
        String[] jours;
        do {
            String randomLine = studentsData.get(r.nextInt(studentsData.size()));
            res = randomLine.split("\",\"");
            jours = res[20].replace(" ", "").split(",");
        } while (!Arrays.asList(jours).contains(date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale)));
        return res;
    }

    public static Integer getStudentPerDayOf(LocalDate date) {
        Integer studentsNumber = studentPerDay.get(date);
        if (studentsNumber == null) throw new IllegalArgumentException("Date non accepted");
        return studentsNumber;
    }
}
