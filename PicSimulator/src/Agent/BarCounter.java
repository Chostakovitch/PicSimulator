package Agent;

import Enum.WallStatus;

public class BarCounter implements Inanimate, Invalid {
	/**
	 * Configuration du mur dans l'espace
	 */
	private WallStatus configuration;

	public BarCounter(WallStatus configuration) {
		this.configuration = configuration;
	}

	public WallStatus getConfiguration() {
		return configuration;
	}
}
