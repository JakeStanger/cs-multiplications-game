package game;

import engine.IGameLogic;
import engine.MouseInput;
import engine.Window;
import game.scenes.IScene;

/**
 * @author Jake stanger
 * A wrapper class for IScene instances.
 */
public class GameLogic implements IGameLogic
{
	private IScene scene;
	
	public GameLogic(IScene scene)
	{
		this.scene = scene;
	}
	
	@Override
	public void init(Window window) throws Exception
	{
		this.scene.init(window);
	}
	
	@Override
	public void input(Window window, MouseInput mouseInput)
	{
		this.scene.input(window, mouseInput);
	}
	
	@Override
	public void update(float interval, MouseInput mouseInput)
	{
		this.scene.update(interval, mouseInput);
	}
	
	@Override
	public void render(Window window)
	{
		this.scene.render(window);
	}
	
	@Override
	public void cleanup()
	{
		this.scene.cleanup();
	}
	
	public void setScene(IScene scene, Window window) throws Exception
	{
		this.cleanup();
		this.scene = scene;
		this.init(window);
	}
}
