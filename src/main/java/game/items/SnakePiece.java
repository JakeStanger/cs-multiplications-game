package game.items;

import engine.Utils;
import engine.items.GameItem;
import game.Game;
import game.enums.Direction;
import game.wrappers.TurnPoint;

import java.util.List;

import static game.items.SnakeHead.SNAKE_STEP;

/**
 * @author Jake stanger
 * A section of snake
 */
public class SnakePiece extends GameItem
{
	int ID;
	
	protected Direction direction;
	
	private void move()
	{
		TurnPoint turnPoint = this.getFirstUnvisitedTurnPoint();
		if(turnPoint != null)
		{
			if (Utils.areVectorsEqual(this.getPosition(), turnPoint.getPosition()))
			{
				turnPoint.addPieceToVisitedList(this.ID);
				this.direction = turnPoint.getDirection();
			}
			
			this.step(direction);
		}
		else if(Game.snakeHead.getDirection() != null) step(Game.snakeHead.getDirection());
	}
	
	private void step(Direction direction)
	{
		switch (direction)
		{
			case FORWARDS:
				this.getPosition().z -= SNAKE_STEP;
				break;
			case BACKWARDS:
				this.getPosition().z += SNAKE_STEP;
				break;
			case LEFT:
				this.getPosition().x -= SNAKE_STEP;
				break;
			case RIGHT:
				this.getPosition().x += SNAKE_STEP;
				break;
			case UP:
				this.getPosition().y += SNAKE_STEP;
				break;
			case DOWN:
				this.getPosition().y -= SNAKE_STEP;
				break;
		}
	}
	
	private TurnPoint getFirstUnvisitedTurnPoint()
	{
		List<TurnPoint> turnPoints = Game.snakeHead.getTurnPoints();
		for(int i = 0; i < turnPoints.size(); i++)
		{
			TurnPoint turnPoint = turnPoints.get(i);
			
			List<Integer> tailsVisited = turnPoint.getTailsVisited();
			if(!(tailsVisited.contains(this.ID)))
			{
				if(i-1 >= 0) this.direction = turnPoints.get(i-1).getDirection();
				return turnPoint;
			}
		}
		
		return null;
	}
	
	protected void update()
	{
		this.move();
	}
	
	int getID()
	{
		return ID;
	}
	
	public Direction getDirection()
	{
		return direction;
	}
}
