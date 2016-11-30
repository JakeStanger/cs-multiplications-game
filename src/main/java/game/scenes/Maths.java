package game.scenes;

import engine.MouseInput;
import engine.Scene;
import engine.SceneLight;
import engine.Window;
import engine.graph.Camera;
import engine.graph.Renderer;
import engine.graph.lights.DirectionalLight;
import engine.items.GameItem;
import engine.sound.SoundManager;
import game.Hud;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class Maths implements IScene
{
	private static final int MENU_COOLDOWN_TIME = 5;
	
	private static final int DIGIT_ONE_ID = 0;
	private static final int MULTIPLY_ID = 1;
	private static final int DIGIT_TWO_ID = 2;
	
	private static final int OPT_ONE_ID = 3;
	private static final int OPT_TWO_ID = 4;
	private static final int OPT_THREE_ID = 5;
	private static final int OPT_FOUR_ID = 6;
	
	private static final int TOTAL_MAJOR_COMPONENTS = 7;
	private static final int DIGIT_COMPONENTS = 2;
	private static final int MULTIPLY_COMPONENTS = 1;
	private static final int OPTION_COMPONENTS = 3;
	
	private List<List<GameItem>> gameItems;
	
	private final Renderer renderer;
	private final SoundManager soundManager;
	private final Camera camera;
	
	private Scene scene;
	private static Hud hud;
	
	private int selectedOption = 0;
	private int menuCooldown = MENU_COOLDOWN_TIME;
	
	public Maths()
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
		
		//--Add game items--
		gameItems = new ArrayList<>();
		for(int i = 0; i < TOTAL_MAJOR_COMPONENTS; i++) gameItems.add(new ArrayList<>());
		
		gameItems.get(MULTIPLY_ID).add(new GameItem());
		for(int i = 0; i < DIGIT_COMPONENTS; i++)
		{
			gameItems.get(DIGIT_ONE_ID).add(new GameItem());
			gameItems.get(DIGIT_TWO_ID).add(new GameItem());
		}
		
		for(int i = 0; i < OPTION_COMPONENTS; i++)
		{
			for(int j = OPT_ONE_ID; j < OPT_FOUR_ID+1; j++)
				gameItems.get(i).add(new GameItem());
		}
		
		this.setupLighting();
		
		//Add items to scene
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
	
	private void selectNextOption()
	{
		this.selectedOption++;
		if(this.selectedOption == OPT_FOUR_ID+1) this.selectedOption = OPT_ONE_ID;
		
		this.updateSelected();
	}
	
	private void selectPrevOption()
	{
		this.selectedOption--;
		if(this.selectedOption == OPT_ONE_ID-1) this.selectedOption = OPT_FOUR_ID;
		
		this.updateSelected();
	}
	
	private void updateSelected() //TODO Cast gameItem to button?
	{
		for(int i = OPT_ONE_ID; i < OPT_FOUR_ID+1; i++)
		{
			if(i == this.selectedOption)
				for(GameItem gameItem : this.gameItems.get(i)) gameItem.setSelected(true);
			else
				for(GameItem gameItem : this.gameItems.get(i)) gameItem.setSelected(false);
		}
	}
	
	private void triggerOption(Window window) //TODO Finish method
	{
		try
		{
			switch (this.selectedOption)
			{
				case OPT_ONE_ID:
					break;
				case OPT_TWO_ID:
					break;
				case OPT_THREE_ID:
					break;
				case OPT_FOUR_ID:
					break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
			if (window.isKeyPressed(GLFW_KEY_DOWN)) this.selectNextOption();
			else if (window.isKeyPressed(GLFW_KEY_UP)) this.selectPrevOption();
			
			if (window.isKeyPressed(GLFW_KEY_SPACE) || window.isKeyPressed(GLFW_KEY_ENTER)) this.triggerOption(window);
			
			this.menuCooldown = Maths.MENU_COOLDOWN_TIME;
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
