package Agent;

import java.util.ArrayList;
import java.util.List;
import Model.Pic;
import Own.Student.BankAccount;
import Own.Student.Drink;
import Own.Student.PayUTCAccount;
import State.StudentState;
import Util.Beer;
import Util.Constant;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

import static State.StudentState.*;

/**
 * Agent dynamique représentant un étudiant (pas nécessairement au sein du Pic).
 */
public class Student implements Steppable {
	private static final long serialVersionUID = 1L;
	/**
	 * État courant de l'étudiant
	 */
	private StudentState studentState;
	
	/**
	 * Indique si l'étudiant est précédemment entré dans le Pic.
	 */
	private boolean hasBeenInside;

	/**
	 * Compte PayUTC de l'étudiant
	 */
	private PayUTCAccount payUTC;
	
	/**
	 * Compte banquaire de l'étudiant
	 */
	private BankAccount bankAccount;
	
	/**
	 * Verre de l'étudiant
	 */
	private Drink cup;

	/**
	 * Distance maximale de déplacement par itération
	 */
	private int walkCapacity;
	
	/**
	 * Chemin que doit suivre l'étudiant pour rejoindre
	 * sa destination. Vide si l'étudiant n'a pas de destination.
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
		cup = new Drink();
		payUTC = new PayUTCAccount();
		bankAccount = new BankAccount();
		//Par défaut, l'étudiant est dehors
		studentState = OUTSIDE;
		path = new ArrayList<>();
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
	            	setNewWalkTarget(pic);
	        	}
	        	break;
	        //L'étudiant ne fait rien, il prend une décision
	        case NOTHING:
	        	if(mustLeavePic()) {
	        		//TODO envoyer l'étudiant vers une sortie avant de le supprimer (état, booléen ?)
	        		pic.getModel().remove(this);
	            	pic.decStudentsInside();
	            	studentState = OUTSIDE;
	        	}
	        	else if(mustWalk()) advancePath(pic);
	        	break;
	        //L'étudiant marchait, il continue sa marche
	        case WALKING:
	        	advancePath(pic);
	        	break;
			default:
				break;
        }
    }
    
	/**
	 * Renvoie le type de bière que l'étudiant veut
	 * @return type de bière
	 */
	public Beer getOrder() {
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
	public void serve() {
		studentState = WAITING_FOR_BEER;
	}

	/**
	 * Fin du service
	 */
	public void endServe() {
		studentState = NOTHING;
	}
	
	/**
	 * Préviens l'étudiant qu'il n'a pas assez d'argent sur son compte
	 */
	public void notEnoughMoney() {
		studentState = POOR;
		//TODO L'étudiant doit recharger si il veut boire
		//TODO Certains ne voudront pas et d'autres rechargeront probablement ?
		//TODO Puis il doit refaire la queue (si il a rechargé)
	}
	
    public PayUTCAccount getPayUTC() {
		return payUTC;
	}

	public Drink getCup() {
		return cup;
	}

	/**
     * Génère une position vers laquelle l'étudiant se déplacera ultérieurement et met à jour le chemin.
     * @param pic État de la simulation
     */
    private void setNewWalkTarget(Pic pic)  {	
		//Position courante
    	Int2D currentPos = pic.getModel().getObjectLocation(this);
    	
    	//Sélection d'une position aléatoire
    	//TODO gérer les points d'intérêt du pic
    	Int2D selectedPos = pic.getRandomValidLocation();
    	
    	//Mise à jour du chemin à suivre
    	path = pic.getPath(currentPos, selectedPos);
    	studentState = WALKING;
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
	 * Permet à l'étudiant de recharger son compte
	 * @param money quantité ajoutée au compte
	 */
	private void rechargePayutc(float money) {
		//TODO gérer la recharge
	}
}