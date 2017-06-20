package Util;

import java.time.LocalDate;
import java.time.LocalTime;

import sim.util.Int2D;

/**
 * Constantes paramétrant la simulation et de l'affichage
 */
public class Constant {

	/**
     * Date de la simulation
     */
    public static LocalDate DATE = LocalDate.of(2017, 03, 10);
	/**
	 * Largeur du Pic
	 */
    public static int PIC_WIDTH = 35;
    
    /**
     * Hauteur du Pic
     */
    public static int PIC_HEIGHT = 26;
    
    public static int PIC_MAP[][] = new int[][] 
    {
    	{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    	{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    	{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    	{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    	{ 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 },
    	{ 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
    	{ 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
    	{ 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 5, 6, 6, 6, 6, 6, 0, 6, 6, 6, 7, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
	    { 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 }
    };

    /**
     * Capacité d'un fût de bière (en litres)
     */
    public static int BARREL_CAPACITY = 3000;

    /**
     * Position de la sortie au Pic
     */
    public static Int2D EXIT_POSITION = new Int2D(0, 0);
    
    /**
     * Nombre maximum d'étudiants sur une case
     */
    public static int MAX_STUDENT_PER_CELL = 10;
    
    /**
     * Capacité d'une ecocup (en centilitres)
     */
    public static int CUP_CAPACITY = 33;
    
    /**
     * Largeur de la console d'affichage
     */
    public static int FRAME_WIDTH = 1000;
    
    /**
     * Hauteur de la console d'affichage
     */
    public static int FRAME_HEIGHT = (int)(1000 * ((double)PIC_HEIGHT / (double)PIC_WIDTH));
    
    /**
     * Heure d'ouverture du Pic
     */
    public static LocalTime PIC_BEGIN = LocalTime.of(18, 30);
    
    /**
     * Heure de fermeture du Pic
     */
    public static LocalTime PIC_END = LocalTime.of(22, 00);
    
    /**
     * Heure de début d'exploitation de la licence II du Pic
     */
    public static LocalTime PIC_BEER_BEGIN = LocalTime.of(18, 30);
    
    /**
     * Heure de fin d'exploitation de la licence II du Pic
     */
    public static LocalTime PIC_BEER_END = LocalTime.of(21, 30);	
    
    /**
     * Temps, en secondes, concerné par une itération de la simulation
     */
    public static int TIMESTEP = 1;
    
    /**
     * Nombre d'euros par recharge, par défaut
     */
    public static int RECHARGE_AMOUNT = 5;
    
    /**
     * Balance intiale d'un compte banquaire d'étudiant par défaut
     */
    public static int BANK_INITIAL_BALANCE = 5;
    
    /**
     * Position de l'entrée du Pic
     */
    public static Int2D PIC_ENTER = new Int2D(0, 0);
    
    /**
     * Distance à laquelle un étudiant peut se déplacer (au maximum) pendant une itération
     */
    public static int STUDENT_WALK_CAPACITY = 1;
    
    /**
     * Nombre de centilitres que l'étudiant boit par gorgée
     */
    public static int STUDENT_SWALLOW_CAPACITY = 3;
    
    /**
     * Secondes que prend un permanencier pour servir
     */
    public static int BARTENDER_TIME_TO_SERVE = 5;

    /**
     * Temps pour réparer un fût
     */
    public static int BARTENDER_TIME_TO_FIXE_BARREL = 120;

    /**
     * Secondes que prend un permanencier pour reremplir un fût
     */
    public static int BARTENDER_TIME_TO_FILL = 30;
    
    /**
     * Secondes que prend un permanencier pour utiliser la caisse
     */
    public static int BARTENDER_TIME_TO_CHECKOUT = 2;

    /**
     * Préférence de bière : j'adore
     */
    public static int LOVE_GRADE = 5; 
    /**
     * Préférence de bière : ça va
     */
    public static int NOT_BAD_GRADE = 2;
    /**
     * Préférence de bière : jamais goûté
     */
    public static int NEVER_TASTE_GRADE = 0; 
    /**
     * Préférence de bière : bof
     */
    public static int AVERAGE_GRADE = -2; 
    /**
     * Préférence de bière : je déteste
     */
    public static int HATE_GRADE = -5; 
    
    /**
     * Taux d'alcool d'une bière.
     */
    public static double BEER_AlCOHOL_LEVEL = 0.25;
    
    /**
     * Taux d'alcool éliminé en une heure
     */
    public static double STUDENT_ALCOHOL_ELIMINATION_PER_HOUR = 0.15;
    
    /**
     * Nombre de minutes avant la fermeture à partir duquel les étudiants sortent
     */
    public static int PIC_DELTA_TO_LEAVE = 5;
}