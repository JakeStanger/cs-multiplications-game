package game.wrappers;

import game.enums.Direction;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jake stanger
 * A position in the map at which the snake is to next turn
 */
public class TurnPoint
{
	private Direction direction;
	private Vector3f position;
	
	private List<Integer> tailsVisited;
	
	public TurnPoint(Direction direction, Vector3f position)
	{
		this.direction = direction;
		this.position = new Vector3f(position);
		
		this.tailsVisited = new ArrayList<>();
	}
	
	public Direction getDirection()
	{
		return direction;
	}
	
	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public void setPosition(Vector3f position)
	{
		this.position = position;
	}
	
	public void addPieceToVisitedList(int tailID)
	{
		this.tailsVisited.add(tailID);
	}
	
	public List<Integer> getTailsVisited()
	{
		return this.tailsVisited;
	}
	
	@Override
	public String toString()
	{
		return this.direction.name() + ": " + this.position;
	}
}
