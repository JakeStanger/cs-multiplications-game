package game.items;

import engine.Utils;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import engine.loaders.obj.OBJLoader;
import game.Game;
import game.enums.Direction;
import game.wrappers.TurnPoint;

import java.util.List;

import static game.Game.SNAKE_TAIL_SCALE;
import static game.items.SnakeHead.REFLECTANCE;
import static game.items.SnakeHead.SNAKE_STEP;

/**
 * @author Jake stanger
 * A tail piece of the snake, which follows the head.
 */
public class SnakeTail extends GameItem
{
	private static final float ANIM_MULTIPLIER = 0.05f;
	
	private int tailID;
	
	private Direction direction;
	
	private boolean visible;
	
	/**
	 * When popIn is true, an
	 * animation plays causing the
	 * tail piece to increase in size.
	 */
	private boolean popIn;
	
	public SnakeTail(int tailID) throws Exception
	{
		this.tailID = tailID;
		
		Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
		
		Material material = new Material(new Texture("/textures/blue.png"), REFLECTANCE);
		mesh.setMaterial(material);
		
		this.setMesh(mesh);
		
		this.direction = Game.snakeHead.getCurrentDirection();
	}
	
	public void update()
	{
		this.move();
		if(this.popIn) this.popIn();
	}
	
	public void move()
	{
		TurnPoint turnPoint = this.getFirstUnvisitedTurnPoint();
		if(turnPoint != null)
		{
			this.step(direction);
			if (Utils.areVectorsEqual(this.getPosition(), turnPoint.getPosition())) turnPoint.addTailToVisitedList(this.tailID);
		}
		else if(Game.snakeHead.getCurrentDirection() != null) step(Game.snakeHead.getCurrentDirection());
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
			if(!(tailsVisited.contains(this.tailID)))
			{
				if(i-1 >= 0) this.direction = turnPoints.get(i-1).getDirection();
				return turnPoint;
			}
		}
		
		return null;
	}
	
	private void popIn()
	{
		final float delta = 0.01f;
		
		float scale = this.getScale();
		if(scale < SNAKE_TAIL_SCALE) this.setScale(scale + (float) (Math.sin(SNAKE_TAIL_SCALE - scale)) * ANIM_MULTIPLIER);
		if(this.getScale() > SNAKE_TAIL_SCALE - delta)
		{
			this.setScale(SNAKE_TAIL_SCALE); //Round off
			this.setPopIn(false);
		}
	}
	
	public boolean isVisible()
	{
		return visible;
	}
	
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	public void setPopIn(boolean shouldPopIn)
	{
		this.popIn = shouldPopIn;
	}
	
	public int getTailID()
	{
		return tailID;
	}
}

