package View;

import java.awt.Color;

import javax.swing.JFrame;

import Agent.Bartender;
import Model.Pic;
import Util.Constant;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;

/**
 * Représentation graphique du Pic, permettant d'afficher l'interprétation 
 * de l'état de la simulation.
 */
public class PicWithUI extends GUIState {
	
    private Display2D display;
    
    /**
     * Grille 2D pouvant représenter une grille avec plusieurs agents par case
     */
    private SparseGridPortrayal2D gridGUI;

    public PicWithUI(SimState state) {
        super(state);
        gridGUI = new SparseGridPortrayal2D();
    }

    @Override
	public void start() {
        super.start();
        setupPortrayals();
    }

    @Override
	public void load(SimState state) {
        super.load(state);
        setupPortrayals();
    }

    private void setupPortrayals() {
        Pic pic = (Pic) state;
        
        //Grille utilisée pour l'affichage
        gridGUI.setField(pic.getModel());
        
        //Représentation des permanenciers
        gridGUI.setPortrayalForClass(Bartender.class, getBartenderPortrayal());
        
        display.reset();
        display.setBackdrop(Color.orange);
        display.repaint();
    }

    /**
     * Renvoie la représentation graphique d'un permanencier
     * @return Rond de couleur
     */
    private OvalPortrayal2D getBartenderPortrayal() {
        OvalPortrayal2D r = new OvalPortrayal2D();
        r.paint = Color.RED;
        r.filled = true;
        return r;
    }

    @Override
	public void init(Controller c) {
        super.init(c);
        display = new Display2D(Constant.FRAME_WIDTH, Constant.FRAME_HEIGHT, this);
        display.setClipping(false);
        
        JFrame displayFrame = display.createFrame();
        displayFrame.setTitle("Pic");
        c.registerFrame(displayFrame); 
        
        displayFrame.setVisible(true);
        display.attach(gridGUI, "Pic");
    }

    @Override
	public Object getSimulationInspectedObject() { return state; }

    @Override
	public Inspector getInspector() {
        Inspector i  =  super.getInspector();
        i.setVolatile(true);
        return  i;
    }
}