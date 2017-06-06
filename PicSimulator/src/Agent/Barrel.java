package Agent;

import Util.Beer;
import Util.Constant;

import java.util.ArrayList;

/**
 * Agent statique représentant un fût de bière.
 */
public class Barrel implements Inanimate {

    private ArrayList<Bartender> waitingList;

    private Bartender usedBy;

    /**
     * Quantité actuelle du fût
     */
    private float actualQuantity;

    /**
     * Type de la bière contenue dans le fût
     */
    private Beer beerType;

    public Barrel(Beer beer) {
        beerType = beer;
        actualQuantity = Constant.BARREL_CAPACITY;
        waitingList = new ArrayList<>();
    }

    /**
     * Retourne le type de la bière
     * @return beer
     */
    public Beer getType() {
        return beerType;
    }

    /**
     * Permet à un permanencier de savoir si c'est à lui d'utiliser le fût
     * C'est à lui si :
     *      - Personne ne l'utilise et il est premier dans la liste
     *      - Personne ne l'utilise et la liste est vide
     * @param b permanencier
     * @return Vrai si c'est au tour du permancier d'utiliser le fût
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
     * Le bartender devient "propriétaire" du barrel, personne d'autre ne peut l'utiliser
     * @param b permanencier
     */
    void useBarrel(Bartender b) {
        if(usedBy != null) //TODO Gerer l'exception quelque part
            throw new IllegalStateException("Ce fût est déjà utilisé");
        else {
            usedBy = b;
            if(waitingList.get(0) == b)
                waitingList.remove(b);
        }
    }

    /**
     * Action utilisé par les bartender pour utiliser le fût
     * @param quantity quantité tirée
     * @return true si on a pu l'utiliser, faux si il n'y a pas assez de bière dans le fût
     */
    boolean pullBeer(float quantity) {
        if(quantity > actualQuantity) {
            return false;
        }
        else {
            actualQuantity-=quantity;
            return true;
        }
    }

    /**
     * Fin de l'utilisation du fût
     */
    void endUseBarrel() {
        usedBy = null;
    }
}
