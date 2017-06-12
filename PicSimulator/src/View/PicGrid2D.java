package View;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import Model.Pic;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.grid.SparseGridPortrayal2D;

/**
 * Cette classe représente le Pic graphiquement.
 * Elle permet de décrire une grille 2D comportant plusieurs éléments sur la même case.
 * Elle lui ajoute l'heure actuelle de la simulation, en terme de "temps Pic".
 * Elle lui ajoute le nombre d'étudiants à l'intérieur du Pic
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
		font = new Font("Dialog", Font.BOLD, 30);
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		super.draw(object, graphics, info);
		if(pic != null) {
			Instant currentTime = pic.getTime();
			
			//Récupération d'un horaire agnostique
			LocalTime agnTime = LocalTime.from(currentTime.atZone(ZoneId.systemDefault()));
			
			//Formattage
			String time = agnTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
			String nbStudents = "Étudiants : " + String.valueOf(pic.getStudentsInside());
			
			//Affichage supplémentaire d'une grille pour délimiter les cellules
			/* graphics.setColor(Color.GRAY);
			for(int i = 0; i < Constant.FRAME_WIDTH; i += Constant.FRAME_WIDTH / Constant.PIC_WIDTH) {
				graphics.drawLine(i, 0, i, Constant.FRAME_HEIGHT);
			}
			
			for(int i = 0; i < Constant.FRAME_HEIGHT; i += Constant.FRAME_HEIGHT / Constant.PIC_HEIGHT) {
				graphics.drawLine(0, i, Constant.FRAME_WIDTH, i);
			} */
		
			//Affichage des textes par dessus la grille
			graphics.setColor(Color.BLACK);
			graphics.setFont(font);
			Rectangle2D recTime = font.getStringBounds(time, graphics.getFontRenderContext());
			Rectangle2D recNbStudents = font.getStringBounds(nbStudents, graphics.getFontRenderContext());
			graphics.drawString(time, (int)(info.draw.width - recTime.getWidth() - 10), (int)recTime.getHeight());
			graphics.drawString(nbStudents, (int)(info.draw.width - recNbStudents.getWidth() - 10), 2 * (int)recNbStudents.getHeight());
		}
	}
}
