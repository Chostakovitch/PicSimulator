package Model;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.threeten.extra.Interval;

import Agent.BarCounter;
import Agent.Barrel;
import Agent.Bartender;
import Agent.BigTable;
import Agent.Chair;
import Agent.CheckoutCounter;
import Agent.Clock;
import Agent.Floor;
import Agent.Inanimate;
import Agent.Invalid;
import Agent.Stair;
import Agent.Student;
import Agent.Table;
import Agent.WaitingLine;
import Agent.Wall;
import Enum.Beer;
import Enum.Direction;
import Enum.WallStatus;
import PathFinding.AStar;
import PathFinding.Node;
import Util.Constant;
import Util.DataPicker;
import Util.DateTranslator;
import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * Modèle du Pic. Permet de gérer l'environnement sous forme de grille, où les agents
 * animés ou non interagissent. 
 */
public class Pic extends SimState {
	private static final long serialVersionUID = 1L;
	private SparseGrid2D pic = new SparseGrid2D(Constant.PIC_WIDTH, Constant.PIC_HEIGHT);
    
	/**
	 * Horaire actuel du Pic
	 */
	private Instant time;
	
	/**
	 * Horaires d'ouverture du Pic
	 */
	private Interval timeslot;
	
	/**
	 * Horaires d'exploitation de la licence II
	 */
	private Interval beerTimeslot;
	
	/**
	 * Nombre d'étudiants au Pic
	 */
	private int studentsInside;

	//TODO pourquoi ?
	private List<Barrel> barrels;

	private List<Barrel> unavailableBeer;

	private CheckoutCounter cc;
	
	private List<WaitingLine> waitingLines;
	
    public Pic(long seed) {
    	super(seed);
    	timeslot = Interval.of(getUTCInstant(Constant.PIC_BEGIN), getUTCInstant(Constant.PIC_END));
    	beerTimeslot = Interval.of(getUTCInstant(Constant.PIC_BEER_BEGIN), getUTCInstant(Constant.PIC_BEER_END));
    	//On considère que la simulation commence à l'ouverture du Pic
    	time = timeslot.getStart();

    	barrels = new ArrayList<>();
		unavailableBeer = new ArrayList<>();
		waitingLines = new ArrayList<>();
		
    	studentsInside = 0;
		cc = new CheckoutCounter();
    }

    @Override
	public void start() {
        System.out.println("GOD DAMN SIMULATION STARTED");
        //Remise à zéro des variables du Pic
    	time = timeslot.getStart();
    	studentsInside = 0;
    	cc = new CheckoutCounter();
    	waitingLines.clear();
    	barrels.clear();
        super.start();
        pic.clear();
        
        //Ajout des agents dont la position est connue à l'avance
        constructInitialMap();
        
        //Ajout des étudiants
        addAgentsStudent();

        //Ajout de l'horloge
        addClock();
    }
    
    /**
     * Teste si l'instant actuel est compris dans les heures d'ouverture du Pic
     * @return true si le pic est ouvert
     */
    public boolean isPicOpened() {
    	return timeslot.contains(time);
    }
    
    /**
     * Teste si l'instant actuel est compris dans les heures de service licence II du Pic
     * @return true si on peut acheter des bières
     */
    public boolean isBeerTime() {
    	return beerTimeslot.contains(time);
    }
    
    /**
     * Vérifie si l'instant actuel est compris entre deux heures relatives
     * @param begin Heure locale de début
     * @param end Heure locale de fin
     * @return true si l'instant actuel est compris
     */
    public boolean isPicTimeWithin(LocalTime begin, LocalTime end) {
    	Instant beginUTC = getUTCInstant(begin);
    	Instant endUTC = getUTCInstant(end);
    	Interval interval = Interval.of(beginUTC, endUTC);
    	return interval.contains(time);
    }
    
    /**
     * Vérifie si l'heure relative demandée est comprise entre deux heures relatives
     * @param begin Heure relative de début
     * @param end Heure relative de fin
     * @param instant Heure relative à opposer
     * @return true si l'heure concernée est comprise
     */
    public boolean isTimeWithin(LocalTime begin, LocalTime end, LocalTime instant) {
    	Instant beginUTC = getUTCInstant(begin);
    	Instant endUTC = getUTCInstant(end);
    	Instant instantUTC = getUTCInstant(instant);
    	Interval interval = Interval.of(beginUTC, endUTC);
    	return interval.contains(instantUTC);
    }
    
    public boolean isLocationValid(Int2D pos) {
    	return isLocationValid(pos.x, pos.y);
    }

	/**
     * Indique si une position est "valide" pour un déplacement :
     *  - Si la position est dans les délimitations virtuelles de la grille (qui n'est pas bornée) ;
     *  - Si la position n'est pas celle d'un objet inanimé, sauf file d'attente
     *  - Si la position n'est pas pleine
     * @param x Coordonnée en abscisses
     * @param y Coordonnéé en ordonnées
     * @return true si la position est dans les délimitations virtuelles de la grille
     */
    public boolean isLocationValid(int x, int y) {
    	//En dehors de la grille
    	if(!(x >= 0 && y >= 0 && x < pic.getWidth() && y < pic.getHeight())) return false;
    	//Case pleine
    	if(isLocationFull(x, y)) return false;
    	//Vérification des objets inanimés
		Bag b = pic.getObjectsAtLocation(x, y);
		if (b != null) {
			for (Object o : b) {
				if (o instanceof Invalid) return false;
			}
		}
    	return true;
    }
    
    public boolean isLocationFull(Int2D pos) {
    	return isLocationFull(pos.x, pos.y);
    }
    
    /**
     * Indique si une position est "remplie", c'est à dire si le nombre maximal d'étudiants sur une case est atteint
     * @param x Coordonnée en abscisses
     * @param y Coordonnéé en ordonnées
     * @return true si la capacité maximale en étudiants de la case est atteinte
     */
    public boolean isLocationFull(int x, int y) {
    	Int2D pos = new Int2D(x, y);
    	int studentNumber = getEntitiesAtLocation(pos, Student.class).size();
    	List<Chair> chairs = getEntitiesAtLocation(pos, Chair.class); 
    	return 
    			//Une file d'attente a une capacité illimitée
    			getEntitiesAtLocation(pos, WaitingLine.class).size() == 0 &&
    			(
    				//Pas plus d'un étudiant par chaise	
    				(!chairs.isEmpty() && studentNumber > 0)
    			||
    				//On compte le nombre d'étudiants sur la case, si ce n'est pas une file d'attente
    			 	studentNumber >= Constant.MAX_STUDENT_PER_CELL
    			);
    }

    /**
     * Récupère le couple (fût, position) correspond à la bière demandée
     * @param b Bière demandée
     * @return Couple (fût, position)
     */
    public Map.Entry<Barrel, Int2D> getBarrel(Beer b) {
    	Optional<Barrel> optionalBarrel = barrels.stream()
				.filter(barrelInt2DEntry -> barrelInt2DEntry.getType() == b).findFirst();

    	if(optionalBarrel.isPresent()) {
    		Barrel barrel = optionalBarrel.get();
			return new AbstractMap.SimpleEntry<>(barrel, getModel().getObjectLocation(barrel));
		}

		return null;
	} 
    
    /**
     * Calcule une position aléatoire valide sur la grille qui n'est pas sur une file d'attente. 
     * En interne, génère des positions aléatoires
     * jusqu'à en trouver une valide. Bien qu'algorithmiquement peu performant, statistiquement plus efficace
     * que de tirer aléatoirement dans l'ensemble des positions valides (nb_valid >> nb_invalid)
     * @return Position valide
     */
    public Int2D getRandomValidLocation() {
    	Int2D pos;
    	int x;
    	int y;
    	do {
    		x = random.nextInt(Constant.PIC_WIDTH);
    		y = random.nextInt(Constant.PIC_HEIGHT);
    		pos = new Int2D(x, y);
    	} while(!isLocationValid(pos) || isOnWaitingLine(pos));
    	return pos;
    }
    
    /**
     * Calcule une position aléatoire VALIDE sur laquelle se trouve un étudiant et qui n'est pas sur une file
     * @param student Étudiant à EXCLURE du tirage
     * @return Position valide contenant un ou plusieurs étudiants
     */
    public Int2D getStudentValidLocation(Student student) {
    	Int2D pos = null;
    	boolean found = false;
    	int limit = 20;
    	while(!found && limit > 0) {
    		pos = getRandomValidLocation();
    		List<Student> studentFounds = getEntitiesAtLocation(pos, Student.class);
    		//Si on a trouvé une case avec un étudiant autre que l'étudiant passé en paramètre
    		if(studentFounds.size() > 0 && !studentFounds.contains(student)) found = true;
    		--limit;
    	}
    	return pos;
    }
    
    /**
     * Calcule le chemin le plus court entre deux positions sur la grille. Évite les objets qui
     * implémente l'interface Inanimate. L'algorithme utilisé est A*, implémenté en Java par Marcelo Surriabre,
     * et cloné depuis son GitHub : https://github.com/marcelo-s/A-Star-Java-Implementation
     * @param start Position de départ
     * @param end Position de fin
     * @return Liste de positions pour aller jusqu'à la destination souhaitée
     */
    public List<Int2D> getPath(Int2D start, Int2D end) {
    	//Noeuds de départ de d'arrivée
    	Node initialPos = new Node(start.x, start.y);
    	Node finalPos = new Node(end.x, end.y);
    	
    	//Construction de la grille utilisée par A*
    	AStar aStar = new AStar(Constant.PIC_WIDTH, Constant.PIC_HEIGHT, initialPos, finalPos);
    	
    	//Construction de l'ensemble des noeuds bloquants
    	List<Int2D> blocks = getAllInvalidLocations();
    	aStar.setBlocks(blocks);
    	
    	//Calcul et conversion du chemin
    	List<Node> path = aStar.findPath();
    	List<Int2D> pathPos = new ArrayList<>();
    	for(Node node : path) pathPos.add(new Int2D(node.getRow(), node.getCol()));
    	
    	return pathPos;
    }

    public CheckoutCounter getCheckoutCounter() {
    	return cc;
	}

	public Int2D getCheckoutCounterLocation() {
    	return pic.getObjectLocation(cc);
	}
    
    public SparseGrid2D getModel() {
        return pic;
    }
    
    /**
     * Incrément le temps Pic d'une unité de temps constante
     */
    public void incrCurrentTime() {
    	time = time.plusSeconds(Constant.TIMESTEP);
    }
    
    /**
     * Renvoie une heure agnostique
     * @return LocalTime
     */
    public LocalTime getTime() {
    	return LocalTime.from(time.atZone(ZoneId.systemDefault()));
    }
    
    public void incrStudentsInside() {
    	++studentsInside;
    }
    
    public void decStudentsInside() {
    	--studentsInside;
    }
    
    public int getStudentsInside() {
    	return studentsInside;
    }
   
    public List<WaitingLine> getWaitingLines() {
		return waitingLines;
	}

	/**
     * Détermine les entités d'un type T à une position donnée
     * @param pos Position où chercher
     * @param type Type à chercher
     * @return Liste de T
     */
    public <T> List<T> getEntitiesAtLocation(Int2D pos, Class<T> type) {
    	if(pos == null) return new ArrayList<T>();
    	Bag entities = pic.getObjectsAtLocation(pos);
    	if(entities == null) return new ArrayList<T>();
    	return Arrays
			.stream(entities.objs)
			.filter(e -> type.isInstance(e))
			.map(type::cast)
			.collect(Collectors.toList());
    }
    
    /**
     * Calcule toutes les positions invalides de la grille (au sens de isLocationValid)
     * @return Liste des positions invalides
     */
    public List<Int2D> getAllInvalidLocations() {
    	List<Int2D> pos = new ArrayList<>();
    	for(int i = 0; i < Constant.PIC_WIDTH; ++i) {
    		for(int j = 0; j < Constant.PIC_HEIGHT; ++j) {
    			if(!isLocationValid(i, j)) pos.add(new Int2D(i, j));
    		}
    	}
    	return pos;
    }
    
    /**
     * Teste si une position est sur une file d'attente
     * @param pos Position à tester
     * @return true si la position est sur une file d'attente
     */
    public boolean isOnWaitingLine(Int2D pos) {
    	for(WaitingLine line : getWaitingLines()) {
    		Int2D linePos = getModel().getObjectLocation(line);
    		if(linePos.x == pos.x && linePos.y == pos.y) return true;
    	}
    	return false;
    }
    
    /**
     * Ajoute les étudiants à la simulation.
     * Bien noter que tous les étudiants ne participeront peut-être
     * pas à la simulation **effective** dans la grille.
     * Ils pourront être en dehors du Pic et venir, ou non,
     * en fonction des horaires. Pour cette raison, ils ne sont 
     * ajoutés qu'au scheduling et non à la grille.
     */
    private void addAgentsStudent() {
    	int i = 0;
    	int student_number = DataPicker.getInstance().getStudentPerDayOf(Constant.DATE);
		while (i < student_number) {
    	    String[] line = DataPicker.getInstance().getRandomLineStudent();
    	    //Probabilité d'aller au Pic de base
    		double probability = 0.2;
    		String[] preferedDays = DateTranslator.translateArray(line[20].split(","));
    		//Si le jour courant est un de ses jours habituels, il a plus de chance d'y entrer
			if(Arrays.asList(preferedDays).contains(Constant.DATE.getDayOfWeek())) {
				probability = 0.75;
			}
			//Si en plus on est Jeudi, il a plus de chances d'aller se rincer
			if(!Constant.DATE.getDayOfWeek().equals(DayOfWeek.THURSDAY)){
				probability += random.nextDouble() * 0.3;
			}
			//Si en plus on est Vendredi, il a plus de chances d'aller se rincer
			if(!Constant.DATE.getDayOfWeek().equals(DayOfWeek.FRIDAY)){
				probability += random.nextDouble() * 0.2;
			}
    	    //S'il la probabilité lui permet de rentrer, il participe à la simulation
    	    if(random.nextDouble() < probability) {
    	        schedule.scheduleRepeating(new Student(DataPicker.getInstance().getRandomLineStudent(), this));
    	        i++;
    	    }
    	}
    }
    
    /**
     * Ajoute l'horloge représentant "l'heure réelle" à la simulation
     */
    private void addClock() {
    	schedule.scheduleRepeating(new Clock());
    }
    
    /**
     * Ajoute tous les agents dont la position est connue à l'avance
     * à la simulation, via une carte.
     */
    private void constructInitialMap() {
    	int bartenderCount = 0;
        int barrelCount = 0;
        for(int j = Constant.PIC_WIDTH - 1; j >= 0; --j) {
        	for(int i = Constant.PIC_HEIGHT - 1; i >= 0; --i) {
        		switch(Constant.PIC_MAP[i][j]) {
        			case 2: 
        				WaitingLine w = new WaitingLine();
        				waitingLines.add(w);
        				pic.setObjectLocation(w, j, i);
        				break;
        			case 3: pic.setObjectLocation(new Stair(), j, i); break;
        			case 4: 
        				Bartender ba = new Bartender(waitingLines.get(bartenderCount), Constant.BARTENDER_TIME_TO_SERVE, Constant.BARTENDER_TIME_TO_FILL, Constant.BARTENDER_TIME_TO_CHECKOUT, new Int2D(j, i));
        				++bartenderCount;
        				pic.setObjectLocation(ba, j, i);
        				schedule.scheduleRepeating(ba);
        				break;
        			case 6:
        				Beer beer = null;
        				//Cas d'une configuration esthétique où un fût n'est associé à rien
        				try {
        					beer = Arrays.asList(Beer.values()).get(barrelCount);
        				} catch(Exception e) { }
        				Barrel b1 = new Barrel(beer);
        				++barrelCount;
        				barrels.add(b1);
        				pic.setObjectLocation(b1, j, i);
        				try {
        					beer = Arrays.asList(Beer.values()).get(barrelCount);
        				} catch(Exception e) { }
        				Barrel b2 = new Barrel(beer);
        				++barrelCount;
        				barrels.add(b2);
        				pic.setObjectLocation(b2, j, i);
        				break;
        			case 7: pic.setObjectLocation(cc, j, i); break;
        			case 10: pic.setObjectLocation(new Wall(WallStatus.BOTTOM), j, i); break;
        			case 11: pic.setObjectLocation(new Wall(WallStatus.TOP), j, i); break;
        			case 12: pic.setObjectLocation(new Wall(WallStatus.RIGHT), j, i); break;
        			case 13: pic.setObjectLocation(new Wall(WallStatus.LEFT), j, i); break;
        			case 14: pic.setObjectLocation(new Wall(WallStatus.BOTTOM_LEFT), j, i); break;
        			case 15: pic.setObjectLocation(new Wall(WallStatus.BOTTOM_RIGHT), j, i); break;
        			case 16: pic.setObjectLocation(new Wall(WallStatus.TOP_LEFT), j, i); break;
        			case 17: pic.setObjectLocation(new Wall(WallStatus.TOP_RIGHT), j, i); break;
        			case 18: pic.setObjectLocation(new Wall(WallStatus.INTERSECT_VERTICAL), j, i); break;
        			case 19: pic.setObjectLocation(new Wall(WallStatus.INTERSECT_HORIZONTAL), j, i); break;
        			case 20: pic.setObjectLocation(new Wall(WallStatus.TOP_INTERSECT), j, i); break;
        			case 21: pic.setObjectLocation(new Wall(WallStatus.LEFT_INTERSECT), j, i); break;
        			case 22: pic.setObjectLocation(new Wall(WallStatus.RIGHT_INTERSECT), j, i); break;
        			case 23: pic.setObjectLocation(new Wall(WallStatus.BOTTOM_INTERSECT), j, i); break;
        			case 24: pic.setObjectLocation(new Wall(WallStatus.CENTER_INTERSECT), j, i); break;
        			case 25: pic.setObjectLocation(new BarCounter(WallStatus.BOTTOM), j, i); break;
        			case 26: pic.setObjectLocation(new BarCounter(WallStatus.RIGHT), j, i); break;
        			case 27: pic.setObjectLocation(new BarCounter(WallStatus.BOTTOM_RIGHT), j, i); break;
        			case 28: pic.setObjectLocation(new Chair(Direction.TOP), j, i); break;
        			case 29: pic.setObjectLocation(new Chair(Direction.BOTTOM), j, i); break;
        			case 30: pic.setObjectLocation(new Chair(Direction.LEFT), j, i); break;
        			case 31: pic.setObjectLocation(new Chair(Direction.RIGHT), j, i); break;
        			case 32: pic.setObjectLocation(new Table(), j, i); break;
        			case 33: pic.setObjectLocation(new BigTable(), j, i); break;
        		}
        	}
        }
    }
    
    /**
     * Crée un instant au format UTC à partir d'une heure abstraite, basé sur la date du jour
     * @param time Objet représentant un instant dans le temps
     */
    private Instant getUTCInstant(LocalTime time) {
    	LocalDate today = LocalDate.now();
    	LocalDateTime todayWithTime = today.atTime(time);
    	ZoneId defaultZoneId = ZoneId.systemDefault();
    	ZonedDateTime zonedTodayWithTime = todayWithTime.atZone(defaultZoneId);
    	Instant instant = zonedTodayWithTime.toInstant();
    	return instant;
    }

    public void addUnavailableBarrel(Barrel b) {
    	unavailableBeer.add(b);
	}

	public void removeUnavailableBarrel(Barrel b) {
    	unavailableBeer.remove(b);
	}

	public List<Beer> getUnavailableBarrel() {
		return unavailableBeer.stream().map(Barrel::getType).collect(Collectors.toList());
	}

	public boolean isBarrelBroken(Barrel b) {
    	return unavailableBeer.contains(b);
	}
}
