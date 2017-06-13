package View;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import Agent.Student;
import Model.Pic;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;

/**
 * Cette classe vise à améliorer la repréentation graphique des étudiants dans 
 * une case de la grille lorsque plusieurs étudiants sont dans cette même case.
 * 
 * En fonction du nombre d'étudiants présents sur la case, les représentations
 * individuelles s'adaptent en taille et en position pour qu'on puisse voir
 * tous les étudiants, en équilibrant les lignes et les colonnes.
 * 
 * Les étudiants sont ainsi tous affichés et ne se superposent pas.
 * 
 * Il est aussi envisageable de modifier la couleur en fonction de l'alcoolisation, etc.
 */
public class StudentPortrayal extends OvalPortrayal2D {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Modèle de la simulation
	 */
	Pic pic;
	
	public StudentPortrayal(SimState state) {
		super();
		
		//Paramétrages, couleur et remplissage
		paint = Color.GREEN;
        filled = true;
        
		this.pic = (Pic) state;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		if(object instanceof Student) {
			Student student = (Student) object;
			int index = -1;
			
			//Étudiants sur la case courante
			List<Student> students = pic.getEntitiesAtLocation(pic.getModel().getObjectLocation(student), Student.class);
			
			//Récupération de l'index de l'étudiant courant dans ces objets
			for(int i = 0; i < students.size(); ++i) {
				if(student == students.get(i)) index = i;
			}
			
			Rectangle2D.Double draw = info.draw;
			
			/* Le facteur de répartition paramètre la division de la cellule en grille. Il
			 * indique simplement le nombre d'éléments par ligne de la grille. Ce nombre
			 * change évidemment en fonction du nombre d'étudiants sur la cellule.
			 * Il est calculé pour être le plus petit possible en respectant la contrainte 
			 * de dessiner des cercles (et non des ovales) de même taille, équitablement répartis. */
			int gridRepartitionFactor = (int) Math.ceil(Math.sqrt(students.size()));
			
			//Largeur de l'élément s'il était seul
			double theoricalWidth = draw.width;
			
			//Largeur (et hauteur) de l'élément en fonction du nombre d'éléments sur une ligne de la grille
			double effectiveWidth = theoricalWidth / gridRepartitionFactor;
			
			//Position en abscisses si l'élément était seul
			double theoricalX = draw.x;
			
			//Décalage en abscisses (le modulo permet de remplir en priorité les lignes plutôt que les colonnes de la grille)
			double xOffset = effectiveWidth * (index % gridRepartitionFactor);
			
			//Idem que pour les abscisses, potentiellement la dernière ligne sera incomplète
			double theoricalY = draw.y;
			double yOffset = effectiveWidth * (index / gridRepartitionFactor);
			
			//Calcul des coordonnées finales des éléments : ils ne s'overlapent plus
			final int x = (int)(theoricalX + xOffset - theoricalWidth / 2.0);
	        final int y = (int)(theoricalY + yOffset - theoricalWidth / 2.0);

	        //Dessin effectif
	        graphics.setPaint(paint);
            graphics.fillOval(x, y, (int)effectiveWidth, (int)effectiveWidth);
            //L'étudiant a une bière
            if(!student.getCup().isEmpty()) {
            	int secondaryWidth = (int)(effectiveWidth / 3);
            	graphics.setColor(Color.orange);
            	graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
            }            
		}
	}
}
