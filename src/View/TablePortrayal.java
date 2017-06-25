package View;

import java.awt.Graphics2D;

import Agent.Table;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

public class TablePortrayal extends ScalablePortrayal<Table> {
private static final long serialVersionUID = 1L;
	
	private static final String baseImageName = "table";
	private static final String extension = ".png";
	
	public TablePortrayal(SimState state) {
		//Le dessin effectif est délayé
		super(state);
		
		//Paramétrage de la classe
		entityType = Table.class;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		if(object instanceof Table) {
			setBackground(baseImageName + extension);
		}
        
        //Dessin effectif
		super.draw(object, graphics, info);
	}
}
