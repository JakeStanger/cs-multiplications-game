package game;

import engine.items.GameItem;
import game.items.MenuButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class Menu
{
	private List<GameItem> menuItems;
	private int highlightedOption;
	
	public Menu() throws Exception
	{
		menuItems = new ArrayList<>();
		
		menuItems.add(new MenuButton("play"));
		menuItems.add(new MenuButton("leaderboards"));
		menuItems.add(new MenuButton("options"));
		menuItems.add(new MenuButton("quit"));
	}
}
