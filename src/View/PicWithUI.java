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
import sim.portrayal.SimplePortrayal2D;
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

        //Représentation de la caisse enregistreuse
        gridGUI.setPortrayalForClass(CheckoutCounter.class, getCheckoutCounterPortrayal());
        
        //Représentation des chaises
        gridGUI.setPortrayalForClass(Chair.class, getChairPortrayal());
        
        //Représentation des escaliers
        gridGUI.setPortrayalForClass(Stair.class, getStairsPortrayal());
        
        //Représentation des tables
        gridGUI.setPortrayalForClass(Table.class, getTablePortrayal());
        
        //Représentation des tables extérieures
        gridGUI.setPortrayalForClass(BigTable.class, getBigTablePortrayal());
        
        display.reset();
        display.setBackdrop(new Color(223, 226, 219));
        
        display.repaint();
    }
    
    private SimplePortrayal2D getBigTablePortrayal() { return new BigTablePortrayal(state); }

    private SimplePortrayal2D getTablePortrayal() { return new TablePortrayal(state); }
    
    private SimplePortrayal2D getChairPortrayal() { return new ChairPortrayal(state); }
    
    private SimplePortrayal2D getStairsPortrayal() { return new StairPortrayal(state); }
    
    private SimplePortrayal2D getBarrelPortrayal() { return new BarrelPortrayal(state); }

    private SimplePortrayal2D getBartenderPortrayal() { return new BartenderPortrayal(state); }

    private SimplePortrayal2D getWallPortrayal() { return new WallPortrayal(state); }

    private SimplePortrayal2D getBarCounterPortrayal() { return new BarCounterPortrayal(state); }

    private RectanglePortrayal2D getWaitingLinePortrayal() {
        RectanglePortrayal2D r = new RectanglePortrayal2D();
        r.paint = new Color(223, 226, 219);
        r.filled = true;
        return r;
    }

    private SimplePortrayal2D getCheckoutCounterPortrayal() { return new CheckoutPortrayal(state); }

    private SimplePortrayal2D getStudentPortrayal() { return new StudentPortrayal(state); }

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
}