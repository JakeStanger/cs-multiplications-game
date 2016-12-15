package game.items;

import engine.MouseInput;
import engine.Utils;
import engine.Window;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.loaders.obj.OBJLoader;
import game.GameLogic;
import game.Main;
import game.enums.Direction;
import game.scenes.Game;
import game.scenes.Maths;
import game.wrappers.TurnPoint;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static game.scenes.Game.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jake stanger
 * The head of the snake. Contains snake logic.
 */
public class SnakeHead extends SnakePiece
{
	static final float REFLECTANCE = 0.7f, SNAKE_STEP = 0.05f;
	
	private List<SnakeTail> tailList;
	
	/**
	 * A list of points where
	 */
	private List<TurnPoint> turnPoints;
	
	public SnakeHead(Vector3f position) throws Exception
	{
		this.ID = 0; //Head ID is always 0
		
		Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
		
		Material material = new Material(new Texture("/textures/green.png"), REFLECTANCE);
		mesh.setMaterial(material);
		
		this.setMesh(mesh);
		
		this.turnPoints = new ArrayList<>();
		
		this.direction = Direction.FORWARDS; //Starting direction
		
		//Add starting position
		this.setPosition(position.x, position.y, position.z);
		turnPoints.add(new TurnPoint(this.direction, position));
		
		this.tailList = new ArrayList<>();
	}
	
	public void input(Window window, MouseInput mouseInput)
	{
		if (window.isKeyPressed(GLFW_KEY_UP)) this.addTurnPoint(Direction.FORWARDS);
		else if (window.isKeyPressed(GLFW_KEY_DOWN)) this.addTurnPoint(Direction.BACKWARDS);
		
		if (window.isKeyPressed(GLFW_KEY_LEFT)) this.addTurnPoint(Direction.LEFT);
		else if (window.isKeyPressed(GLFW_KEY_RIGHT)) this.addTurnPoint(Direction.RIGHT);
		
		if (window.isKeyPressed(GLFW_KEY_RIGHT_CONTROL)) this.addTurnPoint(Direction.DOWN);
		else if (window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) this.addTurnPoint(Direction.UP);
	}
	
	private void addTurnPoint(Direction direction)
	{
		Direction lastDirection = this.turnPoints.get(this.turnPoints.size()-1).getDirection();
		if(direction != lastDirection && direction != Direction.getOppositeDirection(this.direction))
			this.turnPoints.add(new TurnPoint(direction, Utils.getNextRoundedVector(this.getPosition(), this.direction, GRID_SIZE)));
	}
	
	public void update()
	{
		super.update();
		this.collisionCheck();
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
			//Game.incrementScore();
			
			//Add next tail piece
			for(SnakeTail tail : this.tailList) if(!tail.isVisible())
			{
				tail.setVisible(true);
				tail.setPopIn(true);
				break;
			}
			
			//Ask maths question
			try
			{
				((GameLogic)Main.getGameLogic()).setScene(new Maths(), Main.getGameEngine().getWindow());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void collisionCheckSnake()
	{
		for(SnakeTail tail : this.tailList)
			if (Utils.areVectorsInRange(this.getPosition(), tail.getPosition(),
					GRID_SIZE * SNAKE_TAIL_SCALE))
				//We can safely assume we never crash into the head first tail piece
				if (tail.getID() > 1 && tail.isVisible()) Game.setRunning(false);
	}
	
	private void collisionCheckWall()
	{
		if(this.getPosition().x <= 0 || this.getPosition().y <= 0 || this.getPosition().z >= 0 ||
				this.getPosition().x >= MAP_SIZE || this.getPosition().y >= MAP_SIZE || this.getPosition().z <= -MAP_SIZE)
			Game.setRunning(false); //^^z-axis is inverted^^
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
