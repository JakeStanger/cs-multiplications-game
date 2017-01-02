package game.scenes;

import engine.MouseInput;
import engine.Scene;
import engine.SceneLight;
import engine.Window;
import engine.graph.Camera;
import engine.graph.Renderer;
import engine.graph.lights.DirectionalLight;
import engine.graph.lights.PointLight;
import engine.graph.lights.SpotLight;
import engine.items.GameItem;
import engine.sound.SoundBuffer;
import engine.sound.SoundManager;
import engine.sound.SoundSource;
import game.enums.Direction;
import game.enums.Sound;
import game.items.*;
import game.utils.Database;
import game.GameLogic;
import game.Hud;
import game.Main;
import game.wrappers.LeaderboardEntry;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL11;

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
	private static final SoundManager soundManager = new SoundManager();
	private final Camera camera;
	
	private Scene scene;
	private static Hud hud;
	
	private Vector3f cameraDelta;
	
	public static SnakeHead snakeHead;
	public static Food food;
	
	private static int score;
	private static boolean running;
	
	private List<GameItem> gameItems;
	
	public Game()
	{
		this.renderer = new Renderer();
		this.camera = new Camera();
		
		this.cameraDelta = new Vector3f(0, 0, 0);
	}
	
	@Override
	public void init(Window window) throws Exception
	{
		gameItems = new ArrayList<>();
		
		snakeHead = new SnakeHead(new Vector3f(MAP_SIZE/2, MAP_SIZE/2, -MAP_SIZE/2));
		snakeHead.setScale(SNAKE_HEAD_SCALE);
		
		food = new Food();
		food.setScale(FOOD_SCALE);
		
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
		
		this.init(window, gameItems, 0, new Vector3f(MAP_SIZE/2, MAP_SIZE/2 + 10, -MAP_SIZE/2 + 10));
	}
	
	public void init(Window window, List<GameItem> gameItems, int score, Vector3f cameraPos) throws Exception
	{
		this.renderer.init(window);
		soundManager.init();
		
		this.scene = new Scene();
		
		this.camera.setPosition(cameraPos.x, cameraPos.y, cameraPos.z);
		
 		gameItems.add(snakeHead);
		gameItems.add(food);
		
		this.setupLighting();
		this.scene.setGameItems(gameItems);
		this.gameItems = gameItems;
		
		soundManager.init();
		soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
		this.setupSounds();
		
		hud = new Hud();
		this.setScore(score);
		
		Game.setRunning(true);
	}
	
	private void setupSounds() throws Exception
	{
		//Boop
		SoundBuffer bufferBoop = new SoundBuffer("/sounds/boop.ogg");
		soundManager.addSoundBuffer(bufferBoop);
		SoundSource sourceBoop = new SoundSource(false, false);
		sourceBoop.setBuffer(bufferBoop.getBufferID());
		soundManager.addSoundSource(Sound.BOOP.toString(), sourceBoop);
		
		//Boop high
		SoundBuffer bufferBoopHigh = new SoundBuffer("/sounds/boop_high.ogg");
		soundManager.addSoundBuffer(bufferBoopHigh);
		SoundSource sourceBoopHigh = new SoundSource(false, false);
		sourceBoopHigh.setBuffer(bufferBoopHigh.getBufferID());
		soundManager.addSoundSource(Sound.BOOP_HIGH.toString(), sourceBoopHigh);
		
		//Game over
		SoundBuffer bufferGameOver = new SoundBuffer("/sounds/game_over.ogg");
		soundManager.addSoundBuffer(bufferGameOver);
		SoundSource sourceGameOver = new SoundSource(false, false);
		sourceGameOver.setBuffer(bufferGameOver.getBufferID());
		soundManager.addSoundSource(Sound.GAME_OVER.toString(), sourceGameOver);
		
		//Music
		SoundBuffer bufferMusic = new SoundBuffer("/sounds/wagon_wheel.ogg");
		soundManager.addSoundBuffer(bufferMusic);
		SoundSource sourceMusic = new SoundSource(true, false);
		sourceMusic.setBuffer(bufferMusic.getBufferID());
		soundManager.addSoundSource(Sound.GAME_MUSIC.toString(), sourceMusic);
		soundManager.playSoundSource(Sound.GAME_MUSIC.toString());
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
		snakeHead.input(window, mouseInput, soundManager);
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
		//Stop camera from going upside-down
		if(this.camera.getRotation().x > 90) this.camera.getRotation().x = 90;
		if(this.camera.getRotation().x < -90) this.camera.getRotation().x = -90;
		
		//--Update camera position--
		//Move according to input
		this.camera.movePosition(
				this.cameraDelta.x * CAMERA_POS_STEP,
				this.cameraDelta.y * CAMERA_POS_STEP,
				this.cameraDelta.z * CAMERA_POS_STEP);
		
		//Move with snake
		Direction dir = snakeHead.getDirection();
		float step = SnakeHead.SNAKE_STEP;
		this.camera.movePosition(
				dir == Direction.LEFT ? -step : dir == Direction.RIGHT ? step : 0,
				dir == Direction.UP ? step : dir == Direction.DOWN ? -step : 0,
				dir == Direction.FORWARDS ? -step : dir == Direction.BACKWARDS ? step : 0);
		
		if (isRunning())
		{
			snakeHead.update(soundManager);
			snakeHead.getTailList().forEach(SnakeTail::update);
			
			food.update();
		}
		
		//--Sounds--
		//Toggle music
		SoundSource music = soundManager.getSoundSource(Sound.GAME_MUSIC.toString());
		if(music != null)
		{
			if (music.isPlaying() && Options.Values.muteMusic) music.stop();
			else if (!music.isPlaying() && !Options.Values.muteMusic) music.play();
		}
		
		//Toggle sound effects
		SoundSource boop = soundManager.getSoundSource(Sound.BOOP.toString());
		SoundSource boopHigh = soundManager.getSoundSource(Sound.BOOP_HIGH.toString());
		SoundSource gameOver = soundManager.getSoundSource(Sound.GAME_OVER.toString());
		if(Options.Values.muteSound
				&& boop != null && boopHigh != null && gameOver != null)
		{
			if(boop.isPlaying()) boop.stop();
			if(boopHigh.isPlaying()) boopHigh.stop();
			if(gameOver.isPlaying()) gameOver.stop();
		}
	}
	
	public static void endGame()
	{
		soundManager.playSoundSource(Sound.GAME_OVER.toString());
		
		Game.setRunning(false);
		Database.addEntry(new LeaderboardEntry(new String(Options.Values.name), score));
		try
		{
			Thread.sleep(2500);
			((GameLogic)Main.getGameLogic()).setScene(new Menu(), Main.getGameEngine().getWindow());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Increase the score by one.
	 */
	static void incrementScore()
	{
		score++;
		hud.getScoreLabel().setText("Score: " + score);
	}
	
	private void setScore(int score)
	{
		Game.score = score;
		hud.getScoreLabel().setText("Score: " + score);
	}
	
	public int getScore()
	{
		return score;
	}
	
	private static boolean isRunning()
	{
		return running;
	}
	
	private static void setRunning(boolean running)
	{
		Game.running = running;
	}
	
	@Override
	public void render(Window window)
	{
		if (hud != null) hud.updateSize(window);
		this.renderer.render(window, camera, scene, hud);
	}
	
	public List<GameItem> getGameItems()
	{
		return gameItems;
	}
	
	public Camera getCamera()
	{
		return this.camera;
	}
	
	@Override
	public void cleanup()
	{
		this.renderer.cleanup();
		soundManager.cleanup();
		
		if (hud != null) hud.cleanup();
	}
}
