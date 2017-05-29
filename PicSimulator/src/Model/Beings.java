package Model;

import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;

public class Beings extends SimState {
    public static int NB_DIRECTIONS = 8;
    private SparseGrid2D yard = new SparseGrid2D(Constant.GRID_SIZE, Constant.GRID_SIZE);
    public Beings(long seed) {
        super(seed);
    }

    public void start() {
        System.out.println("Simulation started");
        super.start();
        yard.clear();
        addAgentsBartender();
    }

    public SparseGrid2D getYard() {
        return yard;
    }

    private void addAgentsBartender() {
    }
}
