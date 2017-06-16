package State;

/**
 * Représente les différents états d'un permanencier
 */
public enum BartenderState {
	/**
	 * En attente d'un événement
	 */
	NOTHING,
	/**
	 * En attente de la caisse (file de permanenciers)
	 */
	WAITING_CHECKOUT,
	/**
	 * Utilise la caisse
	 */
	USING_CHECKOUT,
	/**
	 * En attente d'un fût libre (file de permanenciers)
	 */
	WAITING_BARREL,
	/**
	 * Utilisation d'un fût (remplissage d'une cup)
	 */
	USING_BARREL,
	/**
	 * Remplissage d'un fût
	 */
	REFILLING_BARREL,
	/**
	 * Réparation d'un fût
	 */
	FIXING_BARREL;
}
