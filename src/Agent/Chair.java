package Agent;

import Enum.Direction;

/**
 * Représente une chaise accessible par les agents.
 */
public class Chair implements Inanimate {
	private Direction direction;

	public Chair(Direction direction) {
		this.direction = direction;
	}

	public Direction getDirection() {
		return direction;
	}
}
