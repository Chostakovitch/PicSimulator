package View;

import java.awt.Graphics2D;

import Agent.Wall;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

public class WallPortrayal extends ScalablePortrayal<Wall> {
	private static final long serialVersionUID = 1L;
	private static final String baseImageName = "wall";
	private static final String extension = ".png";
	
	public WallPortrayal(SimState state) {
		super(state);
		
		//Paramétrage de la classe
		entityType = Wall.class;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		if(object instanceof Wall) {
			Wall w = (Wall) object;
			setBackground(baseImageName + getConfigurationSuffix(w) + extension);
		}
		
		//Dessin effectif
		super.draw(object, graphics, info);
	}
	
	/**
	 * Obtient un suffixe standardisé d'image en fonction de la configuration du mur
	 * @param wall Mur
	 * @return Suffixe de configuration
	 */
	private String getConfigurationSuffix(Wall wall) {
		switch(wall.getConfiguration()) {
			case BOTTOM: return "_bottom";
			case TOP: return "_top";
			case LEFT: return "_left";
			case RIGHT: return "_right";
			case BOTTOM_LEFT: return "_bottom_left_corner";
			case BOTTOM_RIGHT: return "_bottom_right_corner";
			case TOP_LEFT: return "_top_left_corner";
			case TOP_RIGHT: return "_top_right_corner";
			case INTERSECT_VERTICAL: return "_horizontal_intersect";
			case INTERSECT_HORIZONTAL: return "_vertical_intersect";
			case TOP_INTERSECT: return "_top_intersection";
			case LEFT_INTERSECT: return "_right_intersection";
			case RIGHT_INTERSECT: return "_left_intersection";
			case BOTTOM_INTERSECT: return "_bottom_intersection";
			case CENTER_INTERSECT: return "_center_intersection";
			default: return "_bottom";
		}
	}
}
