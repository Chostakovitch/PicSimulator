package Util;

/**
 * Cette classe contient les différentes probabilités
 * utilisées au sein de la simulation. Il est possible 
 * de les faire varier pour adapter les comportements.
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
}