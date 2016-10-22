package engine;

/**
 * Main game engine class.
 * Contains initialisation phase
 * as well as the main game loop and render calls.
 */
public class GameEngine implements Runnable
{
	private static final int TARGET_FPS = 60, TARGET_UPS = 60;
	
	private final Window window;
	private final Thread gameLoopThread;
	private final Timer timer;
	private final IGameLogic gameLogic;
	private final MouseInput mouseInput;
	
	private double lastFps;
	private int fps;
	private String windowTitle;
	
	public GameEngine(String windowTitle, int width, int height, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic) throws Exception
	{
		this.windowTitle = windowTitle;
		gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
		window = new Window(windowTitle, width, height, vSync, opts);
		this.gameLogic = gameLogic;
		timer = new Timer();
		this.mouseInput = new MouseInput();
	}
	
	public void start()
	{
		String osName = System.getProperty("os.name");
		if (osName.contains("Mac")) gameLoopThread.run();
		else gameLoopThread.start();
	}
	
	@Override
	public void run()
	{
		try
		{
			this.init();
			this.gameLoop();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			this.cleanup();
		}
	}
	
	protected void init() throws Exception
	{
		window.init();
		timer.init();
		gameLogic.init(window);
		mouseInput.init(window);
		
		this.lastFps = timer.getTime();
		this.fps = 0;
	}
	
	/**
	 * The main game loop.
	 */
	protected void gameLoop()
	{
		//Initialise counters
		float elapsedTime;
		float accumulator = 0f;
		float interval = 1f / TARGET_UPS;
		
		boolean running = true;
		while(running && !window.windowShouldClose())
		{
			elapsedTime = timer.getElapsedTime();
			accumulator += elapsedTime;
			
			input();
			
			while(accumulator >= interval)
			{
				update(interval);
				accumulator -= interval;
			}
			
			render();
			
			if(!window.isvSync()) sync();
		}
	}
	
	private void sync()
	{
		float loopSlot = 1f / TARGET_FPS;
		double endTime = timer.getLastLoopTime() + loopSlot;
		
		while(timer.getTime() < endTime)
		{
			try
			{
				Thread.sleep(1);
			}
			catch(InterruptedException e){}
		}
	}
	
	protected void input()
	{
		mouseInput.input(window);
		gameLogic.input(window, mouseInput);
	}
	
	protected void update(float interval)
	{
		gameLogic.update(interval, mouseInput);
	}
	
	protected void render()
	{
		if(window.getWindowOptions().showFps && timer.getLastLoopTime() - lastFps > 1)
		{
			lastFps = timer.getLastLoopTime();
			window.setWindowTitle(windowTitle + " - " + fps + " FPS");
		}
		
		gameLogic.render(window);
		window.update();
	}
	
	protected void cleanup()
	{
		gameLogic.cleanup();
	}
}