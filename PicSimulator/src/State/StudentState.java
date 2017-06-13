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
	 * L'étudiant se déplace vers une file d'attente
	 */
	WALKING_TO_WAITING_LINE,
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
	OUTSIDE;
}
