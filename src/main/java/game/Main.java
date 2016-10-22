package game;

import engine.GameEngine;
import engine.IGameLogic;

/**
 * @author Jake stanger
 * Main class for game. Starts the engine and the game
 */
public class Main
{
	private static final int WINDOW_WIDTH = 1280, WINDOW_HEIGHT = 720;
	private static final String WINDOW_TITLE = "CS Multiplication Game";
	
	public static void main(String[] args)
	{
		try
		{
			boolean vSync = true; //Enable vsync
			
			engine.Window.WindowOptions opts = new engine.Window.WindowOptions();
			
			opts.cullFace = true;
			opts.showFps = true;
			opts.compatibleProfile = false;
			
			IGameLogic gameLogic = new Game(); //Create game
			GameEngine gameEngine = new GameEngine(WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT, vSync, opts, gameLogic);
			gameEngine.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
}