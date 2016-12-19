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
import game.GameLogic;
import game.Hud;
import game.Main;
import game.items.MenuButton;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class Options implements IScene
{
	private static final Material MATERIAL = new Material(new Vector3f(0.6f, 0, 0.8f), 1);
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	private static final float Z_LEVEL = -5;
	
	private static final int MENU_COOLDOWN_TIME = 7;
	
	private List<List<GameItem>> gameItems;
	
	private final Renderer renderer;
	private final SoundManager soundManager;
	private final Camera camera;
	
	private Scene scene;
	private static Hud hud;
	
	private int selectedID = 0, selectedOption = 0;
	private int menuCooldown = MENU_COOLDOWN_TIME;
	
	public Options()
	{
		this.renderer = new Renderer();
		this.soundManager = new SoundManager();
		this.camera = new Camera();
	}
	
	@Override
	public void init(Window window) throws Exception
	{
		this.renderer.init(window);
		this.soundManager.init();
		
		this.scene = new Scene();
		
		this.gameItems = new ArrayList<>();
		
		Mesh a = OBJLoader.loadMesh("/models/chars/a.obj");
		a.setMaterial(MATERIAL);
		
		//--Username digits--
		for(int i = 0; i < 3; i++)
		{
			List<GameItem> digitList = new ArrayList<>();
			for(char c : ALPHABET.toCharArray())
			{
				Mesh mesh = OBJLoader.loadMesh("/models/chars/" + c + ".obj");
				mesh.setMaterial(MATERIAL);
				MenuButton digit = new MenuButton();
				digit.setMesh(mesh);
				digit.setPosition(-4.5f+i, 2, Z_LEVEL);
				if(c != 'a') digit.setScale(0);
				digitList.add(digit);
			}
			this.gameItems.add(digitList);
		}
		
		//--Save button
		List<GameItem> charList = new ArrayList<>();
		final String text = "save";
		for(int i = 0; i < text.length(); i++)
		{
			Mesh mesh = OBJLoader.loadMesh("/models/chars/" + text.toCharArray()[i] + ".obj");
			mesh.setMaterial(MATERIAL);
			MenuButton digit = new MenuButton();
			digit.setMesh(mesh);
			digit.setPosition(-4.5f+i, -2, Z_LEVEL);
			charList.add(digit);
		}
		this.gameItems.add(charList);
		
		//Select first option
		for (GameItem gameItem : this.gameItems.get(0)) gameItem.setSelected(true);
		
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
		
	}
	
	@Override
	public void input(Window window, MouseInput mouseInput)
	{
		if(this.menuCooldown > 0) this.menuCooldown--;
		
		if(this.menuCooldown == 0)
		{
			int prevId = this.selectedID;
			int prevSelected = this.selectedOption;
			
			//Option selection
			if (window.isKeyPressed(GLFW_KEY_RIGHT)) this.selectedOption++;
			else if (window.isKeyPressed(GLFW_KEY_LEFT)) this.selectedOption--;
			
			//Wrap around
			if (selectedOption == gameItems.size()) selectedOption = 0;
			if (selectedOption == -1) selectedOption = gameItems.size() - 1;
			
			//Update selected option
			if (this.selectedOption != prevSelected)
				for (int i = 0; i < gameItems.size(); i++)
					if (i == this.selectedOption) for (GameItem gameItem : gameItems.get(i)) gameItem.setSelected(true);
					else for (GameItem gameItem : gameItems.get(i)) gameItem.setSelected(false);
			
			//Option cycle
			if (window.isKeyPressed(GLFW_KEY_UP)) this.selectedID--;
			else if (window.isKeyPressed(GLFW_KEY_DOWN)) this.selectedID++;
			
			//Wrap around
			if (selectedID == ALPHABET.length()) selectedID = 0;
			if (selectedID == -1) selectedID = ALPHABET.length() - 1;
			
			//Update cycled item
			if (this.selectedID != prevId)
				for (int i = 0; i < ALPHABET.length(); i++)
					if (i == this.selectedID) this.gameItems.get(selectedOption).get(i).setScale(1);
					else this.gameItems.get(selectedOption).get(i).setScale(0);
			
			//Quit to menu
			if (window.isKeyPressed(GLFW_KEY_Q) || window.isKeyPressed(GLFW_KEY_ESCAPE)) try
			{
				((GameLogic) Main.getGameLogic()).setScene(new Menu(), window);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			this.menuCooldown = MENU_COOLDOWN_TIME;
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
