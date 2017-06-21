package View;

import java.awt.Graphics2D;

import Agent.CheckoutCounter;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

public class CheckoutPortrayal extends ScalablePortrayal<CheckoutCounter> {
	private static final long serialVersionUID = 1L;
	private static final String baseImageName = "checkout";
	private static final String extension = ".png";
	
	public CheckoutPortrayal(SimState state) {
		super(state);
		
		//Paramétrage de la classe
		entityType = CheckoutCounter.class;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		if(object instanceof CheckoutCounter) {
			setBackground(baseImageName + extension);
		}
		
		//Dessin effectif
		super.draw(object, graphics, info);
	}
}
