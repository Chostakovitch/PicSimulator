package Agent;

import Model.Pic;
import Util.Beer;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

import java.util.Map;

/**
 * Agent dynamique représentant un permanencier (serveur)
 */
public class Bartender implements Steppable {
    private static final long serialVersionUID = 1L;

    /**
     * Constante des différents mouvements possible du bartender
     */
    private final static int NOTHING = 0;
    private final static int WAITING_CHECKOUT = 1;
    private final static int USING_CHECKOUT = 2;
    private final static int WAITING_BARREL = 3;
    private final static int USING_BARREL = 4;
    private final static int REFILLING_BARREL = 5;


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
    private Student studentToServe;

    /**
     * Bière à servir à l'étudiant
     */
    private Beer beerToServe;


    /**
     * Action actuelle du bartender
     */
    private int action;


    public Bartender(WaitingLine wl, int speedServe, int speedRefill, int speedCheckoutCounter, Int2D pos) {
        waitingLine = wl;

        initialPosition = pos;
        barrelUsed = null;
        speedToRefill = speedRefill;
        timeRefilling = speedRefill;

        speedToServe = speedServe;
        timeServing = speedServe;

        speedToUseCheckoutCounter = speedCheckoutCounter;
        timeServing = speedCheckoutCounter;

        action = NOTHING;
    }

    @Override
    public void step(SimState state) {
        Pic pic = (Pic) state;

        switch (action) {
            case NOTHING:
                if (!waitingLine.isEmpty()) {
                    takeOrder(pic);
                }
                break;

            case WAITING_CHECKOUT:
                CheckoutCounter cc = pic.getCheckoutCounter();
                if (cc.isMyTurnToUse(this)) {
                    cc.useCounter(this);
                    action = USING_CHECKOUT;
                }
                break;

            case USING_CHECKOUT:
                if (timeUsingCheckoutCounter == 0) {
                    timeUsingCheckoutCounter = speedToUseCheckoutCounter;
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
                if (timeServing == 0) { //a fini de servir
                    barrelUsed.endUseBarrel();
                    barrelUsed = null;
                    pic.getModel().setObjectLocation(this, initialPosition);
                    studentToServe.endServe(beerToServe.getPrice());
                    studentToServe = null;
                    beerToServe = null;
                    timeServing = speedToServe;
                    action = NOTHING;
                } else //est en train de servir
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
     * Choisis un étudiant et prends sa commande
     *
     * @param pic pic
     */
    private void takeOrder(Pic pic) {
        studentToServe = waitingLine.getStudent();
        studentToServe.serve();
        beerToServe = studentToServe.getOrder();
        if (beerToServe.getPrice() > studentToServe.getPayutc()) {
            studentToServe.notEnoughMoney();
            studentToServe = null;
            beerToServe = null;
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
        pic.getModel().setObjectLocation(this, checkoutLocation.getX(), checkoutLocation.getY() - 1);
    }

    private void moveToBarrel(Pic pic) {
        Map.Entry<Barrel, Int2D> entry = pic.getBarrel(beerToServe);
        if (entry != null) {
            Int2D locationBarrel = entry.getValue();
            pic.getModel().setObjectLocation(this, locationBarrel.getX(), locationBarrel.getY() - 1);
            barrelUsed = entry.getKey();
            if (barrelUsed.isMyTurnToUse(this)) {
                barrelUsed.useBarrel(this);
                if (!barrelUsed.pullBeer(33f))
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
}
