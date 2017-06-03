package Model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.threeten.extra.Interval;

import Agent.Clock;
import Agent.Student;
import Util.Constant;
import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
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
	
    public Pic(long seed) {
    	super(seed);
    	timeslot = Interval.of(getUTCInstant(Constant.PIC_BEGIN), getUTCInstant(Constant.PIC_END));
    	beerTimeslot = Interval.of(getUTCInstant(Constant.PIC_BEER_BEGIN), getUTCInstant(Constant.PIC_BEER_END));
    	//On considère que la simulation commence à l'ouverture du Pic
    	time = timeslot.getStart();
    	
    	studentsInside = 0;
    }

    @Override
	public void start() {
        System.out.println("GOD DAMN SIMULATION STARTED");
        super.start();
        pic.clear();
        
        //Ajout des agents
        addAgentsStudent();
        addAgentsBartender();
        
        //Ajout de l'horloge
        addClock();
    }
    
    @Override
    public void finish() {
    	super.finish();
    	//Remise à zéro du temps
    	time = timeslot.getStart();
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
     * Indique si une position est "valide", au sens des délimitations virtuelles de la grille (qui n'est pas bornée)
     * @param pos Coordonnées
     * @return true si la position est dans les délimitations virtuelles de la grille
     */
    public boolean isLocationValid(Int2D pos) {
    	int x = pos.x;
    	int y = pos.y;
    	return  x >= 0 && y >= 0 && x < pic.getWidth() && y < pic.getHeight();
    }
    
    /**
     * Indique si une position est "valide", au sens des délimitations virtuelles de la grille (qui n'est pas bornée)
     * @param x Coordonnée en abscisses
     * @param y Coordonnéé en ordonnées
     * @return true si la position est dans les délimitations virtuelles de la grille
     */
    public boolean isLocationValid(int x, int y) {
    	return  x >= 0 && y >= 0 && x < pic.getWidth() && y < pic.getHeight();
    }
    
    /**
     * Calcule un ensemble de position valides autour d'une position initiale,
     * sous forme d'un carré plein paramétré par un "rayon" maximal (une diagonale valant une unité)
     * @param pos Position centrale
     * @param radius "Rayon" du carré
     * @return Liste de coordonnées valides
     */
    public List<Int2D> getSquareValidLocations(Int2D pos, int radius) {
    	List<Int2D> possiblePos = new ArrayList<>();
    	int x = pos.x;
    	int y = pos.y;
    	for(int i = x - 1; i <= x + 1; ++i) {
    		for(int j = y - 1; j <= y + 1; ++j) {
    			//On ne prend pas en compte la position centrale
    			if(!(i == x && j == y) && isLocationValid(i, j)) {
    				possiblePos.add(new Int2D(i, j));
    			}
    		}
    	}
    	return possiblePos;
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
     * Ajoute les permanenciers à la simulation
     */
    private void addAgentsBartender() {
    	
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
    		schedule.scheduleRepeating(new Student());
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
}
