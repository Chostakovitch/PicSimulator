package Agent;

import Enum.WallStatus;

/**
 * Représente un mur du Pic.
 */
public class Wall implements Inanimate, Invalid {
	/**
	 * Configuration du mur dans l'espace
	 */
	private WallStatus configuration;

	public Wall(WallStatus configuration) {
		this.configuration = configuration;
	}

	public WallStatus getConfiguration() {
		return configuration;
	}
}
