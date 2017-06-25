package Agent;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import Own.Bartender.Order;

/**
 * Représente une file d'attente au bar.
 */
public class WaitingLine implements Inanimate {
    /**
     * Représente la file des commandes (i.e. étudiant et demande)
     */
    private ArrayList<Order> orderLine;

    public WaitingLine() {
        orderLine = new ArrayList<>();
    }

    /**
     * Méthode permettant à une commande d'entrer dans la file d'attente
     * @param order Commande concernée
     */
    public void enterLine(Order order) {
        orderLine.add(order);
    }

    /**
     * Renvoie la prochaine commande à traiter
     * @return Commande semi-aléatoire dans les premières à traiter
     */
    public Order getNextOrder() {
        int nbStudent = orderLine.size() < 3 ? orderLine.size() : 3;
        int randomNum = ThreadLocalRandom.current().nextInt(0, nbStudent);
        return orderLine.remove(randomNum);
    }

    /**
     * Teste si la file est vide
     * @return true si oui
     */
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
