package Util;

/**
 * Cette classe contient les différentes probabilités
 * utilisées au sein de la simulation. Il est possible 
 * de les faire varier pour adapter les comportements.
 * 
 * Note : un facteur de saturation correspond à l'idée 
 * que si la proba est testée à toutes les itérations,
 * elle doit être très faible pour résister à la masse de tests.
 * Elle est donc divisée par ce facteur qui correspond au nombre de
 * seconde où elle devrait être réellement testée.
 */
public class Probability {
	/**
	 * Probabilité que l'étudiant vienne pendant l'intervalle où il se rend au Pic en général
	 */
	public static final double STUDENT_ENTER_PIC_WITHIN_INTERVAL = 0.7;
	
	/**
	 * Probabilité que l'étudiant vienne en dehors de l'intervalle où il se rend au Pic en général 
	 */
	public static final double STUDENT_ENTER_PIC_OUTSIDE_INTERVAL = 0.05;
	
	/**
	 * 5 minutes en moyenne pour tester réellement la ré-entrée 
	 */
	public static final int STUDENT_REENTER_SATURATION = 300;
	
	/**
	 * 5 minutes en moyenne pour tester réellement la sortie 
	 */
	public static final int STUDENT_LEAVE_SATURATION = 300;
	
	/**
	 * 5 minutes en moyenne pour tester réellement le fait de marcher 
	 */
	public static final int STUDENT_WALK_SATURATION = 300;
	
	/**
	 * Facteur pour les probabilités que l'étudiant rerentre dans le Pic une fois parti
	 */
	public static final double STUDENT_REENTER_FACTOR = 0.01;
	
	/**
	 * Facteur pour les probabilités que l'étudiant rerentre dans le Pic une fois parti et pauvre
	 */
	public static final double STUDENT_REENTER_POOR_FACTOR = STUDENT_REENTER_FACTOR / 10;
	
	/**
	 * Probabilité que l'étudiant reste plus tard que prévu au Pic
	 */
	public static final double STUDENT_STAY_LATER = 0.2;
	
	/**
	 * Probabilité que l'étudiant parte quand il est pauvre
	 */
	public static final double STUDENT_LEAVE_POOR = 0.7;
	
	/**
	 * Probabilité que l'étudiant parte quand il a fini de boire
	 */
	public static final double STUDENT_LEAVE_NO_MORE_DRINK = 0.3;
	
	/**
	 * Probabilité que l'étudiant parte quand l'heure à laquelle il devait partir est passée
	 */
	public static final double STUDENT_LEAVE_HOUR_PAST = 0.9;
	
	/**
	 * Probabilité que l'étudiant parte quand l'heure est passée mais qu'il reste un peu comme un sagouin
	 */
	public static final double STUDENT_LEAVE_HOUR_PAST_BUT_ANYWAY = 0.7;
	
	/**
	 * Probabilité que l'étudiant marche quand il ne fait rien
	 */
	public static final double STUDENT_WALK_WHEN_NOTHING = 0.9;
	
	/**
	 * Probabilité que l'étudiant se barre alors qu'il parle avec des amis
	 */
	public static final double STUDENT_WALK_WHEN_TALKING_WITH_FRIENDS = 0.3;
	
	/**
	 * Probabilité que lorsque l'étudiant se déplace, il aille vers des amis
	 */
	public static final double STUDENT_WALK_TO_FRIEND = 0.6;
	
	/**
	 * Probabilité que l'étudiant parte quand il est ivre
	 */
	public static final double STUDENT_LEAVE_DRUNK = 0.3;
	
	/**
	 * Probabilité qu'un étudiant ivre commande une nouvelle bière
	 */
	public static final double STUDENT_ORDER_IF_DRUNK = 0.6;
	
	/**
	 * Probabilité qu'un étudiant renverse sa bière
	 */
	public static final double STUDENT_SPILL_HIS_BEER = 0.05;
	
	/**
	 * Probabilité qu'un étudiant reprenne une bière s'il a fini sa moyenne  
	 */
	public static final double STUDENT_ORDER_IF_NO_MORE_BEERS = 0.3;
	
	/**
	 * 5 minutes en moyenne pour tester réellement la prise de bières
	 */
	public static final double STUDENT_ORDER_SATURATION = 300;
	
	/**
	 * Probabilité pour que l'étudiant change de direction physique
	 */
	public static final double STUDENT_CHANGE_DIRECTION = 0.4;
	
	/**
	 * 30 secondes en moyenne pour changer de direction
	 */
	public static final double STUDENT_DIRECTION_SATURATION = 30;
}