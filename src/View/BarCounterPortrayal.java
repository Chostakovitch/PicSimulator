package View;

import java.awt.Graphics2D;

import Agent.BarCounter;
import Agent.Wall;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

public class BarCounterPortrayal extends ScalablePortrayal<BarCounter> {
	private static final long serialVersionUID = 1L;
	private static final String baseImageName = "barcounter";
	private static final String extension = ".png";
	
	public BarCounterPortrayal(SimState state) {
		super(state);
		
		//Paramétrage de la classe
		entityType = BarCounter.class;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		if(object instanceof BarCounter) {
			BarCounter br = (BarCounter) object;
			setBackground(baseImageName + getConfigurationSuffix(br) + extension);
		}
		
		//Dessin effectif
		super.draw(object, graphics, info);
	}
	
	/**
	 * Obtient un suffixe standardisé d'image en fonction de la configuration du comptoir
	 * @param counter Comptoir
	 * @return Suffixe de configuration
	 */
	private String getConfigurationSuffix(BarCounter counter) {
		switch(counter.getConfiguration()) {
			case BOTTOM: return "_bottom";
			case RIGHT: return "_right";
			case BOTTOM_RIGHT: return "_bottom_right_corner";
			default: return "_bottom";
		}
	}
}
