package game.scenes;

import engine.MouseInput;
import engine.Scene;
import engine.SceneLight;
import engine.Window;
import engine.graph.Camera;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Renderer;
import engine.graph.lights.DirectionalLight;
import engine.items.GameItem;
import engine.loaders.obj.OBJLoader;
import engine.sound.SoundManager;
import game.utils.Database;
import game.GameLogic;
import game.Hud;
import game.Main;
import game.wrappers.LeaderboardEntry;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jake stanger
 * The leaderboards scene
 */
public class Leaderboard implements IScene
{
	private static final Material MATERIAL = new Material(new Vector3f(0.6f, 0, 0.8f), 1);
	private static final float NAME_X = -3.5f, SCORE_X = 1, START_Y = 2, Z_LEVEL = -5;
	private static final float CAMERA_ACCELERATION = 0.05f, MAX_DELTA = 10, MAX_DELTA_FAST = 30;
	
	private List<List<GameItem>> gameItems;
	
	private final Renderer renderer;
	private final SoundManager soundManager;
	private final Camera camera;
	
	private Scene scene;
	private static Hud hud;
	
	private Vector3f cameraDelta;
	
	private int height;
	
	public Leaderboard()
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
		
		this.gameItems = new ArrayList<>();
		
		List<LeaderboardEntry> entries = Database.readAllEntries();
		
		if (entries != null)
		{
			for(int i = 0; i < entries.size(); i++)
			{
				String name = entries.get(i).getName();
				
				String score = Integer.toString(entries.get(i).getScore());
				while(score.length() < 3) score = "0" + score; //Make sure score length = 3
				
				List<GameItem> rowItems = new ArrayList<>();
				
				//Build name
				for(int j = 0; j < name.toCharArray().length; j++)
				{
					Mesh mesh = OBJLoader.loadMesh("/models/chars/" + name.toCharArray()[j] + ".obj");
					mesh.setMaterial(MATERIAL);
					
					GameItem gameItem = new GameItem(mesh);
					gameItem.setPosition(NAME_X + j, START_Y-i, Z_LEVEL);
					rowItems.add(gameItem);
				}
				
				//Build score
				for(int j = 0; j < score.toCharArray().length; j++)
				{
					Mesh mesh = OBJLoader.loadMesh("/models/chars/" + score.toCharArray()[j] + ".obj");
					mesh.setMaterial(MATERIAL);
					
					GameItem gameItem = new GameItem(mesh);
					gameItem.setPosition(SCORE_X + j, START_Y-i, Z_LEVEL);
					rowItems.add(gameItem);
				}
				
				this.gameItems.add(rowItems);
			}
			this.height = entries.size()-5;
		}
		
		//Rotate all to be right way up
		for(List<GameItem> gameItems : this.gameItems)
			for(GameItem gameItem : gameItems) gameItem.getRotation().rotateX((float) Math.toRadians(90));
		
		this.setupLighting();
		
		List<GameItem> sceneGameItems = new ArrayList<>();
		this.gameItems.forEach(sceneGameItems::addAll);
		this.scene.setGameItems(sceneGameItems);
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
	}
	
	@Override
	public void update(float interval, MouseInput mouseInput)
	{
		//Update camera position
		this.camera.movePosition(0, this.cameraDelta.y * 0.01f, 0);
	}
	
	@Override
	public void input(Window window, MouseInput mouseInput)
	{
		//Camera up
		if ((window.isKeyPressed(GLFW_KEY_W) || window.isKeyPressed(GLFW_KEY_UP))
				&& camera.getPosition().y < START_Y)
		{
			if(cameraDelta.y > 0) cameraDelta.y += CAMERA_ACCELERATION;
			else cameraDelta.y = CAMERA_ACCELERATION;
		}
		//Camera down
		else if((window.isKeyPressed(GLFW_KEY_S) || window.isKeyPressed(GLFW_KEY_DOWN))
				&& camera.getPosition().y > -height)
		{
			if(cameraDelta.y < 0) cameraDelta.y -= CAMERA_ACCELERATION;
			else cameraDelta.y = -CAMERA_ACCELERATION;
		}
		else //Reduce camera speed when controls released
		{
			if(this.cameraDelta.y < 0) this.cameraDelta.y += CAMERA_ACCELERATION *3;
			if(this.cameraDelta.y > 0) this.cameraDelta.y -= CAMERA_ACCELERATION *3;
			
			//Set to 0 if slow enough
			if(this.cameraDelta.y > 0 &&  cameraDelta.y < CAMERA_ACCELERATION
				|| this.cameraDelta.y < 0 &&  cameraDelta.y > -CAMERA_ACCELERATION)
				this.cameraDelta.y = 0;
		}
		
		//Cap speed
		if(!(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)))
		{
			if(cameraDelta.y > MAX_DELTA) cameraDelta.y = MAX_DELTA;
			else if(cameraDelta.y < -MAX_DELTA) cameraDelta.y = -MAX_DELTA;
		}
		else //Fast mode
		{
			if(cameraDelta.y > MAX_DELTA_FAST) cameraDelta.y = MAX_DELTA_FAST;
			else if(cameraDelta.y < -MAX_DELTA_FAST) cameraDelta.y = -MAX_DELTA_FAST;
		}
		
		//Stop camera from going past end of list
		if((camera.getPosition().y > START_Y && cameraDelta.y > 0) || (camera.getPosition().y < -height && cameraDelta.y < 0))
			cameraDelta.y = 0;
		
		//Quit to menu
		if(window.isKeyPressed(GLFW_KEY_Q) || window.isKeyPressed(GLFW_KEY_ESCAPE)) try
		{
			((GameLogic) Main.getGameLogic()).setScene(new Menu(), window);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
