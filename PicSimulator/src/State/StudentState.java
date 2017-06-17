package State;

/**
 * Représente les différents états d'un étudiant
 */
public enum StudentState {
	/**
	 * L'étudiant ne fait rien de spécial, il doit prendre une décision
	 */
	NOTHING,
	/**
	 * L'étudiant est dans la file d'attente pour se faire servir une bière
	 */
	WAITING_IN_QUEUE,
	/**
	 * L'étudiant est en train d'être servi
	 */
	WAITING_FOR_BEER,
	/**
	 * L'étudiant est en train de se déplacer
	 */
	WALKING,
	/**
	 * L'étudiant se déplace vers la file d'attente LA PLUS VIDE
	 */
	WALKING_TO_WAITING_LINE,
	/**
	 * L'étudiant se déplace vers une file d'attente quelconque en attendant de choisir la plus vide
	 */
	CHOOSING_WAITING_LINE,
	/**
	 * L'étudiant se dirige vers la sortie
	 */
	WALKING_TO_EXIT,
	/**
	 * L'étudiant est pauvre et n'a pas assez d'argent pour une bière
	 */
	POOR,
	/**
	 * L'étudiant n'est pas dans le Pic
	 */
	OUTSIDE,
	/**
	 * L'étudiant boit sa bière sur place avec ses amis
	 */
	DRINKING_WITH_FRIENDS

}
