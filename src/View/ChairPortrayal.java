package View;

import java.awt.Graphics2D;

import Agent.Chair;
import Model.Pic;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

public class ChairPortrayal extends ScalablePortrayal<Chair> {
private static final long serialVersionUID = 1L;
	
	private static final String baseImageName = "chair";
	private static final String extension = ".png";

	public ChairPortrayal(SimState state) {
		//Le dessin effectif est délayé
		super(state);
		
		//Paramétrage de la classe
		entityType = Chair.class;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		if(object instanceof Chair) {
			Chair chair = (Chair) object;
			String suffixDir = getDirectionSuffix(chair.getDirection());
			setBackground(baseImageName + suffixDir + extension);
		}
        
        //Dessin effectif
		super.draw(object, graphics, info);
	}
}
