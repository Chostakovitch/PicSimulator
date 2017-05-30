package Model;

import Util.Constant;
import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;

/**
 * Modèle du Pic. Permet de gérer l'environnement sous forme de grille, où les agents
 * animés ou non interagissent. 
 */
public class Pic extends SimState {
	private static final long serialVersionUID = 1L;
	private SparseGrid2D pic = new SparseGrid2D(Constant.PIC_WIDTH, Constant.PIC_HEIGHT);
    
    public Pic(long seed) {
        super(seed);
    }

    @Override
	public void start() {
        System.out.println("GOD DAMN SIMULATION STARTED");
        super.start();
        pic.clear();
        
        //Ajout des agents
        addAgentsBartender();
    }

    /**
     * Ajoute les permanenciers à la grille
     */
    private void addAgentsBartender() {
    	
    }
    

    public SparseGrid2D getModel() {
        return pic;
    }
}
