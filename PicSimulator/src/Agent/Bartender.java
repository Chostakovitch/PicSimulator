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
     * Boolean pour savoir si le bartender est entrain d'utiliser un fût
     */
	private boolean isUsingBarrel;

    /**
     * Boolean pour savoir i le bartender est en train de remplir un fût
     */
	private boolean isRefillingBarrel;

    /**
     * Boolean pour savoir si le bartender est dans la file pour un fût
     */
    private boolean isInWaitingLine;


    private Barrel barrelUsed;


	/**
     * Etudiant entrain d'être servi
	 */
	private Student studentToServe;



    public Bartender(WaitingLine wl, int speedServe, int speedRefill , Int2D pos) {
        waitingLine = wl;
        speedToServe = speedServe;
        initialPosition = pos;
        timeServing = speedServe;
        barrelUsed = null;
        speedToRefill = speedRefill;
        isInWaitingLine = false;
        isUsingBarrel = false;
    }

    @Override
    public void step(SimState state) {
        Pic pic = (Pic) state;

        if(!isInWaitingLine && !isUsingBarrel) {
            if(!waitingLine.isEmpty()) {
                takeOrder(pic);
            }
        }
        else {
            if(isInWaitingLine) { // dans la file d'attente
                if(barrelUsed.isMyTurnToUse(this)) {
                    barrelUsed.useBarrel(this);
                    isUsingBarrel = true;
                    isInWaitingLine = false;
                }
            }
            else {
                if(isRefillingBarrel) { //en train de recharger un fût
                    if(timeRefilling == 0) {
                        isRefillingBarrel = false;
                        timeRefilling = speedToRefill;
                    }
                    else
                        timeRefilling--;
                }
                else {
                    if (timeServing == 0) { //a fini de servir
                        studentToServe.endServe();
                        studentToServe = null;
                        barrelUsed.endUseBarrel();
                        barrelUsed = null;
                        timeServing = speedToServe;
                        pic.getModel().setObjectLocation(this, initialPosition);
                        isUsingBarrel = false;
                    } else //est en train de servir
                        timeServing--;
                }
            }
        }
    }

    /**
     * Choisis un étudiant et prends sa commande
     * @param pic pic
     */
    private void takeOrder(Pic pic) {
        studentToServe = waitingLine.getStudent();
        studentToServe.serve();
        Beer beerToServe = studentToServe.getOrder();
        Map.Entry<Barrel, Int2D> entry = pic.getBarrel(beerToServe);
        if(entry!=null) {
            pic.getModel().setObjectLocation(this, entry.getValue());
            barrelUsed = entry.getKey();
            if(barrelUsed.isMyTurnToUse(this)) {
                barrelUsed.useBarrel(this);
                if(!barrelUsed.pullBeer(33f))
                    isRefillingBarrel = true;
                isUsingBarrel = true;
            }
            else {
                isInWaitingLine = true;
                barrelUsed.joinWaitingLine(this);
            }
        }
        else {
            //TODO Faire en sorte de prévenir que la bière n'existe pas à l'étudiant
        }
    }
}
