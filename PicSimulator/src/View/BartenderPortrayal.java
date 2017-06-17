package View;

import java.awt.*;

import Agent.Bartender;
import Model.Pic;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

import static State.BartenderState.*;

/**
 * Implémentation des représentations avec mise à l'échelle pour les permanenciers.
 */
public class BartenderPortrayal extends ScalablePortrayal<Bartender> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Modèle de la simulation
	 */
	Pic pic;
	
	BartenderPortrayal(SimState state) {
		super(state);
		//Les permanenciers sont rouges
		paint = Color.RED;
		
		//Paramétrage de la classe
		entityType = Bartender.class;
	}

	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		//Dessin de base
		super.draw(object, graphics, info);

		if(object instanceof Bartender) {
			Bartender bartender = (Bartender) object;
			//Largeur d'un éventuel cercle intérieur
			int secondaryWidth = (int)(effectiveWidth / 3);

			if(bartender.getBartenderState() == NOTHING) {
				graphics.setColor(Color.RED);
				graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
			}
			else if(bartender.getBartenderState() == WAITING_CHECKOUT ||bartender.getBartenderState() == USING_CHECKOUT) {
				graphics.setColor(Color.PINK);
				graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
			}
			else if(bartender.getBartenderState() == WAITING_BARREL) {
				graphics.setColor(Color.BLUE);
				graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
			}
			else if(bartender.getBartenderState() == USING_BARREL) {
				graphics.setColor(Color.BLACK);
				graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
			}
			else if(bartender.getBartenderState() == REFILLING_BARREL) {
				graphics.setColor(Color.CYAN);
				graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
			}
			else if(bartender.getBartenderState() == FIXING_BARREL) {
				graphics.setColor(Color.ORANGE);
				graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
			}
		}
	}
}
