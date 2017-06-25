package View;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import Agent.Wall;
import Enum.Direction;
import Model.Pic;
import State.StudentState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.SimplePortrayal2D;

/**
 * Cette classe vise à améliorer la représentation graphique des entités dans 
 * une case de la grille lorsque plusieurs entités sont dans cette même case.
 * 
 * En fonction du nombre d'entités présents sur la case, les représentations
 * individuelles s'adaptent en taille et en position pour qu'on puisse voir
 * toutes les entités, en équilibrant les lignes et les colonnes.
 * 
 * Les entités sont ainsi toutes affichées et ne se superposent pas.
 * 
 * Par défaut, cette classe affiche des images.
 * @param <T> Type de l'entité
 */
public abstract class ScalablePortrayal<T> extends SimplePortrayal2D {
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
	
	/**
	 * Sauvegarde de l'objet permettant de dessiner
	 */
	Graphics2D graphics;
	
	/**
	 * Image à appliquer
	 */
	protected Image background;
	
	/**
	 * Indiquer si la case doit être dessinée ou délayée à l'utilisateur
	 */
	protected boolean directDraw;
	
	/**
	 * Indique si la logique de scaling doit être appliquée
	 */
	protected boolean scale;
	
	private static final String basePath = "resources/img/";
	
	public ScalablePortrayal(SimState state) {
		this(state, true, true);
	}
	
	public ScalablePortrayal(SimState state, boolean directDraw, boolean scale) {
		super();
		this.directDraw = directDraw;
		this.scale = scale;
		this.pic = (Pic) state;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		this.graphics = graphics;
		if(entityType.isInstance(object)) {
			
			Rectangle2D.Double draw = info.draw;
			effectiveWidth = draw.width; 
			x = (int)draw.x;
			y = (int)draw.y;
			
			if(scale) {
				T entity = entityType.cast(object);
				int index = -1;
				
				//Entités sur la case courante
				List<T> entities = pic.getEntitiesAtLocation(pic.getModel().getObjectLocation(entity), entityType);
				
				//Récupération de l'index de l'entité courante dans ces objets
				for(int i = 0; i < entities.size(); ++i) {
					if(entity == entities.get(i)) index = i;
				}
				
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
			}
			
	        //Dessin effectif
	        if(directDraw)
	        	graphics.drawImage(background, x, y, (int)effectiveWidth, (int)effectiveWidth, null);
		}
	}
	
	/**
	 * Setter pour l'image de l'entité
	 * @param imagePath Chemin vers l'image
	 */
	protected void setBackground(String imagePath) {
		try {
			File fileImage = new File(basePath + imagePath);
			background = ImageIO.read(fileImage);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Dessine l'entité effectivement
	 */
	protected void drawEffectivly() {
		graphics.drawImage(background, x, y, (int)effectiveWidth, (int)effectiveWidth, null);
	}
	
	/**
	 * Obtient un suffixe standardisé d'image en fonction de la direction
	 * @param dir Direction de l'entité
	 * @return Suffixe
	 */
	protected String getDirectionSuffix(Direction dir) {
		switch(dir) {
			case BOTTOM: return "_bottom";
			case TOP: return "_top";
			case LEFT: return "_left";
			case RIGHT: return "_right";
			default: return "_bottom";
		}
	}
}
