package game.enums;

import org.joml.Vector3f;

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
	
	public static Direction getRotatedDirection(Direction currentDirection, Direction directionToRotateIn)
	{
		switch(currentDirection)
		{
			case LEFT:
			
		}
		return null;
	}
	
	public static Vector3f getRotationVector(Direction direction)
	{
		switch(direction)
		{
			case FORWARDS:
				return new Vector3f(0, 0, 0);
			//TODO Finish writing method
		}
		
		return null;
	}
}

