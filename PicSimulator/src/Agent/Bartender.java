package Agent;

import Model.Pic;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Agent dynamique représentant un permanencier (serveur)
 */
public class Bartender implements Steppable {
	private static final long serialVersionUID = 1L;

    public Bartender() {

    }

    @Override
    public void step(SimState state) {
        Pic pic = (Pic) state;
    }
}
