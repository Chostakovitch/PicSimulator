package Agent;

import Model.Pic;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Agent dynamique représentant un étudiant.
 */
public class Student implements Steppable {
	private static final long serialVersionUID = 1L;

    public Student() {

    }

    @Override
    public void step(SimState state) {
        Pic pic = (Pic) state;
    }
}