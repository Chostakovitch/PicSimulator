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

	private WaitingLine waitingLine;

	private float speedToServe;

	private boolean isServing;
	private Student studentToServe;


    public Bartender(WaitingLine wl, float speed) {
        waitingLine = wl;
        speedToServe = speed;
        isServing = false;
    }

    @Override
    public void step(SimState state) {
        Pic pic = (Pic) state;

        if(!isServing) {
            if(!waitingLine.isEmpty()) {
                takeOrder(pic);
            }
        }

    }

    public void takeOrder(Pic pic) {
        isServing = true;
        studentToServe = waitingLine.getStudent();
        //TODO Probablement prévenir l'étudiant qu'il a été choisi
        Beer beerToServe = studentToServe.getOrder();
        Map.Entry<Barrel, Int2D> entry = pic.getBarrel(beerToServe);
        if(entry!=null) {
            pic.getModel().setObjectLocation(this, entry.getValue());
            //TODO
        }
        else {
            //TODO Faire en sorte de prévenir que la bière n'existe pas à l'étudiant
        }
    }
}
