package View;

import java.awt.Color;

import Agent.Bartender;
import Model.Pic;
import sim.engine.SimState;

/**
 * Implémentation des représentations avec mise à l'échelle pour les permanenciers.
 */
public class BartenderPortrayal extends ScalablePortrayal<Bartender> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Modèle de la simulation
	 */
	Pic pic;
	
	public BartenderPortrayal(SimState state) {
		super(state);
		//Les permanenciers sont rouges
		paint = Color.RED;
		
		//Paramétrage de la classe
		entityType = Bartender.class;
	}
}
