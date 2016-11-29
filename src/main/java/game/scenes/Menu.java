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
public class Menu implements IScene
{
	private static final int MENU_COOLDOWN_TIME = 10;
	
	private static final int PLAY_ID = 0;
	private static final int LEADERBOARDS_ID = 1;
	private static final int OPTIONS_ID = 2;
	private static final int QUIT_ID = 3;
	
	private List<GameItem> menuItems;
	
	private final Renderer renderer;
	private final SoundManager soundManager;
	private final Camera camera;
	
	private Scene scene;
	private static Hud hud;
	
	private int selectedOption = 0;
	private int menuCooldown = MENU_COOLDOWN_TIME;
	
	public Menu()
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
		
		menuItems = new ArrayList<>();
		
		menuItems.add(new MenuButton("play"));
		menuItems.add(new MenuButton("leaderboards"));
		menuItems.add(new MenuButton("options"));
		menuItems.add(new MenuButton("quit"));
		
		menuItems.get(0).setSelected(true);
		
		for(int i = 0; i < menuItems.size(); i++)
		{
			menuItems.get(i).getRotation()
					.rotateX((float)Math.toRadians(90f));
			menuItems.get(i).setPosition(-5, 8-i*2, -18);
		}
		
		this.setupLighting();
		this.scene.setGameItems(menuItems);
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
		if(this.selectedOption == this.menuItems.size()) this.selectedOption = 0;
		
		this.updateSelected();
	}
	
	private void selectPrevOption()
	{
		this.selectedOption--;
		if(this.selectedOption == -1) this.selectedOption = this.menuItems.size()-1;
		
		this.updateSelected();
	}
	
	private void updateSelected()
	{
		for(int i = 0; i < this.menuItems.size(); i++)
		{
			MenuButton button = (MenuButton) this.menuItems.get(i);
			
			if(i == this.selectedOption) button.setSelected(true);
			else button.setSelected(false);
		}
	}
	
	private void triggerOption(Window window)
	{
		try
		{
			switch (this.selectedOption)
			{
				case PLAY_ID:
					((GameLogic) Main.getGameLogic()).setScene(new Game(), window);
					break;
				case LEADERBOARDS_ID:
					break; //TODO Switch to leaderboards view
				case OPTIONS_ID:
					break; //TODO Switch to options view
				case QUIT_ID:
					this.cleanup();
					System.exit(0);
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
			
			this.menuCooldown = Menu.MENU_COOLDOWN_TIME;
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
