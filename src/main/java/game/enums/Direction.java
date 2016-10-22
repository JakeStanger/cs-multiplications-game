package game.enums;

/**
 * @author Jake stanger
 * A direction in the world.
 */
public enum Direction
{
	UP, DOWN, LEFT, RIGHT, FORWARDS, BACKWARDS;
	
	public static Direction getOppositeDirection(Direction direction)
	{
		switch(direction)
		{
			case UP:
				return DOWN;
			case DOWN:
				return UP;
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			case FORWARDS:
				return BACKWARDS;
			case BACKWARDS:
				return FORWARDS;
			default:
				return FORWARDS;
		}
	}
}

