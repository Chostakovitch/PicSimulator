package Agent;

import Model.Beings;
import sim.engine.SimState;
import sim.engine.Steppable;

public class Bartender implements Steppable {
    public int x, y;

    public Bartender() {

    }

    @Override
    public void step(SimState state) {
        Beings beings = (Beings) state;
    }
}
