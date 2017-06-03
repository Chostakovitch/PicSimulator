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
     * Capacité d'un fût de bière (en litres)
     */
    public static int BARREL_CAPACITY = 30;
    
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
    public static int FRAME_WIDTH = 800;
    
    /**
     * Hauteur de la console d'affichage
     */
    public static int FRAME_HEIGHT = 800;
    
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
}