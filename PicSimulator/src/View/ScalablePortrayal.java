package View;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import Agent.Bartender;
import Model.Pic;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;

/**
 * Cette classe vise à améliorer la représentation graphique des entités dans 
 * une case de la grille lorsque plusieurs entités sont dans cette même case.
 * 
 * En fonction du nombre d'entités présents sur la case, les représentations
 * individuelles s'adaptent en taille et en position pour qu'on puisse voir
 * toutes les entités, en équilibrant les lignes et les colonnes.
 * 
 * Les entités sont ainsi toutes affichées et ne se superposent pas.
 * @param <T> Type de l'entité
 */
public abstract class ScalablePortrayal<T> extends OvalPortrayal2D {
private static final long serialVersionUID = 1L;
	
	/**
	 * Modèle de la simulation
	 */
	protected Pic pic;
	
	/**
	 * Classe de l'entité que l'on cherche à afficher
	 */
	protected Class<T> entityType;
	
	/**
	 * Largeur effective du cercle dessiné
	 */
	protected double effectiveWidth;
	
	/**
	 * Abscisse effective du cercle dessiné
	 */
	protected int x;
	
	/**
	 * Ordonnée effective du cercle dessiné
	 */
	protected int y;
	
	public ScalablePortrayal(SimState state) {
		super();
        filled = true;
        
		this.pic = (Pic) state;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		if(entityType.isInstance(object)) {
			T entity = entityType.cast(object);
			int index = -1;
			
			//Entités sur la case courante
			List<T> entities = pic.getEntitiesAtLocation(pic.getModel().getObjectLocation(entity), entityType);
			
			//Récupération de l'index de l'entité courante dans ces objets
			for(int i = 0; i < entities.size(); ++i) {
				if(entity == entities.get(i)) index = i;
			}
			
			Rectangle2D.Double draw = info.draw;
			
			/* Le facteur de répartition paramètre la division de la cellule en grille. Il
			 * indique simplement le nombre d'éléments par ligne de la grille. Ce nombre
			 * change évidemment en fonction du nombre d'entités sur la cellule.
			 * Il est calculé pour être le plus petit possible en respectant la contrainte 
			 * de dessiner des cercles (et non des ovales) de même taille, équitablement répartis. */
			int gridRepartitionFactor = (int) Math.ceil(Math.sqrt(entities.size()));
			
			//Largeur de l'élément s'il était seul
			double theoricalWidth = draw.width;
			
			//Largeur (et hauteur) de l'élément en fonction du nombre d'entités sur une ligne de la grille
			effectiveWidth = theoricalWidth / gridRepartitionFactor;
			
			//Position en abscisses si l'élément était seul
			double theoricalX = draw.x;
			
			//Décalage en abscisses (le modulo permet de remplir en priorité les lignes plutôt que les colonnes de la grille)
			double xOffset = effectiveWidth * (index % gridRepartitionFactor);
			
			//Idem que pour les abscisses, potentiellement la dernière ligne sera incomplète
			double theoricalY = draw.y;
			double yOffset = effectiveWidth * (index / gridRepartitionFactor);
			
			//Calcul des coordonnées finales des éléments : ils ne s'overlapent plus
			x = (int)(theoricalX + xOffset - theoricalWidth / 2.0);
	        y = (int)(theoricalY + yOffset - theoricalWidth / 2.0);

	        //Dessin effectif
	        graphics.setPaint(paint);
            graphics.fillOval(x, y, (int)effectiveWidth, (int)effectiveWidth);
		}
	}
}
