package game;

import engine.GameEngine;
import engine.IGameLogic;
import game.scenes.Menu;

/**
 * @author Jake stanger
 * Main class for game. Starts the engine and the game
 */
public class Main
{
	private static final int WINDOW_WIDTH = 1280, WINDOW_HEIGHT = 720;
	private static final String WINDOW_TITLE = "CS Multiplication Game";
	
	private static IGameLogic gameLogic;
	
	private static GameEngine gameEngine;
	
	public static void main(String[] args)
	{
		try
		{
			boolean vSync = true; //Enable vsync
			
			engine.Window.WindowOptions opts = new engine.Window.WindowOptions();
			
			opts.cullFace = true;
			opts.showFps = false;
			opts.compatibleProfile = false;
			
			Main.gameLogic = new GameLogic(new Menu()); //Create game
			Main.gameEngine = new GameEngine(WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT, vSync, opts, gameLogic);
			Main.gameEngine.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static IGameLogic getGameLogic()
	{
		return gameLogic;
	}
	
	public static GameEngine getGameEngine()
	{
		return gameEngine;
	}
}
