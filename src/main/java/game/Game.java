package game;

import engine.*;
import engine.graph.Camera;
import engine.graph.Renderer;
import engine.graph.lights.DirectionalLight;
import engine.items.GameItem;
import engine.sound.SoundManager;
import engine.testGame.Hud;
import game.items.Food;
import game.items.SnakeHead;
import game.items.SnakeTail;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jake stanger
 * Main game class for 3D snake.
 */
public class Game implements IGameLogic
{
	private static final float MOUSE_SENSITIVITY = 0.2f, CAMERA_POS_STEP = 0.1f;
	
	public static final int GRID_SIZE = 2;
	public static final float SNAKE_HEAD_SCALE = 1f, SNAKE_TAIL_SCALE = 0.9f, FOOD_SCALE = 0.7f;
	
	private static final int MAX_SNAKE_LENGTH = 256, DEFAULT_SNAKE_TAIL_LENGTH = 2;
	
	/**
	 * The number of units the map stretches in each direction
	 */
	public static final int MAP_SIZE = 10;
	
	private final Renderer renderer;
	private final SoundManager soundManager;
	private final Camera camera;
	
	private Scene scene;
	private Hud hud;
	
	private Vector3f cameraDelta;
	
	private List<GameItem> gameItems;
	
	public static SnakeHead snakeHead;
	public static Food food;
	
	private static int score;
	private static boolean running;
	
	public Game()
	{
		this.renderer = new Renderer();
		this.soundManager = new SoundManager();
		this.camera = new Camera();
		
		this.cameraDelta = new Vector3f(0, 0, 0);
	}
	
	@Override
	public void init(Window window) throws Exception
	{
		this.renderer.init(window);
		this.soundManager.init();
		
		this.scene = new Scene();
		this.scene.setSceneLight(new SceneLight());
		this.scene.getSceneLight().setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
		this.scene.getSceneLight().setDirectionalLight(new DirectionalLight(new Vector3f(0.9f, 0.2f, 0.8f), new Vector3f(0, 1, 1), 1f));
		
		this.gameItems = new ArrayList<>();
		
		snakeHead = new SnakeHead(new Vector3f(0, 0, -3));
		snakeHead.setScale(SNAKE_HEAD_SCALE);
		
		food = new Food();
		food.setScale(FOOD_SCALE);
		
		this.gameItems.add(snakeHead);
		this.gameItems.add(food);
		
		for(int i = 0; i < MAX_SNAKE_LENGTH; i++)
		{
			SnakeTail tail = new SnakeTail(i);
			tail.setPosition(0, 0, (-3+(i+1)* GRID_SIZE));
			
			if(i-1 < DEFAULT_SNAKE_TAIL_LENGTH)
			{
				tail.setVisible(true);
				tail.setScale(SNAKE_TAIL_SCALE);
			}
			else
			{
				tail.setVisible(false);
				tail.setScale(0); //Invisible effect
			}
			
			this.gameItems.add(tail);
			snakeHead.addTailToList(tail);
		}
		
		this.scene.setGameItems(this.gameItems);
		this.hud = new Hud("Test");
		
		running = true;
	}
	
	@Override
	public void input(Window window, MouseInput mouseInput)
	{
		this.moveCamera(window, mouseInput);
		snakeHead.input(window, mouseInput);
	}
	
	private void moveCamera(Window window, MouseInput mouseInput)
	{
		this.cameraDelta.set(0, 0, 0);
		
		if (window.isKeyPressed(GLFW_KEY_W)) this.cameraDelta.z = -1;
		else if (window.isKeyPressed(GLFW_KEY_S)) this.cameraDelta.z = 1;
		
		if (window.isKeyPressed(GLFW_KEY_A)) this.cameraDelta.x = -1;
		else if (window.isKeyPressed(GLFW_KEY_D)) this.cameraDelta.x = 1;
		
		if (window.isKeyPressed(GLFW_KEY_Z)) this.cameraDelta.y = -1;
		else if (window.isKeyPressed(GLFW_KEY_X)) this.cameraDelta.y = 1;
		
	}
	
	@Override
	public void update(float interval, MouseInput mouseInput)
	{
		//Update camera based on mouse
		if (mouseInput.isRightButtonPressed())
		{
			Vector2f rotVec = mouseInput.getDisplVec();
			this.camera.moveRotation(rotVec.x * Game.MOUSE_SENSITIVITY, rotVec.y * Game.MOUSE_SENSITIVITY, 0);
		}
		
		//Update camera position
		this.camera.movePosition(this.cameraDelta.x * CAMERA_POS_STEP, this.cameraDelta.y * CAMERA_POS_STEP, this.cameraDelta.z * CAMERA_POS_STEP);
		
		if (isRunning())
		{
			snakeHead.update();
			snakeHead.getTailList().forEach(SnakeTail::update);
			
			food.update();
		}
	}
	
	/**
	 * Increase the score by one.
	 */
	public static void incrementScore()
	{
		score++;
	}
	
	public static boolean isRunning()
	{
		return running;
	}
	
	public static void setRunning(boolean running)
	{
		Game.running = running;
	}
	
	@Override
	public void render(Window window)
	{
		this.renderer.render(window, camera, scene, hud);
	}
	
	@Override
	public void cleanup()
	{
		this.renderer.cleanup();
		this.soundManager.cleanup();
	}
}
