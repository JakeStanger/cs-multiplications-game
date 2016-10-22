package engine.testGame;


import engine.GameEngine;
import engine.IGameLogic;
import engine.Window;

import java.awt.*;

/**
 * Main project class. Starts the game engine and the game.
 */
/*public class Main
{
	public static void main(String[] args)
	{
		try
		{
			boolean vSync = true; //Enable vsync
			
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			Window.WindowOptions opts = new Window.WindowOptions();
			
			opts.cullFace = true;
			opts.showFps = true;
			opts.compatibleProfile = false;
			
			IGameLogic gameLogic = new DummyGame(); //Create game
			GameEngine gameEngine = new GameEngine("CS Multiplication Game", 1280, 720, vSync, opts, gameLogic);
			gameEngine.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
}*/