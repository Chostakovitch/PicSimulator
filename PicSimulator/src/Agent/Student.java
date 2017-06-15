package Agent;

import static State.StudentState.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import Enum.Beer;
import Enum.Gender;
import Enum.TypeSemestre;
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
	
	/**
	 * L'étudiant est vraiment pauvre et ne peut plus recharger
	 */
	private boolean veryPoor;
	
	/**
	 * Modèle de la simulation
	 */
	private Pic pic;

    /**
     * Genre de l'étudiant (FEMALE, MALE)
     */
	private Gender gender;

    /**
     * Age de l'étudiant (entre 15 et 90 ans)
     */
    private Integer age;

    /**
     * Type de semestre (Tronc commun, branche, master etc)
     */
    private TypeSemestre type;

    /**
     * Type et numéro de semestre (exemple: GI04) -> Non pertinant par exemple pour les professeurs
     */
    private String semester;

    /**
     * Liste de préférences des bières : Contiens la valeur de l'énumération de la bière correspondante
     * ainsi qu'une notre associé (entre -5 et 5)
     */
    private HashMap<Beer, Integer> beersGrade;

    /**
     * Nombre de bière que l'étudiant estime sont maximum
     */
    private Integer beerMax;

    /**
     * Nombre de minutes que l'étudiant estime nécessaire pour boire une bière
     */
    private Integer drinkingTime;

    /**
     * Heure à laquelle l'étudiant estime arriver au Pic en moyenne
     */
    private LocalTime arrivalTime;

    /**
     * Heure à laquelle l'étudiant estime partir au Pic en moyenne
     */
    private LocalTime departureTime;

    /**
     * Valeur représentant ce que l'étudiant à mangé
     */
    private MealState mealState;

    /**
     * Sensibilité de l'étudiant à l'alcool (noté entre 1 et 5)
     */
    private Integer alcoholSensitivityGrade;

    public Student() {
    	this(0);
    }

    /**
     *
     * @param money Argent au début de la simulation
     */
    public Student(int money) {
        hasBeenInside = false;
        walkCapacity = Constant.STUDENT_WALK_CAPACITY;
        cup = new Drink();
        payUTC = new PayUTCAccount();
        //Par défaut, l'étudiant est dehors
        studentState = OUTSIDE;
        path = new ArrayList<>();
        //TODO affiner la valeur
        moneyCapacity = Constant.BANK_INITIAL_BALANCE;
        bankAccount = new BankAccount(moneyCapacity);
        veryPoor = false;
    }

	public Student(String[] dataLine) {
		hasBeenInside = false;
		walkCapacity = Constant.STUDENT_WALK_CAPACITY;
		cup = new Drink();
		payUTC = new PayUTCAccount();
		//Par défaut, l'étudiant est dehors
		studentState = OUTSIDE;
		path = new ArrayList<>();
		veryPoor = false;

        gender = dataLine[0].equals("F") ? Gender.FEMALE : Gender.MALE;
        age = Integer.parseInt(dataLine[1]);
        switch (dataLine[2]) {
            case "tronc commun": type = TypeSemestre.TRONC_COMMUN; break;
            case "branche": type = TypeSemestre.BRANCHE; break;
            case "doctorant": type = TypeSemestre.DOCTORANT; break;
            case "escom": type = TypeSemestre.ESCOM; break;
            case "hutech": type = TypeSemestre.HUTECH; break;
            case "professeur-chercheur": type = TypeSemestre.PROFESSEUR_CHERCHEUR; break;
            case "diplomé": type = TypeSemestre.DIPLOME; break;
            case "double diplome": type = TypeSemestre.DOUBLE_DIPLOME; break;
        }
        semester = dataLine[3];

        beersGrade = new HashMap<>();
        for (int i = 4; i <= 13; i++) {
            String[] beer = dataLine[i].split(":");
            Beer beerEnum = Beer.getCorrespondantEnum(beer[0]);
            if (beerEnum != null) beersGrade.put(beerEnum, Integer.parseInt(beer[1]));
        }

        beerMax = Integer.parseInt(dataLine[14]);
        drinkingTime = Integer.parseInt(dataLine[15]);

        String[] time = dataLine[16].split(":");
        arrivalTime = LocalTime.of(Integer.parseInt(time[0]), Integer.parseInt(time[1]));
        time = dataLine[17].split(":");
        departureTime = LocalTime.of(Integer.parseInt(time[0]), Integer.parseInt(time[1]));

        //TODO réfléchir à une proba de mettre plus ou moins
        bankAccount = new BankAccount(Integer.parseInt(dataLine[18]));
        switch (dataLine[19]) {
            case "repas": mealState= MealState.REPAS ; break;
            case "menu": mealState= MealState.MENU; break;
            case "snack": mealState= MealState.SNACK; break;
            default: mealState= MealState.NO_MEAL;
        }
        alcoholSensitivityGrade = Integer.parseInt(dataLine[21]);
    }

    @Override
    public void step(SimState state) {
        Pic pic = (Pic) state;
        //Quoiqu'il arrive, si l'étudiant a une bière, il peut la consommer avant de décider de son action
        if(!cup.isEmpty() && mustDrinkBeer()) {
        	cup.drink(Constant.STUDENT_SWALLOW_CAPACITY);
        }
        
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
	        		setNewWalkTarget(pic, Constant.EXIT_POSITION);
	        		studentState = WALKING_TO_EXIT;
	        	}
	        	//Choisit une file d'attente et initie un déplacement
	        	else if(mustGetBeer()) chooseWaitingLine();
	        	//Choisit une destination aléatoire
	        	else if(mustWalk()) setNewWalkTarget(pic, pic.getRandomValidLocation());
	        	break;
	        //L'étudiant marchait, il continue sa marche
	        case WALKING: case WALKING_TO_WAITING_LINE: case WALKING_TO_EXIT:
	        	advancePath();
	        	break;
	        //L'étudiant est pauvre. Mince alors. Il doit décider s'il recharge et continue de manger des pâtes ou s'il reste à l'eau.
	        case POOR:
	        	if(mustRecharge(Constant.RECHARGE_AMOUNT)) {
	        		rechargePayutc(Constant.RECHARGE_AMOUNT);
	        		chooseWaitingLine();
	        	}
	        	else {
	        		veryPoor = true;
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
    private Beer getOrder() {
		//TODO Surement un attribut / une liste des bières qu'un étudiant veut
    	return choiceOrder();
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
	void endServe() {
		studentState = NOTHING;
	}
	
	/**
	 * Préviens l'étudiant qu'il n'a pas assez d'argent sur son compte
	 */
	void notEnoughMoney() {
		studentState = POOR;
	}
	
    PayUTCAccount getPayUTC() {
		return payUTC;
	}

	public Drink getCup() {
		return cup;
	}

	public StudentState getStudentState() {
		return studentState;
	}
	
	public boolean isVeryPoor() {
		return veryPoor;
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
    private void advancePath() {
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
    			enterWaitingFile();
    		}
    		//L'étudiant est en train de sortir, on le supprime graphiquement
    		else if(studentState == WALKING_TO_EXIT) {
        		pic.getModel().remove(this);
            	pic.decStudentsInside();
            	studentState = OUTSIDE;
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
        return !(!cup.isEmpty() || veryPoor);
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
     * Indique si l'étudiant doit boire une gorgée de bière
     * @return Booléeen
     */
    private boolean mustDrinkBeer() {
        return !cup.isEmpty();
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
	private void chooseWaitingLine() {		
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
	private void enterWaitingFile() throws IllegalStateException {
		//On récupère la file d'attente
		List<WaitingLine> lines = pic.getEntitiesAtLocation(pic.getModel().getObjectLocation(this), WaitingLine.class);
		if(lines.isEmpty()) throw new IllegalStateException("Aucune file d'attente sur la position courante!");
		
		//Si cette file existe, on entre dedans et on modifie l'état de l'étudiant
		lines.get(0).enterLine(new Order(this, getOrder()));
		studentState = WAITING_IN_QUEUE;
	}

    /**
     * Permet de retourner une bière, avec un plus grand % selon les préférences
     * @return choix de la bière
     */
	private Beer choiceOrder() {
		//Choix possibles
		int[] beerGrades = new int[] { Constant.LOVE_GRADE, Constant.NOT_BAD_GRADE, Constant.NEVER_TASTE_GRADE, Constant.AVERAGE_GRADE, Constant.HATE_GRADE };
		
		//Poids associés aux choix
		double[] probs = new double[] { 85, 10, 4, 1, 1 };
		
		Beer beer = null;
		//Implémentation approximative d'un Reservoir Sample
		while(beer == null) {
			int selected = beerGrades[0];
			double total = probs[0];
			for(int i = 1; i < probs.length; ++i) {
				total += probs[i];
				if(pic.random.nextDouble() <= (probs[i] / total)) selected = i;
			}
            beer = getRandomGradeBeer(beerGrades[selected]);
		}
        return beer;
    }

    private Beer getRandomGradeBeer(int grade) {
        return beersGrade.entrySet()
            .stream()
            .filter(beerIntegerEntry -> beerIntegerEntry.getValue() == grade)
            .map(beerEntry -> beerEntry.getKey())
            .findAny()
            .orElse(null);
    }
}
