package View;

import Agent.Bartender;
import Model.Pic;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;

import javax.swing.*;
import java.awt.*;

public class PicWithUI extends GUIState {
    private Display2D display;
    private SparseGridPortrayal2D yardPortrayal;

    public PicWithUI(SimState state) {
        super(state);
        yardPortrayal = new SparseGridPortrayal2D();
    }

    public void start() {
        super.start();
        setupPortrayals();
    }

    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
    }

    private void setupPortrayals() {
        Pic pic = (Pic) state;
        yardPortrayal.setField(pic.getYard());
        yardPortrayal.setPortrayalForClass(Bartender.class, getInsectPortrayal());

        display.reset();
        display.setBackdrop(Color.orange);
        // redraw the display
        //addBackgroundImage();
        display.repaint();
    }

    private OvalPortrayal2D getInsectPortrayal() {
        OvalPortrayal2D r = new OvalPortrayal2D();
        r.paint = Color.RED;
        r.filled = true;
        return r;
    }

    private OvalPortrayal2D getTypeBPortrayal() {
        OvalPortrayal2D r = new OvalPortrayal2D();
        r.paint = Color.GRAY;
        r.filled = true;
        return r;
    }

    public void init(Controller c) {
        super.init(c);
        int FRAME_SIZE = 600;
        display = new Display2D(FRAME_SIZE, FRAME_SIZE,this);
        display.setClipping(false);
        JFrame displayFrame = display.createFrame();
        displayFrame.setTitle("Pic");
        c.registerFrame(displayFrame); // so the frame appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach( yardPortrayal, "Yard" );
    }

    public  Object  getSimulationInspectedObject()  {  return  state;  }

    public Inspector getInspector() {
        Inspector i  =  super.getInspector();
        i.setVolatile(true);
        return  i;
    }
}