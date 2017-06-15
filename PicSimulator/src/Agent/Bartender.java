package Agent;

import static State.BartenderState.NOTHING;
import static State.BartenderState.REFILLING_BARREL;
import static State.BartenderState.USING_BARREL;
import static State.BartenderState.USING_CHECKOUT;
import static State.BartenderState.WAITING_BARREL;
import static State.BartenderState.WAITING_CHECKOUT;

import java.util.Map;

import Enum.Beer;
import Model.Pic;
import Own.Bartender.Order;
import State.BartenderState;
import Util.Constant;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

/**
 * Agent dynamique représentant un permanencier (serveur)
 */
public class Bartender implements Steppable {
    private static final long serialVersionUID = 1L;
    /**
     * Waiting line attribué au permanencier
     */
    private WaitingLine waitingLine;

    /**
     * Nombre de tour pour servir une bière
     */
    private int speedToServe;

    /**
     * Nombre de tour pour remplir un fût
     */
    private int speedToRefill;

    /**
     * Nombre de tour pour utiliser la caisse enregistreuse
     * //TODO Peut-être une constante ?
     * //TODO Peut-être peut diminuer au fil de la perm ?
     */
    private int speedToUseCheckoutCounter;

    /**
     * Position initial à laquelle il retourne après avoir servi
     */
    private Int2D initialPosition;

    /**
     * Temps pour servir
     * Quand il est en train de servir, descend de 1 à chaque tour
     * Et quand il est à 0 on sert on revient à speedToServe
     */
    private int timeServing;

    /**
     * Temps pour remplir un fût
     * Quand il est en train de remplir le fût, descend de 1 à chaque tour
     * Et quand il est à 0 on a fini de remplir on revient à speedToRefill
     */
    private int timeRefilling;

    /**
     * Temps pour utiliser la caisse
     * Quand il est en train d'utiliser la caisse, descend de 1 à chaque tour
     * Et quand il est à 0 on a fini de remplir on revient à speedToUseCheckoutCounter
     */
    private int timeUsingCheckoutCounter;

    /**
     * Fût actuellement utilisé
     * Null quand le bartender n'est pas en train de servir
     */
    private Barrel barrelUsed;

    /**
     * Etudiant entrain d'être servi
     */
    private Order currentOrder;

    /**
     * Action actuelle du bartender
     */
    private BartenderState action;


    public Bartender(WaitingLine wl, int speedServe, int speedRefill, int speedCheckoutCounter, Int2D pos) {
        waitingLine = wl;

        initialPosition = pos;
        barrelUsed = null;
        speedToRefill = speedRefill;
        timeRefilling = speedRefill;

        speedToServe = speedServe;
        timeServing = speedServe;

        speedToUseCheckoutCounter = speedCheckoutCounter;
        timeUsingCheckoutCounter = speedCheckoutCounter;

        action = NOTHING;
    }

    @Override
    public void step(SimState state) {
        Pic pic = (Pic) state;
        switch (action) {
            case NOTHING:
                if(!waitingLine.isEmpty()) {
                    takeOrder(pic);
                }
                break;

            case WAITING_CHECKOUT:
                CheckoutCounter cc = pic.getCheckoutCounter();
                //Le permanencier est le premier de la file, il peut utiliser la caisse
                if (cc.isMyTurnToUse(this)) {
                    cc.useCounter(this);
                    action = USING_CHECKOUT;
                }
                break;

            case USING_CHECKOUT:
            	//Le permanencier a fini d'enregistrer la transaction
                if (timeUsingCheckoutCounter == 0) {
                    timeUsingCheckoutCounter = speedToUseCheckoutCounter;
                    pic.getCheckoutCounter().endUseCounter();
                    moveToBarrel(pic);
                } else
                    timeUsingCheckoutCounter--;
                break;

            case WAITING_BARREL:
                if (barrelUsed.isMyTurnToUse(this)) {
                    barrelUsed.useBarrel(this);
                    action = USING_BARREL;
                }
                break;

            case USING_BARREL:
            	//L'étudiant est servi
                if (timeServing == 0) {
                    //Notification de fin d'utilisation du fût
                	barrelUsed.endUseBarrel();
                    barrelUsed = null;
                    
                    //Le permanencier revient à sa position initiale
                    //TODO gérer le déplacement avec le plus court chemin comme dans étudiant ; corollaire : classe gérant les entités se déplaçant et mutualisant les méthodes ?
                    pic.getModel().setObjectLocation(this, initialPosition);
                    
                    //Remplissage effectif de la coupe, virement sur le compte du Pic
                    performOrder(pic);
                    
                    timeServing = speedToServe;
                    action = NOTHING;
                }
                //Toujours en train de servir
                else 
                    timeServing--;
                break;

            case REFILLING_BARREL:
                if (timeRefilling == 0) {
                    timeRefilling = speedToRefill;
                    action = USING_BARREL;
                } else
                    timeRefilling--;
                break;
        }
    }

    /**
     * Choix d'une commande à traiter
     *
     * @param pic pic
     */
    private void takeOrder(Pic pic) {
    	if(!pic.isBeerTime()) throw new IllegalStateException("Le Pic ne sert pas encore de bière!");
        currentOrder = waitingLine.getNextOrder();
        Student student = currentOrder.getStudent();
        Beer beer = currentOrder.getBeerType();
        student.serve();
        //On vérifie que l'étudiant a assez d'argent
        if(!student.getPayUTC().hasEnough(beer.getPrice())) {
            student.notEnoughMoney();
            currentOrder = null;
        } else {
            CheckoutCounter cc = pic.getCheckoutCounter();
            if (cc.isMyTurnToUse(this)) {
                cc.useCounter(this);
                action = USING_CHECKOUT;
            } else {
                action = WAITING_CHECKOUT;
                cc.joinWaitingLine(this);
            }
            moveToCheckout(pic);
        }
    }

    private void moveToCheckout(Pic pic) {
        Int2D checkoutLocation = pic.getCheckoutCounterLocation();
        pic.getModel().setObjectLocation(this, checkoutLocation.getX(), checkoutLocation.getY() + 1);
    }

    private void moveToBarrel(Pic pic) {
        Map.Entry<Barrel, Int2D> entry = pic.getBarrel(currentOrder.getBeerType());
        if (entry != null) {
            Int2D locationBarrel = entry.getValue();
            pic.getModel().setObjectLocation(this, locationBarrel.getX(), locationBarrel.getY() + 1);
            barrelUsed = entry.getKey();
            if (barrelUsed.isMyTurnToUse(this)) {
                barrelUsed.useBarrel(this);
                if (!barrelUsed.pullBeer(Constant.CUP_CAPACITY))
                    action = REFILLING_BARREL;
                else
                    action = USING_BARREL;
            } else {
                action = WAITING_BARREL;
                barrelUsed.joinWaitingLine(this);
            }
        } else {
            System.out.println("Cette bière n'existe pas ?");
            //TODO Faire en sorte de prévenir que la bière n'existe pas à l'étudiant
        }
    }
    
    /**
     * Réalise la commande : remplissage du verre et prélèvement.
     * @param pic Modèle de la simulation
     */
    private void performOrder(Pic pic) {
    	Student student = currentOrder.getStudent();
    	Beer beer = currentOrder.getBeerType();
    	student.getCup().fillCup(beer);
        pic.getCheckoutCounter().getAccount().transfer(student.getPayUTC(), beer.getPrice());
        student.endServe();
        currentOrder = null;
    }
}