package Agent;

import Util.Beer;
import Util.Constant;

/**
 * Agent statique représentant un fût de bière.
 */
public class Barrel implements Inanimate {
    /**
     * Indique si le barrel est utilisé par un Bartender
     */
    private boolean isOccupied;

    private boolean isBeingRefilled;

    /**
     * Quantité actuelle du fût
     */
    private float actualQuantity;

    /**
     * Type de la bière contenue dans le fût
     */
    private Beer beerType;

    /**
     * Permet de savoir si le fût a besoin d'être remplit
     */
    private boolean needRefill;

    public Barrel(Beer beer) {
        isOccupied = false;
        beerType = beer;
        actualQuantity = Constant.BARREL_CAPACITY;
        needRefill = false;
    }

    /**
     * Retourne le type de la bière
     * @return beer
     */
    public Beer getType() {
        return beerType;
    }

    /**
     * Permet de savoir si le fût est en train d'être remplit
     * @return true si elle est en cours de recharge
     */
    public boolean isBeingRefilled() {
        return isBeingRefilled;
    }

    /**
     * Permet de savoir si le fût est occupé pour servir
     * @return true si il est utilisé
     */
    public boolean isOccupied() {
        return isOccupied;
    }

    /**
     * Return vrai si le fût a besoin d'être remplit
     * @return needRefill
     */
    public boolean needRefill() {
        return needRefill;
    }

    /**
     * Action utilisé par les bartender pour utiliser le fût
     * @param quantity quantité tirée
     * @return true si on a pu l'utiliser, faux si il n'y a pas assez de bière dans le fût
     */
    public boolean pullBeer(float quantity) {
        if(quantity > actualQuantity) {
            needRefill = true;
            return false;
        }
        else {
            isOccupied = true;
            actualQuantity-=quantity;
            return true;
        }
    }

    /**
     * Fin de l'utilisation du fût
     */
    public void endUseBarrel() {
        isOccupied = false;
    }

    /**
     * Action utilisé par les bartenders pour remplir le fût
     */
    public void refill() {
        actualQuantity = Constant.BARREL_CAPACITY;
        isBeingRefilled = true;
    }

    /**
     * Fin du remplissage
     */
    public void endRefill() {
        isBeingRefilled = false;
    }
}
