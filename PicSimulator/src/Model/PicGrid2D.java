package Model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.grid.SparseGridPortrayal2D;

/**
 * Cette classe représente le Pic graphiquement.
 * Elle permet de décrire une grille 2D comportant plusieurs éléments sur la même case.
 * Elle lui ajoute l'heure actuelle de la simulation, en terme de "temps Pic".
 */
public class PicGrid2D extends SparseGridPortrayal2D {
	/**
	 * Police utilisée pour l'heure
	 */
	Font font;
	
	/**
	 * Modèle contenant l'heure effective
	 */
	Pic pic;
	
	public PicGrid2D(SimState state) {
		super();
		pic = (Pic) state;
		font = new Font("SansSerif", 0, 25);
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		super.draw(object, graphics, info);
		String s = new String();
		if(pic != null) {
			Instant currentTime = pic.getTime();
			
			//Récupération d'un horaire agnostique
			LocalTime agnTime = LocalTime.from(currentTime.atZone(ZoneId.systemDefault()));
			
			//Formattage
			s = agnTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
			
			//Affichage
			graphics.setColor(Color.BLUE);
			graphics.drawString(s, (int)info.clip.x + 10, (int)(info.clip.y + font.getStringBounds(s, graphics.getFontRenderContext()).getHeight()));
		}
	}
}
