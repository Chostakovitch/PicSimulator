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
	public static final double STUDENT_ENTER_PIC_WITHIN_INTERVAL = 0.9;
	
	/**
	 * Probabilité que l'étudiant vienne en dehors de l'intervalle où il se rend au Pic en général 
	 */
	public static final double STUDENT_ENTER_PIC_OUTSIDE_INTERVAL = 0.05;
	
	/**
	 * Facteur pour les probabilités que l'étudiant rerentre dans le Pic une fois parti
	 */
	public static final double STUDENT_REENTER_FACTOR = 0.001;
}
