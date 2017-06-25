package Agent;

import Model.Pic;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Cette classe gère l'horloge du Pic. Elle n'est pas présente dans la grille.
 */
public class Clock implements Steppable {
	private static final long serialVersionUID = 1L;

	@Override
	public void step(SimState state) {
		Pic pic = (Pic) state;
		
		//Incrémentation du temps courant de la simulation
		pic.incrCurrentTime();
		
		//On arrête la simulation à la fermeture du Pic
		if(!pic.isPicOpened()) {
			pic.finish();
		}
	}
}
