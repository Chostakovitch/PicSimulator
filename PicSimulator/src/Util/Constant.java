package Util;

import java.time.LocalTime;

import sim.util.Int2D;

/**
 * Constantes paramétrant la simulation et de l'affichage
 */
public class Constant {
	/**
	 * Largeur du Pic
	 */
    public static int PIC_WIDTH = 30;
    
    /**
     * Hauteur du Pic
     */
    public static int PIC_HEIGHT = 30;

    /**
     * Position des murs du Pic
     */
    public static Int2D PIC_WALLS[][] = {
            // Murs extérieurs
            {new Int2D(4, 4), new Int2D(4, 8)},
            {new Int2D(4, 11), new Int2D(4, 20)},
            {new Int2D(4, 24), new Int2D(4, 25)},


            {new Int2D(5, 4), new Int2D(28, 4)},
            {new Int2D(5, 25), new Int2D(28, 25)},

            {new Int2D(28, 5), new Int2D(28, 7)},
            {new Int2D(28, 11), new Int2D(28, 19)},
            {new Int2D(28, 23), new Int2D(28, 25)},


            // Murs intérieurs
            {new Int2D(4, 8), new Int2D(8, 8)},
            {new Int2D(12, 8), new Int2D(16, 8)},

            {new Int2D(16, 4), new Int2D(16, 8)},
            {new Int2D(10, 4), new Int2D(10, 8)},

            {new Int2D(4, 11), new Int2D(14, 11)},
            {new Int2D(16, 11), new Int2D(16, 15)},
            {new Int2D(12, 15), new Int2D(16, 15)},
            {new Int2D(12, 11), new Int2D(12, 15)},
            {new Int2D(4, 15), new Int2D(10, 15)},

    };

    public static Int2D PIC_BAR_COUNTER[][] = {
            {new Int2D(5, 19), new Int2D(16, 19)},
            {new Int2D(16, 16), new Int2D(16, 18)},

    };

    /**
     * Capacité d'un fût de bière (en litres)
     */
    public static int BARREL_CAPACITY = 30;

    /**
     * Position des fûts de bière
     */
    public static Int2D BARREL_POSITIONS[] = {
            new Int2D(6, 16),
            new Int2D(8, 16),
            new Int2D(10, 16),
            new Int2D(12, 16),
            new Int2D(14, 16),
    };

    /**
     * Position des files d'attentes
     */
    public static Int2D BARTENDER_POSITIONS[] = {
            new Int2D(5, 18),
            new Int2D(6, 18),
            new Int2D(7, 18),
            new Int2D(8, 18),
            new Int2D(9, 18),
            new Int2D(10, 18),
            new Int2D(11, 18),
            new Int2D(12, 18),
            new Int2D(13, 18),
            new Int2D(14, 18),
            new Int2D(15, 18),
    };
    
    /**
     * Nombre maximum d'étudiants sur une case
     */
    public static int MAX_STUDENT_PER_CELL = 10;
    
    /**
     * Capacité d'une ecocup (en centilitres)
     */
    public static int CUP_CAPACITY = 33;
    
    /**
     * Nombre de permanenciers dans le pic
     */
    public static int BARTENDER_NUMBER = 5;
    
    /**
     * Largeur de la console d'affichage
     */
    public static int FRAME_WIDTH = 810;
    
    /**
     * Hauteur de la console d'affichage
     */
    public static int FRAME_HEIGHT = 810;
    
    /**
     * Heure d'ouverture du Pic
     */
    public static LocalTime PIC_BEGIN = LocalTime.of(10, 0);
    
    /**
     * Heure de fermeture du Pic
     */
    public static LocalTime PIC_END = LocalTime.of(22, 30);
    
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
    public static int TIMESTEP = 5;
    
    /**
     * Nombre d'étudiants participant à la simulation (!= nombre d'étudiants dans le pic)
     */
    public static int STUDENT_NUMBER = 100;
    
    /**
     * Position de l'entrée du Pic
     */
    public static Int2D PIC_ENTER = new Int2D(0, 0);
    
    /**
     * Distance à laquelle un étudiant peut se déplacer (au maximum) pendant une itération
     */
    public static int STUDENT_WALK_CAPACITY = 3;
}