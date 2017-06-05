package Agent;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class WaitingLine implements Inanimate {
    /**
     * Liste des étudiant dans la file
     */
    private ArrayList<Student> studentLine;

    public WaitingLine() {
        studentLine = new ArrayList<>();
    }

    public void enterLine(Student st) {
        studentLine.add(st);
    }

    public Student getStudent() {
        int nbStudent = studentLine.size()<3?studentLine.size():3;
        int randomNum = ThreadLocalRandom.current().nextInt(0, nbStudent);
        return studentLine.remove(randomNum);
    }

    public boolean isEmpty() {
        return studentLine.isEmpty();
    }
}
