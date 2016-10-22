package engine;

import engine.items.GameItem;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public interface IHud
{
	GameItem[] getGameItems();
	
	default void cleanup()
	{
		GameItem[] gameItems = getGameItems();
		for(GameItem gameItem : gameItems) gameItem.getMesh().cleanup();
	}
}
