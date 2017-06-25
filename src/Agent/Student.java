package Agent;

import static State.StudentState.CHOOSING_WAITING_LINE;
import static State.StudentState.DRINKING_WITH_FRIENDS;
import static State.StudentState.NOTHING;
import static State.StudentState.OUTSIDE;
import static State.StudentState.POOR;
import static State.StudentState.WAITING_FOR_BEER;
import static State.StudentState.WAITING_IN_QUEUE;
import static State.StudentState.WALKING;
import static State.StudentState.WALKING_TO_EXIT;
import static State.StudentState.WALKING_TO_WAITING_LINE;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Enum.Beer;
import Enum.Direction;
import Enum.Gender;
import Enum.MealState;
import Enum.TypeSemestre;
import Model.Pic;
import Own.Bartender.Order;
import Own.Person.BankAccount;
import Own.Person.PayUTCAccount;
import Own.Student.Drink;
import State.StudentState;
import Util.Constant;
import Util.DateTranslator;
import Util.Probability;
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
     * Nombre de bière que l'étudiant a bu
     */
    private Integer beerDrunk;

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
    
    /**
	 * Jours favoris de l'étudiant pour aller boire
	 */
	private String[] preferedDays;
	
	/**
	 * Booléen indiquant si l'étudiant restera plus tard que prévu 
	 */
	private boolean willStayLater;
	
	/**
	 * Taux d'alcoolémie de l'étudiant
	 */
	private double alcoholLevel;
	
	/**
	 * Direction de l'étudiant
	 */
	private Direction direction;

	public Student(String[] dataLine, Pic pic) {
		this.pic = pic;
		hasBeenInside = false;
		walkCapacity = Constant.STUDENT_WALK_CAPACITY;
		cup = new Drink();
		//Par défaut, l'étudiant est dehors
		beerDrunk = 0;
		studentState = OUTSIDE;
		path = new ArrayList<>();
		veryPoor = false;
		willStayLater = pic.random.nextDouble() < Probability.STUDENT_STAY_LATER;
		direction = Direction.BOTTOM;

        gender = dataLine[0].equals("\"F") ? Gender.FEMALE : Gender.MALE;
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
        
        //Gestion des dates pathologiques
        if(!pic.isTimeWithin(Constant.PIC_BEGIN, Constant.PIC_END, arrivalTime)) 
        	arrivalTime = Constant.PIC_BEGIN;
        
        if(!pic.isTimeWithin(Constant.PIC_BEGIN, Constant.PIC_END, departureTime)) 
        	departureTime = Constant.PIC_END;
        
        int budget = Integer.parseInt(dataLine[18]);
        double onBankAccount = pic.random.nextDouble();
        double onPayUTAccount = 1 - onBankAccount;
        bankAccount = new BankAccount(budget * onBankAccount);
		payUTC = new PayUTCAccount(budget * onPayUTAccount);
        switch (dataLine[19]) {
	        case "repas": mealState= MealState.REPAS ; break;
	        case "menu": mealState= MealState.MENU; break;
	        case "snack": mealState= MealState.SNACK; break;
	        default: mealState= MealState.NO_MEAL;
	    }
        alcoholLevel = 0;
        alcoholSensitivityGrade = Integer.parseInt(dataLine[21]);
        preferedDays = DateTranslator.translateArray(dataLine[20].split(","));
    }

    @Override
    public void step(SimState state) {
        pic = (Pic) state;
        //À chaque tour, son taux d'alcoolémie diminue
		decreaseAlcoholLevel();
        //Quoiqu'il arrive, si l'étudiant a une bière, il peut la consommer avant de décider de son action
        if(!cup.isEmpty() && mustDrinkBeer()) {
        	swallowBeer();
        	if(cup.isEmpty()) {
        		//Il va pas forcément abandonner ses potes parce qu'il a plus de bière quand même...
        		if(studentState != DRINKING_WITH_FRIENDS) studentState = NOTHING;
        	}
        }
        
		if(isDrunk() && !cup.isEmpty() && (studentState == DRINKING_WITH_FRIENDS || studentState == NOTHING || studentState == WALKING))
			spillBeer();
		
		Int2D currentPos = pic.getModel().getObjectLocation(this);
		
        //Décision en fonction de l'état de l'étudiant
        switch(studentState) {
	        //L'étudiant attend pour une bière, il n'a rien à faire
	        case WAITING_FOR_BEER: case WAITING_IN_QUEUE:
	        	return;
	        //L'étudiant est en dehors du Pic, il prend une décision
	        case OUTSIDE:
	        	//L'étudiant arrive à l'entrée du Pic, il bouge immédiatement sur une position valide
	        	if(mustEnterPic()) {
	        		if(!pic.isLocationFull(Constant.PIC_ENTER)) {
		        		pic.getModel().setObjectLocation(this, Constant.PIC_ENTER);
		        		direction = Direction.BOTTOM;
		            	pic.incrStudentsInside();
		            	studentState = NOTHING;
		            	hasBeenInside = true;
	        		}
	        	}
	        	break;
	        //L'étudiant ne fait rien ou parle avec des amis, il peut effectuer certaines actions
	        case NOTHING: case DRINKING_WITH_FRIENDS:
	        	//Si l'étudiant ne fait rien mais se trouve sur une case avec d'autres gens, il sociabilise + s'il n'est pas à l'entrée
	        	if(studentState == NOTHING 
	        		&& pic.getEntitiesAtLocation(currentPos, Student.class).size() > 1
	        		&& !currentPos.equals(Constant.PIC_ENTER))
					studentState = StudentState.DRINKING_WITH_FRIENDS;
	        	
	        	//Ne reste pas à l'entrée
	        	if(currentPos.equals(Constant.PIC_ENTER))
	        		setNewWalkTarget(getPositionToWalkTo(), WALKING);
	        	
	        	//Ne reste pas sur une file d'attente
	        	boolean lineFound = false;
	        	List<WaitingLine> lines = pic.getEntitiesAtLocation(currentPos, WaitingLine.class);
	        	for(WaitingLine line : lines) {
	        		if(pic.getModel().getObjectLocation(line).equals(currentPos)) {
	        			setNewWalkTarget(getPositionToWalkTo(), WALKING);
	        			lineFound = true;
	        			break;
	        		}
	        	}
	        	
	        	if(!lineFound) {
		        	if(mustLeavePic()) setNewWalkTarget(Constant.EXIT_POSITION, WALKING_TO_EXIT);
		        	//Choisit une file d'attente et initie un déplacement
		        	else if(mustGetBeer()) {
		        		WaitingLine line = chooseRandomWaitingLine();
		        		setNewWalkTarget(pic.getModel().getObjectLocation(line), CHOOSING_WAITING_LINE);
		        	}
		        	//Choisit une destination aléatoire
		        	else if(mustWalk()) setNewWalkTarget(getPositionToWalkTo(), WALKING);
		        	
		        	//Gestion du changement physique de direction
		        	else {
		        		double random = pic.random.nextDouble();
		        		if(random < Probability.STUDENT_CHANGE_DIRECTION / Probability.STUDENT_DIRECTION_SATURATION) setRandomDirection();
		        	}
	        	}
	        	break;
	        //L'étudiant marchait, il continue sa marche
	        case WALKING: case WALKING_TO_WAITING_LINE: case WALKING_TO_EXIT: case CHOOSING_WAITING_LINE:
	        	//Si l'étudiant a fini de marcher
	        	if(advancePath()) {
	        		setRandomDirection();
	        		switch(studentState) {
	        		//L'étudiant est arrivé aux files, il choisit la file minimale
	        		case CHOOSING_WAITING_LINE:
	        			WaitingLine line = chooseMinimalWaitingLine();
		        		setNewWalkTarget(pic.getModel().getObjectLocation(line), WALKING_TO_WAITING_LINE);
		        		break;
	        		//S'il allait vers une file d'attente minimale, il y rentre
	        		case WALKING_TO_WAITING_LINE:
	        			direction = Direction.TOP;
		    			enterWaitingLine();
		    			studentState = WAITING_IN_QUEUE;
		    			break;
		    		//L'étudiant est en train de sortir, on le supprime graphiquement
	        		case WALKING_TO_EXIT:
	        			//L'étudiant finit sa bière avant de sortir
	        			direction = Direction.TOP;
	        			while(!cup.isEmpty()) swallowBeer();
		        		pic.getModel().remove(this);
		            	pic.decStudentsInside();
		            	studentState = OUTSIDE;
		            	break;
		            //S'il était juste en train de marcher, il va discuter avec des potos
		    		default:
		    			if(pic.getEntitiesAtLocation(currentPos, Student.class).size() > 1)
		    				studentState = DRINKING_WITH_FRIENDS;
		    			else
		    				studentState = NOTHING;
		    			break;
	        		}
	        	}
	        	break;
	        //L'étudiant est pauvre. Mince alors. Il doit décider s'il recharge et continue de manger des pâtes ou s'il reste à l'eau.
	        case POOR:
	        	if(mustRecharge(Constant.RECHARGE_AMOUNT)) {
	        		rechargePayutc(Constant.RECHARGE_AMOUNT);
	        		WaitingLine line = chooseRandomWaitingLine();
	        		setNewWalkTarget(pic.getModel().getObjectLocation(line), CHOOSING_WAITING_LINE);
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
    public Beer getOrder() {
    	return choiceOrder();
	}

	/**
	 * L'étudiant se fait servir
	 */
	public void serve() {
		studentState = WAITING_FOR_BEER;
	}

	/**
	 * L'étudiant a récupéré sa bière, il s'en va
	 */
	public void endServe() {
		setNewWalkTarget(getPositionToWalkTo(), WALKING);
		//Si l'étudiant prend une bière de plus, on augmente son quota
		if(beerDrunk == beerMax) ++beerMax;
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

	public StudentState getStudentState() {
		return studentState;
	}
	
	public boolean isVeryPoor() {
		return veryPoor;
	}
	
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Détermine si l'étudiant est ivre ou non en fonction de sa sensibilité, de ce qu'il mangé et bu
	 * @return true s'il est ivre, false sinon
	 */
	public boolean isDrunk() {
		// Donne une équivalence en terme d'alcoolémie au delà de laquelle l'étudiant est ivre
		double alcoholSensitivityEquivalence = alcoholSensitivityGrade / (2 * Math.sqrt(3.0));
		return alcoholLevel >= (alcoholSensitivityEquivalence + mealAlcoholInfluence());
	}
	
	/**
	 * Calcule l'influence du repas de l'étudiant sur son taux d'alcoolémie
	 * @return dose d'alcool qu'il pourra supporter en plus
	 */
	private double mealAlcoholInfluence() {
		//Nombre de minutes depuis son arrivée
		long presentMinutes = Duration.between(arrivalTime, pic.getTime()).toMinutes();
		/* On considère que l'étudiant a fini de digérer en une heure à partir de son 
		arrivalTime (pas nécessairement égale à l'heure d'ouverture du pic) */
		if(presentMinutes > 60) return 0; 
		
		//Le sursis induit par la bouffe diminue au fur et à mesure de la digestion
		double mealAlcoholInfluence = mealState.getAlcoholLevelInfluence();
		return mealAlcoholInfluence - (mealAlcoholInfluence * presentMinutes / 60);  
	}
	
	/**
	 * Diminue le taux d'alcoolémie de l'étudiant
	 */
	private void decreaseAlcoholLevel() {
		double alcoholEliminateInASecond = Constant.STUDENT_ALCOHOL_ELIMINATION_PER_HOUR / 3600;
		if(alcoholLevel < 0.01) 
			alcoholLevel = 0;
		else alcoholLevel -= alcoholEliminateInASecond;
	}
	
	/**
	 * Détermine si l'étudiant renverse sa bière ou non
	 */
	public void spillBeer(){
		if(pic.random.nextDouble() < Probability.STUDENT_SPILL_HIS_BEER){
			cup.emptyCup();
			studentState = NOTHING;
		}
	}
	
	/**
	 * Met à jour la liste des préférences de l'étudiant et renvoie une bière valide 
	 * @param unavailableBeer Bières à enlever de la liste des préférences
	 * @return Bière choisie parmi la liste actualisée
	 */
    public Beer reorder(List<Beer> unavailableBeer) {
		HashMap<Beer, Integer> savedGrade = new HashMap<>(beersGrade);
		beersGrade.keySet().removeAll(unavailableBeer);
		Beer choice = choiceOrder();
		beersGrade = savedGrade;
		return choice;
	}

	/**
     * Génère un chemin pour le déplacement de l'étudiant.
     * @param finalPos Destination de l'étudiant.
     * @param stateAfter État de l'étudiant s'il a trouvé un chemin, ne change pas sinon
     * @return true si un chemin a été trouvé, false sinon
     */
    private boolean setNewWalkTarget(Int2D finalPos, StudentState stateAfter)  {
		//Position courante
    	Int2D currentPos = pic.getModel().getObjectLocation(this);
    	//Mise à jour du chemin à suivre
    	path = pic.getPath(currentPos, finalPos);
    	//L'étudiant peut se rendre à sa destination
    	if(path.size() != 0)
    		studentState = stateAfter;
    	//Sinon, l'étudiant est bloqué pour X raisons (e.g. veut sortir d'une file d'attente mais bloqué derrière) : il retentera plus tard
    	return path.size() != 0;
    }
    
    /**
     * Avance sur le chemin pré-déterminé par l'étudiant 
     * @return true si l'étudiant a fini son chemin
     */
    private boolean advancePath() {
    	//Position courante
    	Int2D currentPos = pic.getModel().getObjectLocation(this);
    	
    	int i = walkCapacity;
    	
    	//Nombre d'étudiants sur la case
    	int studentsOnPos = pic.getEntitiesAtLocation(currentPos, Student.class).size();
    	
    	//Plus la case est remplie, moins les étudiants arrivent à se déplacer
    	double prob = walkCapacity * (1.0 / studentsOnPos);
    	if(pic.random.nextDouble() * walkCapacity < prob) {
	    	Int2D finalPos = currentPos;
	    	
	    	//Déplacement et mise à jour
	    	while(path.size() > 0 && i >= 1) {
	    		finalPos = path.remove(0);
	    		--i;
	    	}
	    	
	    	//Mise à jour de la direction
	    	if(path.size() > 0) {
	    		setDirection(currentPos, path.get(0));
	    	}
	    	
	    	//Mise à jour de la position
	    	pic.getModel().setObjectLocation(this, finalPos);
    	}
    	return path.isEmpty();
    }
    
    /**
     * Consomme une gorgée de bière
     */
    private void swallowBeer() {
    	cup.drink(Constant.STUDENT_SWALLOW_CAPACITY);
    	//Augmentation du taux d'alcoolémie
		alcoholLevel += Constant.STUDENT_SWALLOW_CAPACITY * Constant.BEER_AlCOHOL_LEVEL / Constant.CUP_CAPACITY;
    	//S'il a fini sa bière, on incrémente le nombre de bières bues et on le place dans un état de décision
    	if(cup.isEmpty())
    		++beerDrunk;
    }
    
    /**
     * Indique si l'étudiant doit entrer dans le Pic
     * @return Booléen
     * @throws IllegalStateException si l'étudiant est déjà dans le Pic
     */
    private boolean mustEnterPic() throws IllegalStateException {
    	if(studentState != OUTSIDE) throw new IllegalStateException("Student is already inside Pic");
    	//Cas où l'étudiant est déjà parti, modification de la probabilité de re-rentrer
    	double factor = 1;
		//TODO Réfléchir à moduler son retour en fonction de son ivresse ? 
    	if(hasBeenInside) {
    		factor = Probability.STUDENT_REENTER_FACTOR;
    		if(veryPoor)
    			factor = Probability.STUDENT_REENTER_POOR_FACTOR;
    	}
    	double rand = pic.random.nextDouble();
    	
    	//Cas où l'heure actuelle du pic est comprise dans les horaires classiques de l'étudiant 
    	if(pic.isPicTimeWithin(arrivalTime, departureTime)) 
    		return rand < Probability.STUDENT_ENTER_PIC_WITHIN_INTERVAL / Probability.STUDENT_REENTER_SATURATION * factor;
	
		//Cas où il est en dehors de ses heures habituelles :
		return rand < Probability.STUDENT_ENTER_PIC_OUTSIDE_INTERVAL / Probability.STUDENT_REENTER_SATURATION * factor;
    }
    
    /**
     * Indique si l'étudiant doit sortir du Pic
     * @return Booléen
     * @throws IllegalStateException si l'étudiant n'est pas dans le Pic
     */
    private boolean mustLeavePic() throws IllegalStateException {
    	if(studentState == OUTSIDE) throw new IllegalStateException("Student is not inside Pic");
    	
    	//Probabilité utilisée pour la comparaison
		double rand = pic.random.nextDouble();
		
    	//Si le pic va ferme, l'étudiant part quoi qu'il arrive
    	if(Duration.between(pic.getTime(), Constant.PIC_END).toMinutes() < Constant.PIC_DELTA_TO_LEAVE) return true;
		
    	//Si l'étudiant est trop bourré, il a une chance de partir
		else if(isDrunk())
			return rand < Probability.STUDENT_LEAVE_DRUNK / Probability.STUDENT_LEAVE_SATURATION;
		
		
    	//Sinon, l'étudiant ne part que s'il a fini sa bière
    	if(cup.isEmpty()) {
    		//S'il est temps de partir
    		if(departureTime.isBefore(pic.getTime())) {
    			//Si l'étudiant a prévu de rester plus tard
    			if(willStayLater) 
    				return rand < Probability.STUDENT_LEAVE_HOUR_PAST_BUT_ANYWAY / Probability.STUDENT_LEAVE_SATURATION;
    			//Sinon, s'il a prévu de partir à l'heure
    			return rand < Probability.STUDENT_LEAVE_HOUR_PAST / Probability.STUDENT_LEAVE_SATURATION;
    		}
    		//S'il est pauvre, il a une chance de partir
    		if(veryPoor) 
    			return rand < Probability.STUDENT_LEAVE_POOR / Probability.STUDENT_LEAVE_SATURATION;
    		//S'il a consommé son quota, il a une chance de partir
    		if(beerDrunk >= beerMax)
    			return rand < Probability.STUDENT_LEAVE_NO_MORE_DRINK / Probability.STUDENT_LEAVE_SATURATION;
    	}
    	return false;
    }
    
    /**
     * Renvoie une position aléatoire valide vers laquelle se déplacer
     * @return Position
     */
    private Int2D getPositionToWalkTo() {
    	double rand = pic.random.nextDouble();
    	//S'il rentre dans le Pic il ne s'attarde pas avec des potes
    	if(pic.getModel().getObjectLocation(this).equals(Constant.PIC_ENTER)) return pic.getRandomValidLocation();
    	if(rand < Probability.STUDENT_WALK_TO_FRIEND) return pic.getStudentValidLocation(this);
    	Int2D pos = pic.getRandomValidLocation();
    	return pos;
    }
    
    /**
     * Indique si l'étudiant doit aller chercher une bière
     * @return true si l'étudiant doit aller chercher une bière
     */
    private boolean mustGetBeer() {
    	double rand = pic.random.nextDouble();
    	//Conditions triviales
    	if(!cup.isEmpty() || veryPoor || !pic.isBeerTime()) return false;
    	
    	//Si l'étudiant est ivre, il a une chance de ne plus consommer
    	if(isDrunk() && rand < Probability.STUDENT_ORDER_IF_DRUNK / Probability.STUDENT_ORDER_SATURATION) return false;
    	
    	double remainingTime = Math.min(Constant.PIC_BEER_END.toSecondOfDay(), departureTime.toSecondOfDay());
    	
    	//S'il n'a plus le temps de boire en temps simulation
    	if(Constant.CUP_CAPACITY / Constant.STUDENT_SWALLOW_CAPACITY > remainingTime) return false;
    	
    	//Facteur de modification de la proba
    	double factor = 0.7;
    	
    	//S'il n'a plus le temps de boire en temps Pic rapport à sa moyenne, on augmente
    	if(drinkingTime < remainingTime / 60) factor = 1.0;
    	
    	DayOfWeek currentDay = Constant.DATE.getDayOfWeek();
    	//Si on est en fin de semaine, le factor modificateur augmente
    	if(currentDay.equals(DayOfWeek.THURSDAY) || currentDay.equals(DayOfWeek.FRIDAY)) factor += 0.2;
    	
    	//Si l'étudiant n'a pas bu toutes ses bières, il a toutes les chances d'en reprendre une
    	if(beerDrunk < beerMax) return rand < factor / Probability.STUDENT_ORDER_SATURATION;
    	
    	//Sinon, on pondère par une probabilité
    	return rand < Probability.STUDENT_ORDER_IF_NO_MORE_BEERS / Probability.STUDENT_ORDER_SATURATION * factor;
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
    	//Si l'étudiant est sur une file d'attente il doit bouger
    	if(pic.isOnWaitingLine(pic.getModel().getObjectLocation(this))) return true;
		double rand = pic.random.nextDouble();
		//S'il parle avec des amis
		if(studentState == StudentState.DRINKING_WITH_FRIENDS)
			return rand < Probability.STUDENT_WALK_WHEN_TALKING_WITH_FRIENDS / Probability.STUDENT_WALK_SATURATION;
		//S'il ne fait rien de particulier
		return rand < Probability.STUDENT_WALK_WHEN_NOTHING / Probability.STUDENT_WALK_SATURATION;
    }
    
    /**
     * Indique si l'étudiant doit boire une gorgée de bière
     * @return Booléeen
     */
    private boolean mustDrinkBeer() {
    	return !cup.isEmpty() && pic.random.nextDouble() < getDrinkingTimeFactor();
    }
    
    /**
	 * Renvoie un facteur proportionnel au temps qu'a l'étudiant
	 * pour consommer les bières qu'il a prévu de consommer, compris entre 0 et 1.
	 * Plus l'étudiant a de temps, plus ce facteur est faible, et inversement.
	 * On peut interpréter ce facteur comme la probabilité de boire une gorgée, et
	 * => en moyenne <=, cette probabilité aura été réalisée au bout du nombre de
	 * secondes moyen que devrait respecter l'étudiant pour tout consommer à temps.
	 * @return booleen
	 */
	private double getDrinkingTimeFactor() {
		//Nombre de gorgées restantes pour boire la bière courante
		double n = (double)Constant.CUP_CAPACITY / Constant.STUDENT_SWALLOW_CAPACITY;
		
		//Nombre de bières restante à boire, en moyenne
		int beerLeft = beerMax - beerDrunk;

		//Nombre de secondes nécessaires pour boire toutes ses bières		
		double time = drinkingTime * 60 * beerLeft;

		//Numéro de seconde de la fermeture ou du départ
		long logicalDepartureTime = Math.min(Constant.PIC_BEER_END.toSecondOfDay(), departureTime.toSecondOfDay());
		
		//Temps restant avant de ne plus pouvoir commander ou de partir
		double timeLeft = logicalDepartureTime - pic.getTime().toSecondOfDay();
		
		//Oh, le fourbe a déjà dépassé son heure : on considère qu'il va partir bientôt
		if(timeLeft < 0) timeLeft = pic.getTime().plusMinutes(10).toSecondOfDay();
		
		//Si l'étudiant va rester plus tard, on ajoute un temps aléatoire prévisionnel
		if(willStayLater) 
			timeLeft += pic.random.nextDouble() * (Constant.PIC_END.toSecondOfDay() - logicalDepartureTime);
		
		/* On cherche ici à faire tendre la fin de sa consommation de bière à l'heure à
		 * laquelle il part ou à l'heure à laquelle on ne sert plus de rien, en pondérant 
		 * par rapport à la vitesse à laquelle il boit sa bière. Plus il a de temps,
		 * plus il boit lentement.
		 */
		time = timeLeft * ((timeLeft / time) * (0.4 + (pic.random.nextDouble() * 0.3)));
		
		//Nombre de gorgée restantes sur le temps calculé de consommation totale
		return (n * Math.max(beerLeft, 1)) / time;
	}

	/**
	 * Permet à l'étudiant de recharger son compte
	 * @param money quantité ajoutée au compte
	 */
	private void rechargePayutc(double money) {
		payUTC.transfer(bankAccount, money);
	}
	
	/**
	 * Choisit une file d'attente au hasard.
	 * @return File d'attente aléatoire
	 */
	private WaitingLine chooseRandomWaitingLine() {
		List<WaitingLine> lines = pic.getWaitingLines();
		return lines.get(pic.random.nextInt(lines.size()));
	}
	
	/**
	 * Choisit une file d'attente sur laquelle s'insérer. La file choisie est 
	 * celle contenant le moins d'étudiant au moment d'y entrer, ou, s'il y 
	 * a "plusieurs" files minimales, une au hasard.
	 * @return File d'attente minimale
	 */
	private WaitingLine chooseMinimalWaitingLine() {
		List<WaitingLine> lines = pic.getWaitingLines();
		//Nombre minimum d'étudiants dans une file
		Integer minStudents = 
			lines
				.stream()
				.min((l, r) -> Integer.compare(l.getStudentNumber(), r.getStudentNumber()))
				.map(WaitingLine::getStudentNumber)
				.orElse(0);
		
		//Files ayant le nombre d'étudiants minimal 
		List<WaitingLine> candidates =
			lines
				.stream()
				.filter(l -> l.getStudentNumber() == minStudents)
				.collect(Collectors.toList());
		
		//Choix au hasard
		return candidates.get(pic.random.nextInt(candidates.size()));
	}

	/**
	 * L'étudiant rentre dans une file d'attente sur sa position actuelle
	 */
	private void enterWaitingLine() throws IllegalStateException {
		//On récupère la file d'attente
		List<WaitingLine> lines = pic.getEntitiesAtLocation(pic.getModel().getObjectLocation(this), WaitingLine.class);
		if(lines.isEmpty()) throw new IllegalStateException("Aucune file d'attente sur la position courante!");
		//Si cette file existe, on entre dedans et on modifie l'état de l'étudiant
		lines.get(0).enterLine(new Order(this, getOrder()));
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
			int selected = 0;
			double total = probs[0];
			for(int i = 1; i < probs.length; ++i) {
				total += probs[i];
				if(pic.random.nextDouble() <= (probs[i] / total)) selected = i;
			}
            beer = getRandomGradeBeer(beerGrades[selected]);
		}
        return beer;
    }

	/**
	 * Retourne une bière aléatoire selon l'ordre de préférence
	 * @param grade Ordre de préférence
	 * @return Bière
	 */
    private Beer getRandomGradeBeer(int grade) {
        List<Beer> beersWithGrade = beersGrade.entrySet()
            .stream()
            .filter(beerIntegerEntry -> beerIntegerEntry.getValue() == grade)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        if(beersWithGrade.size() == 0) return null;
        return beersWithGrade.get(pic.random.nextInt(beersWithGrade.size()));
    }

    /**
     * Détermine et assigne la direction de l'étudiant
     * @param before Position actuelle
     * @param after Position suivante
     */
    private void setDirection(Int2D before, Int2D after) {
    	if(after.x > before.x) direction = Direction.RIGHT;
    	else if(after.y > before.y) direction = Direction.BOTTOM;
    	else if(after.x < before.x) direction = Direction.LEFT;
    	else if(after.y < before.y) direction = Direction.TOP;
    	//Par défaut
    	else direction = Direction.BOTTOM;
    }
    
    private void setRandomDirection() {
    	double random = pic.random.nextDouble();
    	if(random < 0.25) direction = Direction.BOTTOM;
    	else if(random < 0.5) direction = Direction.LEFT;
    	else if(random < 0.75) direction = Direction.RIGHT;
    	else direction = Direction.TOP;
    }
}
