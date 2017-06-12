package Model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.threeten.extra.Interval;

import Agent.BarCounter;
import Agent.Barrel;
import Agent.Bartender;
import Agent.CheckoutCounter;
import Agent.Clock;
import Agent.Inanimate;
import Agent.Student;
import Agent.WaitingLine;
import Agent.Wall;
import PathFinding.AStar;
import PathFinding.Node;
import Util.Beer;
import Util.Constant;
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

	private HashMap<Barrel, Int2D> barrels;

	private CheckoutCounter cc;
	
    public Pic(long seed) {
    	super(seed);
    	timeslot = Interval.of(getUTCInstant(Constant.PIC_BEGIN), getUTCInstant(Constant.PIC_END));
    	beerTimeslot = Interval.of(getUTCInstant(Constant.PIC_BEER_BEGIN), getUTCInstant(Constant.PIC_BEER_END));
    	//On considère que la simulation commence à l'ouverture du Pic
    	time = timeslot.getStart();

    	barrels = new HashMap<>();
    	
    	studentsInside = 0;
    }

    @Override
	public void start() {
        System.out.println("GOD DAMN SIMULATION STARTED");
        //Remise à zéro du temps
    	time = timeslot.getStart();
        super.start();
        pic.clear();
        
        //Ajout des agents
		addAgentsWall();
		addAgentsBarCounter();
        addAgentsStudent();
        addAgentsBartenderAndWaitingLine();
        addAgentsBarrel();
        addAgentsBarCounter();

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
     * Indique si une position est "valide" pour un déplacement :
     *  - Si la position est dans les délimitations virtuelles de la grille (qui n'est pas bornée) ;
     *  - Si la position n'est pas celle d'un objet inanimé
     *  - Si la position n'est pas pleine
     * @param x Coordonnée en abscisses
     * @param y Coordonnéé en ordonnées
     * @return true si la position est dans les délimitations virtuelles de la grille
     */
    public boolean isLocationValid(int x, int y) {
    	if(x >= 0 && y >= 0 && x < pic.getWidth() && y < pic.getHeight()) return false;
    	if(isLocationFull(x, y)) return false;
		Bag b = pic.getObjectsAtLocation(x, y);
		if (b != null) {
			for (Object o : b) {
				if (o instanceof Inanimate) return false;
			}
		}
    	return true;
    }
    
    /**
     * Indique si une position est "remplie", c'est à dire si le nombre maximal d'étudiants sur une case est atteint
     * @param x Coordonnée en abscisses
     * @param y Coordonnéé en ordonnées
     * @return true si la capacité maximale en étudiants de la case est atteinte
     */
    public boolean isLocationFull(int x, int y) {
    	int studentCount = 0;
    	Bag objs = pic.getObjectsAtLocation(x, y);
    	if(objs == null) return false;
    	//On compte le nombre d'étudiants sur une case
    	for(int i = 0; i < objs.size(); ++i) {
    		if(objs.get(i) instanceof Student) ++studentCount;
    	}
    	return studentCount >= Constant.MAX_STUDENT_PER_CELL;
    }

    public Map.Entry<Barrel, Int2D> getBarrel(Beer b) {
    	Optional<Map.Entry<Barrel,Int2D>> optionalBarrel = barrels.entrySet().stream()
				.filter(barrelInt2DEntry -> barrelInt2DEntry.getKey().getType() == b).findFirst();
		return optionalBarrel.orElse(null);
	}
    
    /**
     * Calcule un ensemble de position valides autour d'une position initiale pour un étudiant
     * sous forme d'un carré plein paramétré par un "rayon" maximal (une diagonale valant une unité)
     * Une position est valide si elle est dans la grille et n'est pas occupée par un objet
     * inanimé ou par un permanencier.
     * @param pos Position centrale
     * @param radius "Rayon" du carré
     * @return Liste de coordonnées valides
     */
    public List<Int2D> getSquareValidLocations(Int2D pos, int radius) {
    	List<Int2D> possiblePos = new ArrayList<>();
    	int x = pos.x;
    	int y = pos.y;
    	for(int i = x - radius; i <= x + radius; ++i) {
    		for(int j = y - radius; j <= y + radius; ++j) {
    			//On ne prend pas en compte la position centrale
    			if(!(i == x && j == y) && isLocationValid(i, j) && !isLocationFull(i, j)) {
    				possiblePos.add(new Int2D(i, j));
    			}
    		}
    	}
    	return possiblePos;
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
    	AStar aStar = new AStar(Constant.PIC_HEIGHT, Constant.PIC_WIDTH, initialPos, finalPos);
    	
    	//Construction de l'ensemble des noeuds bloquants
    	List<Int2D> blocks = getAllInvalidLocations();
    	aStar.setBlocks(blocks);
    	
    	//Calcul et conversion du chemin
    	List<Node> path = aStar.findPath();
    	List<Int2D> pathPos = new ArrayList<>();
    	for(Node node : path) pathPos.add(new Int2D(node.getRow(), node.getCol()));
    	
    	return pathPos;
    }
    
    public SparseGrid2D getModel() {
        return pic;
    }
    
    public void incrCurrentTime() {
    	time = time.plusSeconds(Constant.TIMESTEP);
    }
    
    public Instant getTime() {
    	return time;
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
    
    /**
     * Calcule toutes les positions invalides de la grille (au sens de isLocationValid)
     * @return Liste des positions invalides
     */
    private List<Int2D> getAllInvalidLocations()
    	List<Int2D> pos = new ArrayList<>();
    	for(int i = 0; i < pic.getHeight(); ++i) {
    		for(int j = 0; j < pic.getWidth(); ++j) {
    			if(!isLocationValid(i, j)) pos.add(new Int2D(i, j));
    		}
    	}
    	return pos;
    }

	/**
	 * Ajoute les murs à la simulation
	 */
	private void addAgentsWall() {
		for(int i = 0; i < Constant.PIC_WALLS.length; i++) {
			for (int x = Constant.PIC_WALLS[i][0].getX(); x <= Constant.PIC_WALLS[i][1].getX(); x++ ) {
				for (int y = Constant.PIC_WALLS[i][0].getY(); y <= Constant.PIC_WALLS[i][1].getY(); y++ ) {
					pic.setObjectLocation(new Wall(), x, y);
				}
			}
		}
	}

	/**
	 * Ajoute le comptoir de bar à la simulation
	 */
	private void addAgentsBarCounter() {
		for(int i = 0; i < Constant.PIC_BAR_COUNTER.length; i++) {
			for (int x = Constant.PIC_BAR_COUNTER[i][0].getX(); x <= Constant.PIC_BAR_COUNTER[i][1].getX(); x++ ) {
				for (int y = Constant.PIC_BAR_COUNTER[i][0].getY(); y <= Constant.PIC_BAR_COUNTER[i][1].getY(); y++ ) {
					pic.setObjectLocation(new BarCounter(), x, y);
				}
			}
		}
	}

	/**
	 * Ajouter la caisse enregistreuse
	 */
	private void addAgentsCheckoutCounter() {
		cc = new CheckoutCounter();
		//TODO Add checkout counter dans l'UI (à la place d'un des barrels)
	}

	/**
	 * Ajoute les fûts de bière à la simulation
	 */
	private void addAgentsBarrel() {
		List<Beer> beerList = Arrays.asList(Beer.values());
		for(int i = 0; i < Constant.BARREL_POSITIONS.length; i++) {
			Barrel b = new Barrel(beerList.get(i));
			Int2D pos = Constant.BARREL_POSITIONS[i];
			pic.setObjectLocation(b, pos.getX(), pos.getY());
			barrels.put(b, pos);
		}
	}

	/**
	 * Ajoute les fûts de bière à la simulation
	 */
	private void addAgentsBartenderAndWaitingLine() {
		for(int i = 0; i < Constant.BARTENDER_POSITIONS.length; i++) {
			Int2D pos = Constant.BARTENDER_POSITIONS[i];
			WaitingLine w = new WaitingLine();
			Bartender b = new Bartender(w, 10, 10, 10, pos);
			pic.setObjectLocation(b, pos.getX(), pos.getY());
			pic.setObjectLocation(w, pos.getX(), pos.getY()+2);

		}
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
    	for(int i = 0; i < Constant.STUDENT_NUMBER; ++i) {
    		schedule.scheduleRepeating(new Student(10));
    	}
    }
    
    /**
     * Ajoute l'horloge représentant "l'heure réelle" à la simulation
     */
    private void addClock() {
    	schedule.scheduleRepeating(new Clock());
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

    public CheckoutCounter getCheckoutCounter() {
    	return cc;
	}

	public Int2D getCheckoutCounterLocation() {
    	return pic.getObjectLocation(cc);
	}
}
