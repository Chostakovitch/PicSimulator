package Agent;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import Own.Bartender.Order;

public class WaitingLine implements Inanimate {
    /**
     * Représente la file des commandes (i.e. étudiant et demande)
     */
    private ArrayList<Order> orderLine;

    public WaitingLine() {
        orderLine = new ArrayList<>();
    }

    public void enterLine(Order order) {
        orderLine.add(order);
        System.out.println("oui");
    }

    public Order getNextOrder() {
        int nbStudent = orderLine.size() < 3 ? orderLine.size() : 3;
        int randomNum = ThreadLocalRandom.current().nextInt(0, nbStudent);
        return orderLine.remove(randomNum);
    }

    public boolean isEmpty() {
        return orderLine.isEmpty();
    }
    
    /**
     * Retourne le nombre d'étudiants dans la ligne
     * @return Entier
     */
    public int getStudentNumber() {
    	return orderLine.size();
    }
}
