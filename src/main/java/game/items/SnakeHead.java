package game.items;

import engine.MouseInput;
import engine.Timer;
import engine.Utils;
import engine.Window;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import engine.loaders.obj.OBJLoader;
import game.Game;
import game.enums.Direction;
import game.wrappers.TurnPoint;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static game.Game.GRID_SIZE;
import static game.Game.FOOD_SCALE;
import static game.Game.SNAKE_TAIL_SCALE;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jake stanger
 * The head of the snake. Contains snake logic.
 */
public class SnakeHead extends GameItem
{
	public static final float REFLECTANCE = 0.7f, SNAKE_STEP = 0.05f;
	private static final double COOLDOWN_TIME = 0.2;
	
	public Direction getCurrentDirection()
	{
		return currentDirection;
	}
	
	private Direction currentDirection, nextDirection;
	
	private List<SnakeTail> tailList;
	
	/**
	 * A list of points where
	 */
	private List<TurnPoint> turnPoints;
	
	private double cooldownBegin;
	private Timer timer;
	
	public SnakeHead(Vector3f position) throws Exception
	{
		Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
		
		Material material = new Material(new Texture("/textures/green.png"), REFLECTANCE);
		mesh.setMaterial(material);
		
		this.setMesh(mesh);
		
		this.turnPoints = new ArrayList<>();
		
		this.currentDirection = Direction.FORWARDS; //Starting direction
		this.nextDirection = currentDirection;
		
		//Add starting position
		this.setPosition(position.x, position.y, position.z);
		turnPoints.add(new TurnPoint(this.currentDirection, new Vector3f(position)));
		
		this.tailList = new ArrayList<>();
	}
	
	public void input(Window window, MouseInput mouseInput)
	{
		if(this.timer == null) this.timer = new Timer();
		double elapsedTime = this.timer.getTime() - this.cooldownBegin;
		
		if(elapsedTime > COOLDOWN_TIME)
		{
			Direction prevDirection = this.currentDirection;
			
			if (window.isKeyPressed(GLFW_KEY_UP)) this.setNextDirection(Direction.FORWARDS);
			else if (window.isKeyPressed(GLFW_KEY_DOWN)) this.setNextDirection(Direction.BACKWARDS);
			
			if (window.isKeyPressed(GLFW_KEY_LEFT)) this.setNextDirection(Direction.LEFT);
			else if (window.isKeyPressed(GLFW_KEY_RIGHT)) this.setNextDirection(Direction.RIGHT);
			
			if (window.isKeyPressed(GLFW_KEY_RIGHT_CONTROL)) this.setNextDirection(Direction.DOWN);
			else if (window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) this.setNextDirection(Direction.UP);
			
			//Check if we have changed direction
			if (this.currentDirection != prevDirection) this.cooldownBegin = this.timer.getTime();
		}
	}
	
	public void setNextDirection(Direction direction)
	{
		if(this.currentDirection != direction && direction != Direction.getOppositeDirection(this.currentDirection))
		{
			this.nextDirection = direction;
			
			//if(this.turnPoints.size() > 0 && this.turnPoints.get(0).getPosition() == this.getPosition()) this.turnPoints.remove(0);
			this.turnPoints.add(new TurnPoint(direction, Utils.getNextRoundedVector(this.getPosition(), direction, GRID_SIZE)));
		}
	}
	
	public void move()
	{
		if(Utils.areVectorsEqual(this.getPosition(), Utils.getNextRoundedVector(this.getPosition(), this.currentDirection, GRID_SIZE)))
			this.currentDirection = this.nextDirection;
		
		switch (currentDirection)
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
	
	public void update()
	{
		this.move();
		//if(this.turnPoints.size() == 0) this.turnPoints.add(new TurnPoint(this.currentDirection,
		//		Utils.getNextRoundedVector(this.getPosition(), this.currentDirection, GRID_SIZE)));
		
		this.collisionCheck();
		
		System.out.println(this.turnPoints);
	}
	
	private void collisionCheck()
	{
		this.collisionCheckFood();
		this.collisionCheckSnake();
		this.collisionCheckWall();
	}
	
	private void collisionCheckFood()
	{
		boolean inRange = Utils.areVectorsInRange(this.getPosition(), Game.food.getPosition(),
				GRID_SIZE * FOOD_SCALE);
		
		if(inRange)
		{
			Game.food.randomlyPlaceOnMap();
			Game.incrementScore();
			
			//Add next tail piece
			for(SnakeTail tail : this.tailList) if(!tail.isVisible())
			{
				tail.setVisible(true);
				tail.setPopIn(true);
				break;
			}
		}
	}
	
	private void collisionCheckSnake()
	{
		for(SnakeTail tail : this.tailList)
			if (Utils.areVectorsInRange(this.getPosition(), tail.getPosition(),
					GRID_SIZE * SNAKE_TAIL_SCALE))
				//We can assume we never crash into the first tail piece
				if (tail.getTailID() != 0 && tail.isVisible()) Game.setRunning(false);
	}
	
	private void collisionCheckWall()
	{
		//TODO Write method
	}
	
	public void addTailToList(SnakeTail tail)
	{
		this.tailList.add(tail);
	}
	
	public List<SnakeTail> getTailList()
	{
		return this.tailList;
	}
	
	public List<TurnPoint> getTurnPoints()
	{
		return turnPoints;
	}
	
}
