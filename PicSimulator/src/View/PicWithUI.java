package View;

import java.awt.Color;

import javax.swing.JFrame;

import Agent.*;
import Model.Pic;
import Util.Constant;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;

/**
 * Représentation graphique du Pic, permettant d'afficher l'interprétation 
 * de l'état de la simulation.
 */
public class PicWithUI extends GUIState {
	
    private Display2D display;
    
    /**
     * Grille 2D pouvant représenter une grille avec plusieurs agents par case
     */
    private PicGrid2D gridGUI;

    public PicWithUI(SimState state) {
        super(state);
        //Ajout de l'état de la simulation (temps du Pic)
        gridGUI = new PicGrid2D(state); 
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
        
        //Représentation des étudiants
        gridGUI.setPortrayalForClass(Student.class, getStudentPortrayal());

        //Représentation des murs
        gridGUI.setPortrayalForClass(Wall.class, getWallPortrayal());

        //Représentation du comptoir de bar
        gridGUI.setPortrayalForClass(BarCounter.class, getBarCounterPortrayal());

        //Représentation des fûts de bière
        gridGUI.setPortrayalForClass(Barrel.class, getBarrelPortrayal());

        //Représentation des files d'attente
        gridGUI.setPortrayalForClass(WaitingLine.class, getWaitingLinePortrayal());
        
        display.reset();
        display.setBackdrop(Color.WHITE);
        
        display.repaint();
    }

    /**
     * Renvoie la représentation graphique d'un fût de bière
     * @return Rond de couleur
     */
    private OvalPortrayal2D getBarrelPortrayal() {
        OvalPortrayal2D r = new OvalPortrayal2D();
        r.paint = Color.YELLOW;
        r.filled = true;
        return r;
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

    /**
     * Renvoie la représentation graphique d'un mur
     * @return Carré de couleur
     */
    private RectanglePortrayal2D getWallPortrayal() {
        RectanglePortrayal2D r = new RectanglePortrayal2D();
        r.paint = Color.GRAY;
        r.filled = true;
        return r;
    }

    /**
     * Renvoie la représentation graphique d'un morceau du comptoir du bar
     * @return Carré de couleur
     */
    private RectanglePortrayal2D getBarCounterPortrayal() {
        RectanglePortrayal2D r = new RectanglePortrayal2D();
        r.paint = Color.BLACK;
        r.filled = true;
        return r;
    }

    /**
     * Renvoie la représentation graphique d'une file d'attente
     * @return Carré de couleur
     */
    private RectanglePortrayal2D getWaitingLinePortrayal() {
        RectanglePortrayal2D r = new RectanglePortrayal2D();
        r.paint = Color.CYAN;
        r.filled = true;
        return r;
    }
    
    private OvalPortrayal2D getStudentPortrayal() {
    	return new StudentPortrayal(state);
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
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
    }
}