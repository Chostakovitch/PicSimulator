package View;

import Agent.Barrel;
import Model.Pic;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

import java.awt.*;

import static State.StudentState.*;

/**
 * Implémentation des représentations avec mise à l'échelle pour les fûts de bière.
 * 
 */
public class BarrelPortrayal extends ScalablePortrayal<Barrel> {
	private static final long serialVersionUID = 1L;

	/**
	 * Modèle de la simulation
	 */
	Pic pic;

	public BarrelPortrayal(SimState state) {
		super(state);
		
		//Les fûts sont jaune
		paint = Color.YELLOW;
		
		//Paramétrage de la classe
		entityType = Barrel.class;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		//Dessin de base
		super.draw(object, graphics, info);
		
		if(object instanceof Barrel) {
			Barrel barrel = (Barrel) object;
            //Largeur d'un éventuel cercle intérieur
        	int secondaryWidth = (int)(effectiveWidth / 3);
		}
	}
}
