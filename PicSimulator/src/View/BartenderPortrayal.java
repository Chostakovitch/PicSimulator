package View;

import java.awt.*;

import Agent.Bartender;
import Agent.Student;
import Model.Pic;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

import static State.BartenderState.*;

/**
 * Implémentation des représentations avec mise à l'échelle pour les permanenciers.
 */
public class BartenderPortrayal extends ScalablePortrayal<Bartender> {
	private static final long serialVersionUID = 1L;

	private static final String baseImageName = "bartender";
	
	private static final String extension = ".png";
	/**
	 * Modèle de la simulation
	 */
	Pic pic;
	
	BartenderPortrayal(SimState state) {
		//Le dessin effectif est délayé
		super(state, false, true);
		
		//Paramétrage de la classe
		entityType = Bartender.class;
	}

	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		//Calcul préalable
		super.draw(object, graphics, info);
		
		if(object instanceof Bartender) {
			Bartender bartender = (Bartender) object;
			String suffixDir = getDirectionSuffix(bartender.getDirection());
			String suffixState = getStateSuffix(bartender);
			setBackground(baseImageName + suffixDir + suffixState + extension);
		}
        
        //Dessin effectif
		drawEffectivly();
	}
	
	/**
	 * Obtient un suffixe standardisé d'image en fonction de l'état du permanencier
	 * @param bartender Permanencier
	 * @return Suffixe d'état
	 */
	public String getStateSuffix(Bartender bartender) {
		//Ne fait rien
		if(bartender.getBartenderState() == NOTHING) return "_nothing";
		
		//Utilise la caisse
		else if(bartender.getBartenderState() == WAITING_CHECKOUT ||bartender.getBartenderState() == USING_CHECKOUT) return "_checkout";
		
		//Attend pour le fût
		else if(bartender.getBartenderState() == WAITING_BARREL) return "_wait";
		
		//Utilise le fût
		else if(bartender.getBartenderState() == USING_BARREL) return "_barrel";
		
		//Remplit le fût
		else if(bartender.getBartenderState() == REFILLING_BARREL) return "_refilling";
		
		//Répare le fût
		else if(bartender.getBartenderState() == FIXING_BARREL) return "_fixing";
		
		//On ne devrait pas arriver ici
		else return "_nothing";
	}
}
