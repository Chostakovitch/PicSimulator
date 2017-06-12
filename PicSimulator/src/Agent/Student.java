package Agent;

import java.util.List;

import Model.Pic;
import Util.Beer;
import Util.Constant;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

/**
 * Agent dynamique représentant un étudiant (pas nécessairement au sein du Pic).
 */
public class Student implements Steppable {
	private static final long serialVersionUID = 1L;
	
	//Constantes de tous les états possibles de l'étudiant
	/**
	 * L'étudiant ne fait rien de spécial, il doit prendre une décision
	 */
	private static final int NOTHING = 0;
	/**
	 * L'étudiant est dans la file d'attente pour se faire servir une bière
	 */
	private static final int WAITING_IN_QUEUE = 1;
	/**
	 * L'étudiant est en train d'être servi
	 */
	private static final int WAITING_FOR_BEER = 2;
	/**
	 * L'étudiant est en train de se déplacer
	 */
	private static final int WALKING = 3;
	/**
	 * L'étudiant est pauvre et n'a pas assez d'argent pour une bière
	 */
	private static final int POOR = 4;
	/**
	 * L'étudiant n'est pas dans le Pic
	 */
	private static final int OUTSIDE = 5;
	
	/**
	 * État courant de l'étudiant
	 */
	private int studentState;
	
	/**
	 * Indique si l'étudiant est précédemment entré dans le Pic.
	 */
	private boolean hasBeenInside;

	/**
	 * Quantité de bière dans le verre, se vide au fur et à mesure et se remplit au bar
	 */
	private float quantityBeer;

	/**
	 * Balance de l'étudiant
	 */
	private float payutc;

	/**
	 * Distance maximale de déplacementl
	 */
	private int walkCapacity;
	
	/**
	 * Chemin que doit suivre l'étudiant pour rejoindre
	 * sa destination. Vaut null si l'étudiant n'a pas de destination.
	 */
	private List<Int2D> path;

    public Student() {
    	this(0);
    }

	/**
	 * //TODO Utiliser cette méthode quand on aura les chiffres
	 * @param money Argent au début de la simulation
	 */
	public Student(float money) {
		hasBeenInside = false;
		walkCapacity = Constant.STUDENT_WALK_CAPACITY;
		quantityBeer = 0f;
		payutc = money;
		//Par défaut, l'étudiant est dehors
		studentState = OUTSIDE;
		path = null;
	}

    @Override
    public void step(SimState state) {
        Pic pic = (Pic) state;
        //Décision en fonction de l'état de l'étudiant
        switch(studentState) {
	        //L'étudiant attend pour une bière, il n'a rien à faire
	        case WAITING_FOR_BEER: case WAITING_IN_QUEUE:
	        	return;
	        //L'étudiant est en dehors du Pic, il prend une décision
	        case OUTSIDE:
	        	//L'étudiant arrive à l'entrée du Pic, il bouge immédiatement sur une position valide
	        	if(mustEnterPic()) {
	        		pic.getModel().setObjectLocation(this, Constant.PIC_ENTER);
	            	pic.incrStudentsInside();
	            	studentState = NOTHING;
	            	hasBeenInside = true;
	            	justMoveIt(pic);
	        	}
	        	break;
	        //L'étudiant ne fait rien, il prend une décision
	        case NOTHING:
	        	if(mustLeavePic()) {
	        		pic.getModel().remove(this);
	            	pic.decStudentsInside();
	            	studentState = OUTSIDE;
	        	}
	        	else if(mustWalk()) justMoveIt(pic);
	        	break;
	        //L'étudiant marchait, il continue sa marche
	        case WALKING:
	        	advancePath(pic);
	        	break;
        }
    }
    
    /**
     * Déplace l'étudiant.
     * - S'il n'était pas en cours de déplacement, détermine une position valide et initie le déplacement.
     * - S'il était en cours de déplacement, consomme la partie du chemin nécessaire.
     * @param pic État de la simulation
     */
    private void justMoveIt(Pic pic)  {
    	//Pas de déplacement en cours, on en génère un
    	if(studentState != WALKING) {	
    		//Position courante
        	Int2D currentPos = pic.getModel().getObjectLocation(this);
        	
	    	//Sélection d'une position aléatoire
	    	Int2D selectedPos = pic.getRandomValidLocation();
	    	
	    	//Mise à jour du chemin à suivre
	    	path = pic.getPath(currentPos, selectedPos);
    	}
    	
    	advancePath(pic);
    }
    
    /**
     * Avance sur le chemin pré-déterminé par l'étudiant
     * et assigne l'état de l'étudiant en fonction de l'état du chemin. 
     * Si le déplacement est fini, positionne l'état de l'étudiant à NOTHING.
     * @param pic État de la simulation
     */
    private void advancePath(Pic pic) {
    	//Position courante
    	Int2D currentPos = pic.getModel().getObjectLocation(this);
    	
    	int i = walkCapacity;
    	Int2D finalPos = currentPos;
    	
    	//Déplacement et mise à jour
    	while(path.size() > 0 && i > 0) {
    		finalPos = path.remove(0);
    		--i;
    	}
    	studentState = WALKING;
    	pic.getModel().setObjectLocation(this, finalPos);
    	
    	//L'étudiant est arrivé à sa destination
    	if(path.isEmpty()) studentState = NOTHING;
    }
    
    /**
     * Indique si l'étudiant doit entrer dans le Pic
     * @return Booléen
     * @throws IllegalStateException si l'étudiant est déjà dans le Pic
     */
    private boolean mustEnterPic() throws IllegalStateException {
    	if(studentState != OUTSIDE) throw new IllegalStateException("Student is already inside Pic");
    	double prob = Math.random();
    	if(hasBeenInside)
    		return prob < 0.1;
    	return prob < 0.6; 
    }
    
    /**
     * Indique si l'étudiant doit sortir du Pic
     * @return Booléen
     * @throws IllegalStateException si l'étudiant n'est pas dans le Pic
     */
    private boolean mustLeavePic() throws IllegalStateException {
    	if(studentState == OUTSIDE) throw new IllegalStateException("Student is not inside Pic");
    	return Math.random() < 0.2;    	
    }
    
    /**
     * Indique si l'étudiant doit effectuer un déplacement
     * @return Booléean
     * @throws IllegalStateException si l'étudiant n'est pas dans le Pic
     */
    private boolean mustWalk() throws IllegalStateException {
    	if(studentState == OUTSIDE) throw new IllegalStateException("Student is not inside Pic");
    	return Math.random() < 0.5;
    }

	/**
	 * Renvoie le type de bière que l'étudiant veut
	 * @return type de bière
	 */
	Beer getOrder() {
		//TODO Surement un attribut / une liste des bières qu'un étudiant veut
    	return Beer.BarbarBok;
	}

	/**
	 * L'étudiant rentre dans une file d'attente
	 */
	public void enterWaitingFile() {
		//TODO Utiliser la méthode Agent.WaitingLine.enterLine(this)
		studentState = WAITING_IN_QUEUE;
	}

	/**
	 * L'étudiant se fait servir
	 */
	void serve() {
		studentState = WAITING_FOR_BEER;
	}

	/**
	 * Fin du service
	 */
	void endServe(double cost) {
		studentState = NOTHING;
		payutc -= cost;
		quantityBeer = 33f;
	}

	/**
	 * Retourne le solde payutc actuel de l'étudiant
	 * @return solde payutc
	 */
	double getPayutc() {
		return payutc;
	}

	/**
	 * Préviens l'étudiant qu'il n'a pas assez d'argent sur son compte
	 */
	void notEnoughMoney() {
		studentState = POOR;
		//TODO L'étudiant doit recharger si il veut boire
		//TODO Certains ne voudront pas et d'autres rechargeront probablement ?
		//TODO Puis il doit refaire la queue (si il a rechargé)
	}

	/**
	 * Permet à l'étudiant de recharger son compte
	 * //TODO Probablement pas instantannée ?
	 * @param money quantité ajoutée au compte
	 */
	private void rechargePayutc(float money) {
		payutc += money;
	}
}