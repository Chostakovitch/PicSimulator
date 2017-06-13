package Agent;

import static State.StudentState.NOTHING;
import static State.StudentState.OUTSIDE;
import static State.StudentState.POOR;
import static State.StudentState.WAITING_FOR_BEER;
import static State.StudentState.WAITING_IN_QUEUE;
import static State.StudentState.WALKING;
import static State.StudentState.WALKING_TO_WAITING_LINE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.Pic;
import Own.Bartender.Order;
import Own.Person.BankAccount;
import Own.Person.PayUTCAccount;
import Own.Student.Drink;
import State.StudentState;
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
	 * Argent disponible, au cas où, sur le compte banquaire de l'étudiant.
	 * Le compte PayUTC est à 0 et s'alimente sur le compte banquaire.
	 */
	private int moneyCapacity;
	
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
		//Par défaut, l'étudiant est dehors
		studentState = OUTSIDE;
		path = new ArrayList<>();
		//TODO trouver une valeur par défaut
		moneyCapacity = 100;
		bankAccount = new BankAccount(moneyCapacity);
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
	        	//Choisit une file d'attente et initie un déplacement
	        	else if(mustGetBeer()) chooseWaitingLine(pic);
	        	//Choisit une destination aléatoire
	        	else if(mustWalk()) setNewWalkTarget(pic, pic.getRandomValidLocation());
	        	break;
	        //L'étudiant marchait, il continue sa marche
	        case WALKING: case WALKING_TO_WAITING_LINE:
	        	advancePath(pic);
	        	break;
	        //L'étudiant est pauvre. Mince alors. Il doit décider s'il recharge et continue de manger des pâtes ou s'il reste à l'eau.
	        case POOR:
	        	//TODO discuter de cette constante arbitraire
	        	if(mustRecharge(10)) {
	        		rechargePayutc(10);
	        		chooseWaitingLine(pic);
	        	}
	        	else {
	        		studentState = NOTHING;
	        	}
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
	}
	
    public PayUTCAccount getPayUTC() {
		return payUTC;
	}

	public Drink getCup() {
		return cup;
	}

	/**
     * Génère un chemin pour le déplacement de l'étudiant.
     * @param pic État de la simulation
     * @param finalPos Destination de l'étudiant.
     */
    private void setNewWalkTarget(Pic pic, Int2D finalPos)  {
		//Position courante
    	Int2D currentPos = pic.getModel().getObjectLocation(this);
    	//Mise à jour du chemin à suivre
    	path = pic.getPath(currentPos, finalPos);
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
    	if(path.isEmpty()) {
    		//S'il allait vers une file d'attente, il y rentre
    		if(studentState == WALKING_TO_WAITING_LINE) {
    			enterWaitingFile(pic);
    		}
    		//Sinon, il retourne à son état initial
    		else {
    			studentState = NOTHING;
    		}
    	}
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
    	return cup.isEmpty() && Math.random() < 0.2;    	
    }
    
    /**
     * Indique si l'étudiant doit aller chercher une bière
     * @return true si l'étudiant doit aller chercher une bière
     */
    private boolean mustGetBeer() {
    	if(!cup.isEmpty()) return false;
    	return true;
    }
    
    /**
     * Indique si l'étudiant doit recharger sa carte PayUTC
     * @return true si l'étudiant doit recharger
     */
    private boolean mustRecharge(double amount) {
    	//TODO implémenter les conditions
    	return bankAccount.hasEnough(amount);
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
	private void rechargePayutc(double money) {
		payUTC.transfer(bankAccount, money);
	}
	
	/**
	 * Choisit une file d'attente sur laquelle s'insérer. La file choisie est 
	 * celle contenant le moins d'étudiant au moment d'y entrer. Initie le déplacement.
	 * @param pic Modèle de la simulation.
	 */
	private void chooseWaitingLine(Pic pic) {		
		WaitingLine line = Arrays
			.stream(Constant.WAITING_LINES_POSITIONS)
			.map(pos -> pic.getEntitiesAtLocation(pos, WaitingLine.class))
			.flatMap(List::stream)
			.min((l, r) -> Integer.compare(l.getStudentNumber(), r.getStudentNumber()))
			.get();
		setNewWalkTarget(pic, pic.getModel().getObjectLocation(line));
		studentState = WALKING_TO_WAITING_LINE;
	}

	/**
	 * L'étudiant rentre dans une file d'attente sur sa position actuelle
	 */
	private void enterWaitingFile(Pic pic) throws IllegalStateException {
		//On récupère la file d'attente
		List<WaitingLine> lines = pic.getEntitiesAtLocation(pic.getModel().getObjectLocation(this), WaitingLine.class);
		if(lines.isEmpty()) throw new IllegalStateException("Aucune file d'attente sur la position courante!");
		
		//Si cette file existe, on entre dedans et on modifie l'état de l'étudiant
		lines.get(0).enterLine(new Order(this, getOrder()));
		studentState = WAITING_IN_QUEUE;
	}
}