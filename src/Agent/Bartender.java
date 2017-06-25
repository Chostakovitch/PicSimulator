package Agent;

import static State.BartenderState.FIXING_BARREL;
import static State.BartenderState.NOTHING;
import static State.BartenderState.REFILLING_BARREL;
import static State.BartenderState.USING_BARREL;
import static State.BartenderState.USING_CHECKOUT;
import static State.BartenderState.WAITING_BARREL;
import static State.BartenderState.WAITING_CHECKOUT;

import java.util.Map;

import Enum.Beer;
import Enum.Direction;
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
     * Temps actuel pour fixer un fût.
     * Quand il est en train de reparer le fût, descend de 1 à chaque tour
     * Et quand il est à 0 on a fini de reparer on revient à Constant.BARTENDER_TIME_TO_FIXE_BARREL
     */
    private int timeFixingBarrel;

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
    private BartenderState bartenderState;

    /**
     * Direction physique du permanencier
     */
    private Direction direction;

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

        timeFixingBarrel = Constant.BARTENDER_TIME_TO_FIXE_BARREL;

        bartenderState = NOTHING;
        direction = Direction.BOTTOM;
    }

    @Override
    public void step(SimState state) {
        Pic pic = (Pic) state;
        switch (bartenderState) {
            case NOTHING:
            	direction = Direction.BOTTOM;
                if(!waitingLine.isEmpty() || currentOrder != null) {
                    takeOrder(pic);
                }
                break;

            case WAITING_CHECKOUT:
            	direction = Direction.TOP;
                CheckoutCounter cc = pic.getCheckoutCounter();
                //Le permanencier est le premier de la file, il peut utiliser la caisse
                if (cc.isMyTurnToUse(this)) {
                    cc.useCounter(this);
                    bartenderState = USING_CHECKOUT;
                }
                break;

            case USING_CHECKOUT:
            	direction = Direction.TOP;
            	//Le permanencier a fini d'enregistrer la transaction
                if (timeUsingCheckoutCounter == 0) {
                    timeUsingCheckoutCounter = speedToUseCheckoutCounter;
                    pic.getCheckoutCounter().endUseCounter();
                    moveToBarrel(pic);
                } else
                    timeUsingCheckoutCounter--;
                break;

            case FIXING_BARREL:
            	direction = Direction.TOP;
                if (timeFixingBarrel == 0) {
                    timeFixingBarrel = Constant.BARTENDER_TIME_TO_FIXE_BARREL;
                    barrelUsed.fixBarrel();
                    pic.removeUnavailableBarrel(barrelUsed);
                    if(barrelUsed.pullBeer(Constant.CUP_CAPACITY))
                        bartenderState = USING_BARREL;
                    else
                        bartenderState = REFILLING_BARREL;
                } else
                    timeFixingBarrel--;
                break;

            case WAITING_BARREL:
            	direction = Direction.TOP;
                if (barrelUsed.isMyTurnToUse(this)) {
                    barrelUsed.useBarrel(this);
                    if(barrelUsed.isBarrelBroken()) {
                        pic.addUnavailableBarrel(barrelUsed);
                        bartenderState = FIXING_BARREL;
                    }
                    else
                        bartenderState = USING_BARREL;
                }
                else {
                    if(barrelUsed.isBarrelBroken()) {
                        pic.getModel().setObjectLocation(this, initialPosition);
                        barrelUsed.leaveWaitingLine(this);
                        barrelUsed = null;
                        bartenderState = NOTHING;
                    }
                }
                break;

            case USING_BARREL:
            	direction = Direction.TOP;
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
                    bartenderState = NOTHING;
                }
                //Toujours en train de servir
                else 
                    timeServing--;
                break;

            case REFILLING_BARREL:
            	direction = Direction.TOP;
                if (timeRefilling == 0) {
                    timeRefilling = speedToRefill;
                    bartenderState = USING_BARREL;
                    barrelUsed.refill();
                    barrelUsed.pullBeer(Constant.CUP_CAPACITY);
                } else
                    timeRefilling--;
                break;
        }
    }

    /**
     * Retourne la direction graphique du permanencier
     * @return Direction
     */
    public Direction getDirection() {
		return direction;
	}

	/**
     * Choix d'une commande à traiter
     * @param pic pic
     */
    private void takeOrder(Pic pic) {
        Student student;
    	if(currentOrder == null) {
            currentOrder = waitingLine.getNextOrder();
            student = currentOrder.getStudent();
            student.serve();
        }
        else
            student = currentOrder.getStudent();

        Barrel b = pic.getBarrel(currentOrder.getBeerType()).getKey();
        if(pic.isBarrelBroken(b)) { //TODO || barrel.isGettingRefill() ?
            currentOrder.changeOrder(student.reorder( pic.getUnavailableBarrel()));
        }
        Beer beer = currentOrder.getBeerType();
        //On vérifie que l'étudiant a assez d'argent
        if(!student.getPayUTC().hasEnough(beer.getPrice())) {
            student.notEnoughMoney();
            currentOrder = null;
        } else {
            moveToCheckout(pic);
        }
    }

    /**
     * Bouge le permanencier jusqu'à la caisse
     * @param pic Modèle de la simulation
     */
    private void moveToCheckout(Pic pic) {
        CheckoutCounter cc = pic.getCheckoutCounter();
        if (cc.isMyTurnToUse(this)) {
            cc.useCounter(this);
            bartenderState = USING_CHECKOUT;
        } else {
            bartenderState = WAITING_CHECKOUT;
            cc.joinWaitingLine(this);
        }
        Int2D checkoutLocation = pic.getCheckoutCounterLocation();
        pic.getModel().setObjectLocation(this, checkoutLocation.getX(), checkoutLocation.getY() + 1);
    }

    /**
     * Bouge le permanencier jusqu'au fût
     * @param pic Modèle de la simulation
     */
    private void moveToBarrel(Pic pic) {
        Map.Entry<Barrel, Int2D> entry = pic.getBarrel(currentOrder.getBeerType());
        if (entry != null) {
            Int2D locationBarrel = entry.getValue();
            pic.getModel().setObjectLocation(this, locationBarrel.getX(), locationBarrel.getY() + 1);
            barrelUsed = entry.getKey();
            if (barrelUsed.isMyTurnToUse(this)) {
                barrelUsed.useBarrel(this);
                if(barrelUsed.isBarrelBroken()) {
                    pic.addUnavailableBarrel(barrelUsed);
                    bartenderState = FIXING_BARREL;
                }
                else if (!barrelUsed.pullBeer(Constant.CUP_CAPACITY))
                    bartenderState = REFILLING_BARREL;
                else
                    bartenderState = USING_BARREL;
            } else {
                bartenderState = WAITING_BARREL;
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

    /**
     * Retourne l'action actuelle executée par le bartender
     * @return state
     */
    public BartenderState getBartenderState() {
        return bartenderState;
    }
}