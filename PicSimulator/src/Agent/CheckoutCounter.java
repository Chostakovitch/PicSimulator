package Agent;

import java.util.ArrayList;

import Own.Person.PayUTCAccount;

public class CheckoutCounter implements Inanimate {
	/**
	 * File d'attente de permanenciers en attente d'utilisation de la caisse
	 */
    private ArrayList<Bartender> waitingList;
    
    /**
     * Permanencier en train d'utiliser la caisse, null si libre
     */
    private Bartender usedBy;
    
    /**
     * Compte PayUTC du Pic. Représente le gain total réalisé pendant la simulation.
     */
    private PayUTCAccount account;

    public CheckoutCounter() {
        waitingList = new ArrayList<>();
        usedBy = null;
        account = new PayUTCAccount();
    }
    
    public PayUTCAccount getAccount() {
		return account;
	}

	/**
     * Permet à un permanencier de savoir si c'est à lui d'utiliser la caisse enregitreuse
     * C'est à lui si :
     *      - Personne ne l'utilise et il est premier dans la liste
     *      - Personne ne l'utilise et la liste est vide
     * @param b permanencier
     * @return Vrai si c'est au tour du permancier d'utiliser la caisse
     */
    boolean isMyTurnToUse(Bartender b) {
        return usedBy == null && (waitingList.isEmpty() || waitingList.get(0) == b);
    }

    /**
     * Permet à un bartender de rentrer dans une file d'attente pour utiliser un fût
     * @param b bartender
     */
    void joinWaitingLine(Bartender b) {
        waitingList.add(b);
    }

    /**
     * Le bartender devient "propriétaire" de la caisse, personne d'autre ne peut l'utiliser
     * @param b permanencier
     */
    void useCounter(Bartender b) {
        if(usedBy != null) //TODO Gerer l'exception quelque part
            throw new IllegalStateException("Cette caisse est déjà utilisé");
        else {
            usedBy = b;
            if(!waitingList.isEmpty() && waitingList.get(0) == b)
                waitingList.remove(b);
        }
    }

    /**
     * Fin de l'utilisation de la caisse
     */
    void endUseCounter() {
        usedBy = null;
    }
}
