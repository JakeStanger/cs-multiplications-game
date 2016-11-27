package game;

import engine.IHud;
import engine.Utils;
import engine.Window;
import engine.graph.hud.TextLabel;
import engine.items.GameItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jake stanger
 * Responsible for all of the game's HUD components
 */
public class Hud implements IHud
{
	private List<GameItem> hudItems;
	
	private TextLabel scoreLabel;
	
	Hud() throws Exception
	{
		this.hudItems = new ArrayList<>();
		
		this.scoreLabel = new TextLabel(10, 10, "Score: 0");
		this.hudItems.add(scoreLabel.getTextItem());
	}
	
	@Override
	public GameItem[] getGameItems()
	{
		return Utils.gameItemListToArray(hudItems);
	}
	
	void updateSize(Window window)
	{
		for(GameItem gameItem : this.hudItems)
			gameItem.setPosition(gameItem.getPosition().x, gameItem.getPosition().y, 0);
	}
	
	public TextLabel getScoreLabel()
	{
		return scoreLabel;
	}
}