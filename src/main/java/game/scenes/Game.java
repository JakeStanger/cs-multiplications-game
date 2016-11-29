package game.scenes;

import engine.*;
import engine.graph.Camera;
import engine.graph.Renderer;
import engine.graph.lights.DirectionalLight;
import engine.graph.lights.PointLight;
import engine.graph.lights.SpotLight;
import engine.items.GameItem;
import engine.sound.SoundManager;
import game.Hud;
import game.items.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jake stanger
 * Main game class for 3D snake.
 */
public class Game implements IScene
{
	private static final float MOUSE_SENSITIVITY = 0.2f, CAMERA_POS_STEP = 0.1f;
	
	public static final int GRID_SIZE = 2;
	public static final float SNAKE_HEAD_SCALE = 1f, SNAKE_TAIL_SCALE = 0.9f, FOOD_SCALE = 0.7f;
	
	private static final int MAX_SNAKE_LENGTH = 256, DEFAULT_SNAKE_TAIL_LENGTH = 2;
	
	/**
	 * The number of units the map stretches in each direction
	 */
	public static final int MAP_SIZE = 30;
	
	private final Renderer renderer;
	private final SoundManager soundManager;
	private final Camera camera;
	
	private Scene scene;
	private static Hud hud;
	
	private Vector3f cameraDelta;
	
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
		
		List<GameItem> gameItems = new ArrayList<>();
		
		snakeHead = new SnakeHead(new Vector3f(MAP_SIZE/2, MAP_SIZE/2, -MAP_SIZE/2));
		snakeHead.setScale(SNAKE_HEAD_SCALE);
		
		food = new Food();
		food.setScale(FOOD_SCALE);
		
		gameItems.add(snakeHead);
		gameItems.add(food);
		//gameItems.add(new MenuButton("play.obj"));
		
		//Add snake tails
		for(int i = 0; i < MAX_SNAKE_LENGTH; i++)
		{
			SnakeTail tail = new SnakeTail(i+1);
			tail.setPosition(MAP_SIZE/2, MAP_SIZE/2, ((-MAP_SIZE/2)+(i+1)* GRID_SIZE));
			
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
			
			gameItems.add(tail);
			snakeHead.addTailToList(tail);
		}
		//Add walls
		Wall wall = new Wall();
		gameItems.add(wall);
		
		//Lighting
		this.setupLighting();
		
		this.scene.setGameItems(gameItems);
		hud = new Hud();
		
		running = true;
	}
	
	private void setupLighting()
	{
		final Vector3f ambientIntensity = new Vector3f(0.3f, 0.3f, 0.3f);
		
		final Vector3f directionalColour = new Vector3f(1, 1, 1);
		final Vector3f directionalDirection = new Vector3f(0, 1, 1);
		final float directionalIntensity = 0.4f;
		
		this.scene.setSceneLight(new SceneLight());
		this.scene.getSceneLight().setAmbientLight(ambientIntensity);
		this.scene.getSceneLight().setSkyBoxLight(ambientIntensity);
		this.scene.getSceneLight().setDirectionalLight(new DirectionalLight(directionalColour, directionalDirection, directionalIntensity));
		
		final Vector3f foodLightColour = new Vector3f(0.7f, 0.7f, 0.3f);
		final float foodLightIntensity = 2f;
		PointLight foodLight = new PointLight(foodLightColour, food.getPosition(), foodLightIntensity);
		
		final Vector3f headLightColour = new Vector3f(0.7f, 0.7f, 0.7f);
		final float headLightIntensity = 1f;
		final float headLightAngle = 70;
		
		PointLight headLightPoint = new PointLight(headLightColour, snakeHead.getPosition(), headLightIntensity);
		Vector3f rotation = new Vector3f(snakeHead.getRotation().x, snakeHead.getRotation().y, snakeHead.getRotation().z);
		SpotLight headLight = new SpotLight(headLightPoint, rotation, headLightAngle);
		
		this.scene.getSceneLight().setPointLights(new PointLight[]{foodLight});
		this.scene.getSceneLight().setSpotLights(new SpotLight[]{headLight});
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
		if(mouseInput.isRightButtonPressed())
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
		hud.getScoreLabel().setText("Score: " + score);
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
		if (hud != null) hud.updateSize(window);
		this.renderer.render(window, camera, scene, hud);
	}
	
	@Override
	public void cleanup()
	{
		this.renderer.cleanup();
		this.soundManager.cleanup();
		
		if (hud != null) hud.cleanup();
	}
}
