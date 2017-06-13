package Agent;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import Own.Bartender.Order;

public class WaitingLine implements Inanimate {
    /**
     * Représente la file des commandes (i.e. étudiant et demande)
     */
    private ArrayList<Order> studentLine;

    public WaitingLine() {
        studentLine = new ArrayList<>();
    }

    public void enterLine(Order order) {
        studentLine.add(order);
        System.out.println("oui");
    }

    public Order getNextOrder() {
        int nbStudent = studentLine.size() < 3 ? studentLine.size() : 3;
        int randomNum = ThreadLocalRandom.current().nextInt(0, nbStudent);
        return studentLine.remove(randomNum);
    }

    public boolean isEmpty() {
        return studentLine.isEmpty();
    }
    
    /**
     * Retourne le nombre d'étudiants dans la ligne
     * @return Entier
     */
    public int getStudentNumber() {
    	return studentLine.size();
    }
}
