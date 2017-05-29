package Agent;

import Model.Beings;
import sim.engine.SimState;
import sim.engine.Steppable;

public class Student implements Steppable {
    public int x, y;

    public Student() {

    }

    @Override
    public void step(SimState state) {
        Beings beings = (Beings) state;
    }
}