package View;

import java.awt.Graphics2D;

import Agent.BigTable;
import Agent.Table;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

public class BigTablePortrayal extends ScalablePortrayal<BigTable> {
private static final long serialVersionUID = 1L;
	
	private static final String baseImageName = "bigtable";
	private static final String extension = ".png";
	
	public BigTablePortrayal(SimState state) {
		//Le dessin effectif est délayé
		super(state);
		
		//Paramétrage de la classe
		entityType = BigTable.class;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		if(object instanceof BigTable) {
			setBackground(baseImageName + extension);
		}
        
        //Dessin effectif
		super.draw(object, graphics, info);
	}
}

