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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import Enum.Beer;
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
import sun.misc.Timeable;

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
        alcoholSensitivityGrade = Integer.parseInt(dataLine[21]);
        preferedDays = DateTranslator.translateArray(dataLine[20].split(","));
    }

    @Override
    public void step(SimState state) {
        pic = (Pic) state;
        //Quoiqu'il arrive, si l'étudiant a une bière, il peut la consommer avant de décider de son action
        if(!cup.isEmpty() && mustDrinkBeer()) {
        	cup.drink(Constant.STUDENT_SWALLOW_CAPACITY);
        	if(cup.isEmpty())
        		++beerDrunk;
        }
        
        //Décision en fonction de l'état de l'étudiant
        switch(studentState) {
	        //L'étudiant attend pour une bière, il n'a rien à faire
	        case WAITING_FOR_BEER: case WAITING_IN_QUEUE:
	        	return;
			case DRINKING_WITH_FRIENDS:
				//TODO Faire durer l'activité plus longtemps que juste pcq ils ont envie de boire ?
				//TODO Pour l'instant quand sa bière est vide il revient à l'état vide pour prendre une décision
				if(cup.isEmpty()) studentState = NOTHING;
				break;
	        //L'étudiant est en dehors du Pic, il prend une décision
	        case OUTSIDE:
	        	//L'étudiant arrive à l'entrée du Pic, il bouge immédiatement sur une position valide
	        	if(mustEnterPic()) {
	        		if(!pic.isLocationFull(Constant.PIC_ENTER)) {
		        		pic.getModel().setObjectLocation(this, Constant.PIC_ENTER);
		            	pic.incrStudentsInside();
		            	studentState = NOTHING;
		            	hasBeenInside = true;
	        		}
	        	}
	        	break;
	        //L'étudiant ne fait rien, il prend une décision
	        case NOTHING:
	        	if(mustLeavePic()) setNewWalkTarget(Constant.EXIT_POSITION, WALKING_TO_EXIT);
	        	//Choisit une file d'attente et initie un déplacement
	        	else if(mustGetBeer()) {
	        		WaitingLine line = chooseRandomWaitingLine();
	        		setNewWalkTarget(pic.getModel().getObjectLocation(line), CHOOSING_WAITING_LINE);
	        	}
	        	//Choisit une destination aléatoire
	        	else if(mustWalk()) setNewWalkTarget(pic.getRandomValidLocation(), WALKING);
	        	break;
	        //L'étudiant marchait, il continue sa marche
	        case WALKING: case WALKING_TO_WAITING_LINE: case WALKING_TO_EXIT: case CHOOSING_WAITING_LINE:
	        	//Si l'étudiant a fini de marcher
	        	if(advancePath()) {
	        		switch(studentState) {
	        		//L'étudiant est arrivé aux files, il choisit la file minimale
	        		case CHOOSING_WAITING_LINE:
	        			WaitingLine line = chooseMinimalWaitingLine();
		        		setNewWalkTarget(pic.getModel().getObjectLocation(line), WALKING_TO_WAITING_LINE);
		        		break;
	        		//S'il allait vers une file d'attente minimale, il y rentre
	        		case WALKING_TO_WAITING_LINE:
		    			enterWaitingLine();
		    			studentState = WAITING_IN_QUEUE;
		    			break;
		    		//L'étudiant est en train de sortir, on le supprime graphiquement
	        		case WALKING_TO_EXIT:
		        		pic.getModel().remove(this);
		            	pic.decStudentsInside();
		            	studentState = OUTSIDE;
		            	break;
		    		default:
		    			if(veryPoor) //Si il est très pauvre, il va boucler sur vagabonder jusqu'à sortir du pic
		    				studentState = NOTHING;
		    			else //Sinon il se place pour boire
		    				studentState = DRINKING_WITH_FRIENDS;
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
	    	
	    	pic.getModel().setObjectLocation(this, finalPos);
    	}
    	return path.isEmpty();
    }
    
    /**
     * Indique si l'étudiant doit entrer dans le Pic
     * @return Booléen
     * @throws IllegalStateException si l'étudiant est déjà dans le Pic
     */
    private boolean mustEnterPic() throws IllegalStateException {
    	if(studentState != OUTSIDE) throw new IllegalStateException("Student is already inside Pic");
 	   	/* On ne teste que toutes les 5 minutes pour éviter de saturer la proba
 	   	Si on est sur un nombre de minutes divisible par 5, on teste s'il peut rentrer */
    	if(pic.getMinutes() % 5 == 0) {
	    	//Cas où l'étudiant est déjà parti, modification de la probabilité de re-rentrer
	    	double factor = hasBeenInside ? Probability.STUDENT_REENTER_FACTOR : 1;
	    	
	    	//Cas où l'heure actuelle du pic est comprise dans les horaires classiques de l'étudiant 
	    	if(pic.isPicTimeWithin(arrivalTime, departureTime)) 
	    		return pic.random.nextDouble() < Probability.STUDENT_ENTER_PIC_WITHIN_INTERVAL * factor;
    	
    		//Cas où il est en dehors de ses heures habituelles :
    		return pic.random.nextDouble() < Probability.STUDENT_ENTER_PIC_OUTSIDE_INTERVAL * factor;
    	}
    	return false;
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
        return !(!cup.isEmpty() || veryPoor || !pic.isBeerTime());
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

		//Nombre de secondes le séparant de la fermeture
		double logicalDepartureTime = Math.min(Constant.PIC_BEER_END.toSecondOfDay(), departureTime.toSecondOfDay());
		
		//Temps restant avant de ne plus pouvoir commander ou de partir
		double timeLeft = logicalDepartureTime - pic.getTime().toSecondOfDay();
		
		//Si l'étudiant va rester plus tard, on ajoute un temps aléatoire prévisionnel
		if(willStayLater) 
			timeLeft += pic.random.nextDouble() * (Constant.PIC_END.toSecondOfDay() - logicalDepartureTime);
		
		/* On cherche ici à faire tendre la fin de sa consommation de bières à l'heure à
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

    private Beer getRandomGradeBeer(int grade) {
        List<Beer> beersWithGrade = beersGrade.entrySet()
            .stream()
            .filter(beerIntegerEntry -> beerIntegerEntry.getValue() == grade)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        if(beersWithGrade.size() == 0) return null;
        return beersWithGrade.get(pic.random.nextInt(beersWithGrade.size()));
    }

    Beer reorder(List<Beer> unavailableBeer) {
		HashMap<Beer, Integer> savedGrade = new HashMap<>(beersGrade);
		beersGrade.keySet().removeAll(unavailableBeer);
		Beer choice = choiceOrder();
		beersGrade = savedGrade;
		return choice;
	}
}
