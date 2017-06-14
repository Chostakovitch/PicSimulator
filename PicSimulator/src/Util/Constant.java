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
    public static int PIC_WIDTH = 35;
    
    /**
     * Hauteur du Pic
     */
    public static int PIC_HEIGHT = 26;

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
        new Int2D(6, 16),
        new Int2D(7, 16),
        new Int2D(7, 16),
        new Int2D(8, 16),
        new Int2D(8, 16),
        new Int2D(9, 16),
        new Int2D(9, 16),
        new Int2D(10, 16),
        new Int2D(10, 16),
        new Int2D(12, 16),
        new Int2D(12, 16),
        new Int2D(13, 16),
        new Int2D(13, 16),
        new Int2D(14, 16),
        new Int2D(14, 16),
    };

    /**
     * Position des permanenciers
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
        new Int2D(15, 18)
    };
    
    /**
     * Positions des files d'attente
     */
    public static Int2D WAITING_LINES_POSITIONS[] = {
        new Int2D(5, 20),
        new Int2D(6, 20),
        new Int2D(7, 20),
        new Int2D(8, 20),
        new Int2D(9, 20),
        new Int2D(10, 20),
        new Int2D(11, 20),
        new Int2D(12, 20),
        new Int2D(13, 20),
        new Int2D(14, 20),
        new Int2D(15, 20)
    };
    
    public static Int2D EXIT_POSITION = new Int2D(0, 0);

    public static Int2D PIC_CHECKOUT_COUNTER_POSITION = new Int2D(15, 16);
    
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
    public static int TIMESTEP = 1;
    
    /**
     * Nombre d'étudiants participant à la simulation (!= nombre d'étudiants dans le pic)
     */
    public static int STUDENT_NUMBER = 30;
    
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
    public static int STUDENT_WALK_CAPACITY = TIMESTEP;
    
    /**
     * Nombre de centilitres que l'étudiant boit par gorgée
     */
    public static int STUDENT_SWALLOW_CAPACITY = 3;
    
    /**
     * Secondes que prend un permanencier pour prendre une commande 
     */
    public static int BARTENDER_TIME_TO_SERVE = 5;
    
    /**
     * Secondes que prend un permanencier pour remplir un verre
     */
    public static int BARTENDER_TIME_TO_FILL = 3;
    
    /**
     * Secondes que prend un permanencier pour utiliser la caisse
     */
    public static int BARTENDER_TIME_TO_CHECKOUT = 2;

    /**
     * Préférence des bières, de j'adore à je deteste.
     */
    public static int LOVE_GRADE = 5; //J'adore
    public static int NOT_BAD_GRADE = 2; //ça va
    public static int NEVER_TASTE_GRADE = 0; //Jamais gouté
    public static int AVERAGE_GRADE = -2; //bof
    public static int HATE_GRADE = -5; //je deteste
}