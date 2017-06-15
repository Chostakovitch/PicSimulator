package Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by aureliedigeon on 14/06/2017.
 */
public class DataPicker {
    static List<String> studentsData;
    static HashMap<LocalDate, Integer> studentPerDay;

    private DataPicker() {
        initStudents();
        initStudentsPerDay();
    }
    
    private static DataPicker instance = new DataPicker();
    
    public static DataPicker getInstance() {
    	return instance;
    }

    private void initStudents() {
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

    private void initStudentsPerDay() {
        BufferedReader reader;
        studentPerDay = new HashMap<>();
        try {
            reader = new BufferedReader(new FileReader("resources/students_per_day.csv"));
            reader.readLine(); // Skip header
            String line = reader.readLine();
            String[] data;
            while (line != null) {
                data = line.split(",");
                studentPerDay.put(LocalDate.parse(data[0]), Integer.parseInt(data[1]));
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getRandomLineStudent() {
        Random r = new Random();
        String[] res;
        String randomLine = studentsData.get(r.nextInt(studentsData.size()));
        res = randomLine.split("\",\"");
        return res;
    }

    public Integer getStudentPerDayOf(LocalDate date) {
        Integer studentsNumber = studentPerDay.get(date);
        if (studentsNumber == null) throw new IllegalArgumentException("Date non accepted");
        return studentsNumber;
    }
}
