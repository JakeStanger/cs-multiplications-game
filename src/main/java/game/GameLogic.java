package game;

import engine.IGameLogic;
import engine.MouseInput;
import engine.Window;
import engine.items.GameItem;
import game.scenes.Game;
import game.scenes.IScene;
import game.scenes.Menu;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jake stanger
 * A wrapper class for IScene instances.
 */
public class GameLogic implements IGameLogic
{
	private IScene scene;
	
	private List<GameItem> gameItemList;
	private Vector3f cameraPos;
	private int score;
	
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
		//Backup game items
		if(this.scene instanceof Game && !(scene instanceof Menu))
		{
			this.gameItemList = new ArrayList<>(((Game) this.scene).getGameItems());
			this.score = ((Game)this.scene).getScore();
			this.cameraPos = new Vector3f(((Game)this.scene).getCamera().getPosition());
		}
		
		this.cleanup();
		this.scene = scene;
		
		if(!(scene instanceof Game)) this.init(window);
		else
		{
			if(this.gameItemList != null) ((Game)this.scene).init(window, this.gameItemList, this.score, this.cameraPos);
			else this.init(window);
		}
	}
}
