package View;

import java.awt.Graphics2D;

import Agent.Stair;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

public class StairPortrayal extends ScalablePortrayal<Stair> {
	private static final long serialVersionUID = 1L;
	private static final String baseImageName = "stairs";
	private static final String extension = ".png";
	
	public StairPortrayal(SimState state) {
		super(state);
		
		//Paramétrage de la classe
		entityType = Stair.class;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		if(object instanceof Stair) {
			setBackground(baseImageName + extension);
		}
		
		//Dessin effectif
		super.draw(object, graphics, info);
	}
}
